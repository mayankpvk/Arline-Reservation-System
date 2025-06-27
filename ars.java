import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.binding.BooleanBinding;
import javafx.stage.*;
import javafx.scene.*;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;
class AllSeatsBooked extends Exception {
    String message;
    public AllSeatsBooked(String message) {
        this.message = message;
    }
    public String toString() {
        return(message);
    }
}
class Seat {
    int position;
    String airlines_name;
    String passenger_name;
    BooleanProperty booked;
    double seatPrice;
    Calendar booking_time;
    ToggleButton ref;
    synchronized int book(Traveller traveller) {
        if (booked.get() == false) {
            passenger_name = traveller.getPassengerName();
            traveller.totalCost = traveller.totalCost + traveller.airplane.seatPrice.get();
            Platform.runLater(new Runnable() {
                public void run() {
                    booked.set(true);
                    traveller.airplane.bookedSeats.set(traveller.airplane.bookedSeats.get() + 1);
                }
            });
            booking_time = new GregorianCalendar();
            String timeOfBooking = booking_time.get(Calendar.HOUR_OF_DAY) + ":" + booking_time.get(Calendar.MINUTE)
                    + " " + booking_time.get(Calendar.DAY_OF_MONTH) + "/" + (booking_time.get(Calendar.MONTH) + 1) + "/"
                    + booking_time.get(Calendar.YEAR);
            traveller.seats_booked.add("SEAT " + position + " | " + "BOOKING TIME: " + timeOfBooking + " | " + "AIRLINE: " + airlines_name
                            + " | " + "BOOKED UNDER: " + passenger_name + " | SEAT PRICE: " + seatPrice);
            return(1);           
        } else {
            return(0);
        }
    }
    void updateSeatNo(int position) {
        this.position = position;
    }
 
    void updateAirlinesName(String airlines_name) {
        this.airlines_name = airlines_name;
    }
    Seat(double seatPrice) {
        this.seatPrice = seatPrice;
        this.booked = new SimpleBooleanProperty(false);
        this.passenger_name = "";
        this.booking_time = null;
    }
}
class Airplane {
    String name;
    String type;
    String origin, destination;
    Calendar arrival;
    Calendar departure;
    DoubleProperty seatPrice;
    IntegerProperty bookedSeats;
    Seat[] seats;
    boolean departed;
    Airplane(String name, String type, int seat_capacity, double seatPrice, String origin, String destination,
            int arrival_minute, int arrival_hour, int arrival_day, int arrival_month, int arrival_year,
            int departure_minute, int departure_hour, int departure_day, int departure_month, int departure_year) {
        this.departed = false;
        this.type = type;
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        arrival = new GregorianCalendar(arrival_year, arrival_month - 1, arrival_day);
        arrival.set(Calendar.HOUR_OF_DAY, arrival_hour);
        arrival.set(Calendar.MINUTE, arrival_minute);
        departure = new GregorianCalendar(departure_year, departure_month - 1, departure_day);
        departure.set(Calendar.HOUR_OF_DAY, departure_hour);
        departure.set(Calendar.MINUTE, departure_minute);
        this.seats = new Seat[seat_capacity];
        for (int i = 0; i < seats.length; i++) {
            seats[i] = new Seat(seatPrice);
            seats[i].updateSeatNo(i);
            seats[i].updateAirlinesName(name);
        }
        this.bookedSeats = new SimpleIntegerProperty(0);
        this.seatPrice = new SimpleDoubleProperty(seatPrice);
    }
    int countBooked() {
        int count = 0;
        for (int i = 0; i < seats.length; i++) {
            if (seats[i].booked.getValue()) {
                count++;
            }
        }
        return (count);
    }
    public String toString() {
        int bookedSeats = countBooked();
        String arrival_time = arrival.get(Calendar.HOUR_OF_DAY) + ":" + arrival.get(Calendar.MINUTE) + " "
                + arrival.get(Calendar.DAY_OF_MONTH) + "/" + (arrival.get(Calendar.MONTH) + 1) + "/"
                + arrival.get(Calendar.YEAR);
        String departure_time = departure.get(Calendar.HOUR_OF_DAY) + ":" + departure.get(Calendar.MINUTE) + " "
                + departure.get(Calendar.DAY_OF_MONTH) + "/" + (departure.get(Calendar.MONTH) + 1) + "/"
                + departure.get(Calendar.YEAR);
        return (name + " | " + "ARRIVAL TIME: " + arrival_time + " | " + "DEPARTURE TIME: " + departure_time + " | "
                + "FROM: " + origin + " | " + "TO: " + destination + " | " + "BOOKED SEATS: " + bookedSeats + "/"
                + seats.length);
    }
}
class Stack<T> {
    ArrayList<T> arr; 
    int top;
    Stack() {
        arr = new ArrayList<>();
        top = -1;
    }
    void push(T data) {
        arr.add(data);
        top = top + 1;
    }
    T pop() {
        if (!arr.isEmpty()) {
            T popped = arr.get(top);
            top = top - 1;
            return (popped);
        } else {
            throw new RuntimeException("Stack is Empty");
        }
    }
    void display() {
        for (int i = 0; i <= top; i++) {
            System.out.println(arr.get(i));
        }
    }
}
class Report {
    static Stack<Airplane> logs = new Stack<>();
    static ObservableList<Airplane> departedFlights = FXCollections.observableArrayList();
    static <T> boolean isPresent(Stack<T> stack, T element) {
        boolean present = false;
        for (int i = 0; i <= stack.top; i++) {
            if (stack.arr.get(i).equals(element)) {
                present = true;
                break;
            }
        }
        return(present);
    }
    static <T> int countElement(Stack<T> stack, T element) {
        int count = 0;
        for (int i = 0; i <= stack.top; i++) {
            if (stack.arr.get(i).equals(element)) {
                count++;
            }
        }
        return(count);
    }
    static String getFrequentDeparturePeriod() {
        if (logs.top != -1) {
            Stack<Integer> months_booked = new Stack<>();
            Stack<Integer> unique_months = new Stack<>();
            for (int i = 0; i <= logs.top; i++) {
                months_booked.push(logs.arr.get(i).departure.get(Calendar.MONTH) + 1);
                if (!isPresent(unique_months, logs.arr.get(i).departure.get(Calendar.MONTH) + 1)) {
                    unique_months.push(logs.arr.get(i).departure.get(Calendar.MONTH) + 1);
                }
            }
            int max_count = 0;
            int frequent_month = 0;
            for (int i = 0; i <= unique_months.top; i++) {
                int count = countElement(months_booked, unique_months.arr.get(i));
                if (count > max_count) {
                    max_count = count;
                    frequent_month = unique_months.arr.get(i);
                }
            }
            String month = "";
            switch (frequent_month) {
                case 1:
                    month = "January";
                    break;
                case 2:
                    month = "February";
                    break;
                case 3:
                    month = "March";
                    break;
                case 4:
                    month = "April";
                    break;
                case 5:
                    month = "May";
                    break;
                case 6:
                    month = "June";
                    break;
                case 7:
                    month = "July";
                    break;
                case 8:
                    month = "August";
                    break;
                case 9:
                    month = "September";
                    break;
                case 10:
                    month = "October";
                    break;
                case 11:
                    month = "November";
                    break;
                case 12:
                    month = "December";
                    break;
            }
            return(month);
        } else {
            return("");
        }
    }
    static String getFrequentBookingMonth() {
        if (logs.top != -1) {
            Stack<Integer> booking_months = new Stack<>();
            Stack<Integer> unique_months = new Stack<>();
            for (int i = 0; i <= logs.top; i++) {
                for (int j = 0; j < logs.arr.get(i).seats.length; j++) {
                    if (logs.arr.get(i).seats[j].booked.getValue() == true) {
                        booking_months.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.MONTH) + 1);
                        if (!isPresent(unique_months, logs.arr.get(i).seats[j].booking_time.get(Calendar.MONTH) + 1)) {
                            unique_months.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.MONTH) + 1);
                        }
                    }
                }
            }
            int max_count = 0;
            int frequent_month = 0;
            for (int i = 0; i <= unique_months.top; i++) {
                int count = countElement(booking_months, unique_months.arr.get(i));
                if (count > max_count) {
                    max_count = count;
                    frequent_month = unique_months.arr.get(i);
                }
            }
            String month = "";
            switch (frequent_month) {
                case 1:
                    month = "January";
                    break;
                case 2:
                    month = "February";
                    break;
                case 3:
                    month = "March";
                    break;
                case 4:
                    month = "April";
                    break;
                case 5:
                    month = "May";
                    break;
                case 6:
                    month = "June";
                    break;
                case 7:
                    month = "July";
                    break;
                case 8:
                    month = "August";
                    break;
                case 9:
                    month = "September";
                    break;
                case 10:
                    month = "October";
                    break;
                case 11:
                    month = "November";
                    break;
                case 12:
                    month = "December";
                    break;
            }
            return(month);
        } else {
            return("");
        }
    }
    static String getFrequentBookingYear() {
        if (logs.top != -1) {
            Stack<Integer> booking_years = new Stack<>();
            Stack<Integer> unique_years = new Stack<>();
            for (int i = 0; i <= logs.top; i++) {
                for (int j = 0; j < logs.arr.get(i).seats.length; j++) {
                    if (logs.arr.get(i).seats[j].booked.getValue() == true) {
                        booking_years.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.YEAR));
                        if (!isPresent(unique_years, logs.arr.get(i).seats[j].booking_time.get(Calendar.YEAR))) {
                            unique_years.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.YEAR));
                        }
                    }
                }
            }
            int max_count = 0;
            int frequent_year = 0;
            for (int i = 0; i <= unique_years.top; i++) {
                int count = countElement(booking_years, unique_years.arr.get(i));
                if (count > max_count) {
                    max_count = count;
                    frequent_year = unique_years.arr.get(i);
                }
            }
            return(frequent_year + "");
        } else {
            return("");
        }
    }
    static String getFrequentBookingDay() {
        if (logs.top != -1) {
            Stack<Integer> booking_days = new Stack<>();
            Stack<Integer> unique_days = new Stack<>();
            for (int i = 0; i <= logs.top; i++) {
                for (int j = 0; j < logs.arr.get(i).seats.length; j++) {
                    if (logs.arr.get(i).seats[j].booked.getValue() == true) {
                        booking_days.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.DAY_OF_WEEK));
                        if (!isPresent(unique_days, logs.arr.get(i).seats[j].booking_time.get(Calendar.DAY_OF_WEEK))) {
                            unique_days.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.DAY_OF_WEEK));
                        }
                    }
                }
            }
            int max_count = 0;
            int frequent_days = 0;
            for (int i = 0; i <= unique_days.top; i++) {
                int count = countElement(booking_days, unique_days.arr.get(i));
                if (count > max_count) {
                    max_count = count;
                    frequent_days = unique_days.arr.get(i);
                }
            }
            String day = "";
            switch (frequent_days) {
                case 1:
                    day = "Sunday";
                    break;
                case 2:
                    day = "Monday";
                    break;
                case 3:
                    day = "Tuesday";
                    break;
                case 4:
                    day = "Wednesday";
                    break;
                case 5:
                    day = "Thursday";
                    break;
                case 6:
                    day = "Friday";
                    break;
                case 7:
                    day = "Saturday";
                    break;
            }
            return(day);
        } else {
            return("");
        }
    }
    static String getFrequentDestination() {
        if (logs.top != -1) {
            Stack<String> destinations_booked = new Stack<>();
            Stack<String> unique_destinations = new Stack<>();
            for (int i = 0; i <= logs.top; i++) {
                destinations_booked.push(logs.arr.get(i).destination);
                if (!isPresent(unique_destinations, logs.arr.get(i).destination)) {
                    unique_destinations.push(logs.arr.get(i).destination);
                }
            }
            int max_count = 0;
            String frequent_destination = "";
            for (int i = 0; i <= unique_destinations.top; i++) {
                int count = countElement(destinations_booked, unique_destinations.arr.get(i));
                if (count > max_count) {
                    max_count = count;
                    frequent_destination = unique_destinations.arr.get(i);
                }
            }
            return(frequent_destination);
        } else {
            return("");
        }
    }
}
class Request implements Runnable {
    Traveller traveller;
    Seat seatObj;
    Thread thread;
    int success;
 
    Request(Traveller traveller, Seat seatObj) {
        this.traveller = traveller;
        this.success = 0;
        this.seatObj = seatObj;
        this.thread = new Thread(this, traveller.getPassengerName());
        this.thread.start();
    }
 
    public void run() {
        this.success = seatObj.book(traveller);
    }
}
class Schedule {
    static ObservableList<String> scheduleList = FXCollections.observableArrayList();
    static ObservableList<Airplane> schedule = FXCollections.observableArrayList();
    static int top = -1;
    static {
        Traveller boeingPassenger = new Traveller("Boeing Passenger");
        Manager.addEntry("BoeingC757", "International", 6, 1000.0, "NYC", "Hong Kong", 30, 10, 10, 12, 2024, 30, 11, 10, 12, 2024);
        Airplane BoeingC757 = schedule.get(Manager.searchEntry("BoeingC757"));
        boeingPassenger.airplane = BoeingC757;
        boeingPassenger.bookSeats(BoeingC757.seats[0]);
        boeingPassenger.bookSeats(BoeingC757.seats[1]);
        boeingPassenger.bookSeats(BoeingC757.seats[2]);
        boeingPassenger.bookSeats(BoeingC757.seats[3]);
        boeingPassenger.bookSeats(BoeingC757.seats[4]);
        boeingPassenger.bookSeats(BoeingC757.seats[5]);
        Traveller MH200Passenger = new Traveller("MH200 Passenger");
        Manager.addEntry("MH200", "International", 20, 1500.0, "Sydney", "Delhi", 45, 11, 10, 11, 2024, 50, 11, 10, 11, 2024);
        Airplane MH200 = schedule.get(Manager.searchEntry("MH200"));
        MH200Passenger.airplane = MH200;
        MH200Passenger.bookSeats(MH200.seats[0]);
        MH200Passenger.bookSeats(MH200.seats[1]);
        Traveller A350Passenger = new Traveller("A350 Passenger");
        Manager.addEntry("A350", "Domestic", 15, 1000.0, "Delhi", "Bangalore", 05, 9, 7, 12, 2024, 30, 9, 7, 12, 2024);
        Airplane A350 = schedule.get(Manager.searchEntry("A350"));
        A350Passenger.airplane = A350;
        A350Passenger.bookSeats(A350.seats[0]);
        A350Passenger.bookSeats(A350.seats[1]);
        A350Passenger.bookSeats(A350.seats[2]);
    }
}
class Traveller extends Schedule {
    ObservableList<String> seats_booked;
    ObservableList<String> addons_booked;
    private String passenger_name;
    Airplane airplane;
    double totalCost;
    String getPassengerName() {
        return (passenger_name);
    }
    Traveller(String passenger_name) {
        this.passenger_name = passenger_name;
        this.airplane = null;
        this.seats_booked = FXCollections.observableArrayList();
        this.addons_booked = FXCollections.observableArrayList();
        this.totalCost = 0;
    }
    int bookSeats(Seat seatObj) {
        Request req = new Request(this, seatObj);
        Thread requestThread = req.thread;
        int wasBookingSuccessful = 0;
        try {
            requestThread.join();
            wasBookingSuccessful = req.success;
            if (wasBookingSuccessful == 1) {
                Platform.runLater(new Runnable() {
                    public void run() {
                       Traveller.this.airplane.seatPrice.set(Traveller.this.airplane.seatPrice.get() * 1.1);
                       for (int i = 0; i < Traveller.this.airplane.seats.length; i++) {
                            if (Traveller.this.airplane.seats[i].booked.get() == true) {
                                Traveller.this.airplane.seats[i].seatPrice = Traveller.this.airplane.seatPrice.get();
                            }
                       }
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return(wasBookingSuccessful);
    }
}
 
class Manager extends Schedule {
    static int searchEntry(String airplane_name) {
        int index = -1;
        for (int i = 0; i <= top; i++) {
            if (schedule.get(i).name.toLowerCase().equals(airplane_name.toLowerCase())) {
                index = i;
                break;
            }
        }
        return (index);
    }
    static boolean deleteEntry(String airplane_name) {
        boolean success = false;
        if (top != -1) {
            int index = searchEntry(airplane_name);
            if (index != -1) {
                schedule.remove(index);
                Schedule.scheduleList.remove(index);
                top--;
                success = true;
            }
        }
        return (success);
    }
    static boolean setDeparted(String airplane_name) {
        boolean success = false;
        if (top != -1) {
            int index = searchEntry(airplane_name);
            if (index != -1) {
                Airplane reference = schedule.get(index);
                reference.departed = true;
                deleteEntry(airplane_name);
                Report.logs.push(reference);
                Report.departedFlights.add(reference);
                success = true;
            }
        }
        return (success);
    }
    static void addEntry(String name, String type, int seat_capacity, double seatPrice, String origin,
            String destination, int arrival_minute, int arrival_hour, int arrival_day, int arrival_month,
            int arrival_year, int departure_minute, int departure_hour, int departure_day, int departure_month,
            int departure_year) {
        Airplane reference = new Airplane(name, type, seat_capacity, seatPrice, origin, destination, arrival_minute,
                arrival_hour, arrival_day, arrival_month, arrival_year, departure_minute, departure_hour, departure_day,
                departure_month, departure_year);
        top = top + 1;
        schedule.add(reference);
        Schedule.scheduleList.add(reference.toString());
    }
}
class concurrentTraveller implements Runnable {
    private Traveller traveller;
    Airplane toBookAirplane;
    Thread travellerConsole;
    public void run() {
        System.out.println("Simulating multithreading in concurrent requests");
        @SuppressWarnings("resource")
        Scanner scannerObj = new Scanner(System.in);
        traveller = new Traveller("Traveller 2");
        traveller.airplane = toBookAirplane;
        System.out.println("Demo Traveller is now booking seats in airplane: " + traveller.airplane.name);
        if (traveller.airplane.seats.length == traveller.airplane.countBooked()) {
            System.out.print("Airplane is fully booked.");
        } else {
            do {
                System.out.print("Enter seat number to book (-1 to EXIT): ");
                int seatPos = scannerObj.nextInt();
                if (seatPos == -1) {
                    break;
                }
                scannerObj.nextLine();
                int success = traveller.bookSeats(traveller.airplane.seats[seatPos]);
                if (success == 1) {
                    System.out.println("Seat " + seatPos + " has been booked by Traveller 2");
                } else {
                    System.out.println("Seat " + seatPos + " has already been booked.");
                }
            } while (traveller.airplane.seats.length != traveller.airplane.countBooked());
        }
    }
    concurrentTraveller(Airplane toBookAirplane) {
        this.toBookAirplane = toBookAirplane;
        travellerConsole = new Thread(this, "Traveller 2 Console");
        travellerConsole.start();
    }
}
public class Demo extends Application {
    private Traveller traveller;
    public static void main(String[] args) {
        launch(args);
    }
 
    public void start(Stage ps) {
        ps.setTitle("Airline Reservation System");
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        Scene sc = new Scene(gp, 400, 300);
        Label lblWelcome = new Label("Welcome to Airline Reservation System");
        Label lblLogin = new Label("Login As:");
        Button btnUser = new Button("Traveller");
        Button btnAdmin = new Button("Manager");
        btnUser.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent o) {
                userWindow();
            }
        });
        btnAdmin.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent o) {
                adminWindow();
            }
        });
        gp.add(lblWelcome, 0, 0);
        gp.add(lblLogin, 0, 1);
        GridPane.setHalignment(lblLogin, HPos.CENTER);
        gp.add(btnUser, 0, 2);
        GridPane.setHalignment(btnUser, HPos.CENTER);
        gp.add(btnAdmin, 0, 3);
        GridPane.setHalignment(btnAdmin, HPos.CENTER);
        gp.setHgap(10);
        gp.setVgap(10);
        ps.setScene(sc);
        ps.show();
    }
    public void deleteScene(Stage primaryStage, Scene oldScene) {
        AnchorPane apDelete = new AnchorPane();
        apDelete.setPrefSize(354, 273);
 
        Label titleLabel = new Label("Delete Flight");
        titleLabel.setLayoutX(14);
        titleLabel.setLayoutY(6);
        titleLabel.setFont(Font.font("System Bold", 16));
 
        ListView<Airplane> listView = new ListView<>(Schedule.schedule);
        listView.setLayoutX(17);
        listView.setLayoutY(48);
        listView.setPrefSize(322, 112);
 
        Label scheduleLabel = new Label("Schedule");
        scheduleLabel.setLayoutX(17);
        scheduleLabel.setLayoutY(29);
 
        Label flightNameLabel = new Label("Flight Name");
        flightNameLabel.setLayoutX(17);
        flightNameLabel.setLayoutY(179);
 
        TextField flightNameField = new TextField();
        flightNameField.setLayoutX(100);
        flightNameField.setLayoutY(175);
        flightNameField.setPrefSize(149, 25);
 
        Label placeholderLabel = new Label();
        placeholderLabel.setLayoutX(20);
        placeholderLabel.setLayoutY(237);
        placeholderLabel.setPrefSize(182, 17);
        placeholderLabel.setFont(Font.font(13));
 
        Button backButton = new Button("Back");
        backButton.setLayoutX(299);
        backButton.setLayoutY(234);
 
        Button deleteButton = new Button("Delete");
        deleteButton.setLayoutX(238);
        deleteButton.setLayoutY(234);
 
        Button updateButton = new Button("Update");
        updateButton.setLayoutX(170);
        updateButton.setLayoutY(234);
 
        apDelete.getChildren().addAll(titleLabel, listView, scheduleLabel, flightNameLabel, flightNameField, placeholderLabel, backButton, deleteButton, updateButton);
 
        Scene scene = new Scene(apDelete);
        primaryStage.setScene(scene);
 
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                listView.setItems(FXCollections.observableArrayList());
                listView.setItems(Schedule.schedule);
                flightNameField.clear();
            }
        });
 
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                primaryStage.setScene(oldScene);
            }
        });
 
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                boolean success = Manager.deleteEntry(flightNameField.getText());
                if (success) {
                    placeholderLabel.setText("Successfully deleted.");
                } else {
                    placeholderLabel.setText("Not found.");
                }
            }
        });
 
    }
 
    public void addonScene(Stage primaryStage, Scene Dashboard) {
        AnchorPane apAddon = new AnchorPane();
        apAddon.setPrefSize(230, 243);
 
        Label titleLabel = new Label("Add-ons available");
        titleLabel.setLayoutX(14);
        titleLabel.setLayoutY(6);
        titleLabel.setFont(new Font("System Bold", 16));
 
        CheckBox wifiCheckBox = new CheckBox("Wi-Fi access (₹3000)");
        wifiCheckBox.setLayoutX(14);
        wifiCheckBox.setLayoutY(37);
 
        CheckBox loungeCheckBox = new CheckBox("Lounge access (₹5000)");
        loungeCheckBox.setLayoutX(14);
        loungeCheckBox.setLayoutY(65);
 
        CheckBox cateringCheckBox = new CheckBox("On-demand catering (₹5000)");
        cateringCheckBox.setLayoutX(14);
        cateringCheckBox.setLayoutY(95);
 
        CheckBox luggageCheckBox = new CheckBox("Extra luggage space (₹5000)");
        luggageCheckBox.setLayoutX(14);
        luggageCheckBox.setLayoutY(123);
 
        Button purchaseButton = new Button("Purchase");
        purchaseButton.setLayoutX(138);
        purchaseButton.setLayoutY(204);
 
        Label costLabel = new Label("Total Cost:");
        costLabel.setLayoutX(14);
        costLabel.setLayoutY(159);
        costLabel.setFont(new Font("System Bold", 12));
 
        Label totalCostLabel = new Label(String.format("%.2f", traveller.totalCost));
        totalCostLabel.setLayoutX(85);
        totalCostLabel.setLayoutY(159);
 
        apAddon.getChildren().addAll(titleLabel, wifiCheckBox, loungeCheckBox, cateringCheckBox, luggageCheckBox,
                                  purchaseButton, costLabel, totalCostLabel);
 
        Scene scene = new Scene(apAddon);
        primaryStage.setScene(scene);
 
        wifiCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (wifiCheckBox.isSelected()) {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue + 3000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                } else {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue - 3000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                }
            }
        });
 
        loungeCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (loungeCheckBox.isSelected()) {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue + 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                } else {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue - 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                }
            }
        });
 
        luggageCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (luggageCheckBox.isSelected()) {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue + 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                } else {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue - 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                }
            }
        });
 
        cateringCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (cateringCheckBox.isSelected()) {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue + 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                } else {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue - 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                }
            }
        });
 
        purchaseButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (wifiCheckBox.isSelected()) {
                    traveller.addons_booked.add("ADD-ON PURCHASED: WI-FI ACCESS | COST: 3000 | BOOKED UNDER: " + traveller.getPassengerName());
                    traveller.totalCost = traveller.totalCost + 3000.0;
                }
 
                if (loungeCheckBox.isSelected()) {
                    traveller.addons_booked.add("ADD-ON PURCHASED: LOUNGE ACCESS | COST: 5000 | BOOKED UNDER: " + traveller.getPassengerName());
                    traveller.totalCost = traveller.totalCost + 5000.0;
                }
 
                if (cateringCheckBox.isSelected()) {
                    traveller.addons_booked.add("ADD-ON PURCHASED: ON-DEMAND ACCESS | COST: 5000 | BOOKED UNDER: " + traveller.getPassengerName());
                    traveller.totalCost = traveller.totalCost + 5000.0;
                }
 
                if (luggageCheckBox.isSelected()) {
                    traveller.addons_booked.add("ADD-ON PURCHASED: EXTRA LUGGAGE SPACE | COST: 5000 | BOOKED UNDER: " + traveller.getPassengerName());
                    traveller.totalCost = traveller.totalCost + 5000.0;
                }
                primaryStage.setScene(Dashboard);
            }
        });
    }
 
    public void reportScene(Stage primaryStage, Scene oldScene) {
        Label reportLabel = new Label("Report");
        reportLabel.setLayoutX(14.0);
        reportLabel.setLayoutY(6.0);
        reportLabel.setFont(new Font("System Bold", 17.0));
 
        Label departedFlightsLabel = new Label("Departed Flights");
        departedFlightsLabel.setLayoutX(14.0);
        departedFlightsLabel.setLayoutY(32.0);
 
        Label frequentDestLabel = new Label("Frequent Destination:");
        frequentDestLabel.setLayoutX(14.0);
        frequentDestLabel.setLayoutY(182.0);
 
        Label frequentDepPeriodLabel = new Label("Frequent Departure Month:");
        frequentDepPeriodLabel.setLayoutX(14.0);
        frequentDepPeriodLabel.setLayoutY(199.0);
 
        Label frequentBookPeriodLabel = new Label("Frequent Booking Period:");
        frequentBookPeriodLabel.setLayoutX(14.0);
        frequentBookPeriodLabel.setLayoutY(216.0);
 
        Label fullyBookedFlightsLabel = new Label("Fully Booked Flights");
        fullyBookedFlightsLabel.setLayoutX(14.0);
        fullyBookedFlightsLabel.setLayoutY(254.0);
 
        ListView<Airplane> departedFlightsList = new ListView<>(Report.departedFlights);
        departedFlightsList.setLayoutX(14.0);
        departedFlightsList.setLayoutY(51.0);
        departedFlightsList.setPrefHeight(124.0);
        departedFlightsList.setPrefWidth(470.0);
 
        ListView<Airplane> fullyBookedFlightsList = new ListView<>();
        fullyBookedFlightsList.setLayoutX(14.0);
        fullyBookedFlightsList.setLayoutY(271.0);
        fullyBookedFlightsList.setPrefHeight(101.0);
        fullyBookedFlightsList.setPrefWidth(470.0);
 
        FilteredList<Airplane> filteredListRef = new FilteredList<>(Report.departedFlights, i -> {
            return(i.countBooked() == i.seats.length);
        });
 
        fullyBookedFlightsList.setItems(filteredListRef);
 
        Label frequentDestData = new Label();
        frequentDestData.setLayoutX(150.0);
        frequentDestData.setLayoutY(182.0);
 
        Label frequentDepPeriodData = new Label();
        frequentDepPeriodData.setLayoutX(185.0);
        frequentDepPeriodData.setLayoutY(199.0);
 
        Label frequentBookPeriodData = new Label();
        frequentBookPeriodData.setLayoutX(173.0);
        frequentBookPeriodData.setLayoutY(216.0);
 
        Button backButton = new Button("Back");
        backButton.setLayoutX(370.0);
        backButton.setLayoutY(187.0);
        backButton.setPrefHeight(25.0);
        backButton.setPrefWidth(48.0);
 
        Button updateButton = new Button("Update");
        updateButton.setLayoutX(426.0);
        updateButton.setLayoutY(187.0);
 
 
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                primaryStage.setScene(oldScene);
            }
        });
 
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                departedFlightsList.setItems(FXCollections.observableArrayList());
                departedFlightsList.setItems(Report.departedFlights);
                frequentDepPeriodData.setText(Report.getFrequentDeparturePeriod());
                frequentBookPeriodData.setText(Report.getFrequentBookingDay() + " | " + Report.getFrequentBookingMonth() + " | " + Report.getFrequentBookingYear());
                frequentDestData.setText(Report.getFrequentDestination());
            }
        });
 
        AnchorPane apReport = new AnchorPane();
        apReport.getChildren().addAll(
                reportLabel, departedFlightsLabel, frequentDestLabel, frequentDepPeriodLabel,
                frequentBookPeriodLabel, fullyBookedFlightsLabel, departedFlightsList, fullyBookedFlightsList,
                frequentDestData, frequentDepPeriodData, frequentBookPeriodData, backButton, updateButton
        );
 
        Scene scene = new Scene(apReport, 495, 387);
        primaryStage.setScene(scene);
    }
 
    public void invoiceScene(Stage travellerStage, Scene oldScene) {
        AnchorPane apInvoice = new AnchorPane();
        apInvoice.setPrefSize(408, 292);
 
        Label invoiceLabel = new Label("Invoice");
        invoiceLabel.setLayoutX(14);
        invoiceLabel.setLayoutY(7);
        invoiceLabel.setFont(Font.font("System Bold", 19));
 
        Label seatsBookedLabel = new Label("Seats Booked");
        seatsBookedLabel.setLayoutX(14);
        seatsBookedLabel.setLayoutY(35);
 
        Label addonsPurchasedLabel = new Label("Addons purchased");
        addonsPurchasedLabel.setLayoutX(14);
        addonsPurchasedLabel.setLayoutY(144);
 
        Label totalCostLabel = new Label("TOTAL COST:");
        totalCostLabel.setLayoutX(14);
        totalCostLabel.setLayoutY(262);
        totalCostLabel.setPrefSize(79, 17);
        totalCostLabel.setFont(Font.font("System Bold", 12));
 
        Label totalAmountLabel = new Label();
        totalAmountLabel.setLayoutX(89);
        totalAmountLabel.setLayoutY(260);
 
        ListView<String> seatsListView = new ListView<>(traveller.seats_booked);
        seatsListView.setLayoutX(14);
        seatsListView.setLayoutY(56);
        seatsListView.setPrefSize(373, 83);
 
        ListView<String> addonsListView = new ListView<>(traveller.addons_booked);
        addonsListView.setLayoutX(14);
        addonsListView.setLayoutY(165);
        addonsListView.setPrefSize(373, 83);
 
        Button updateButton = new Button("Update");
        updateButton.setLayoutX(335);
        updateButton.setLayoutY(256);
 
        Button backButton = new Button("Back");
        backButton.setLayoutX(284);
        backButton.setLayoutY(256);
 
        apInvoice.getChildren().addAll(
            invoiceLabel, seatsBookedLabel, seatsListView,
            addonsPurchasedLabel, addonsListView,
            totalCostLabel, totalAmountLabel,
            updateButton, backButton
        );
 
        Scene scene = new Scene(apInvoice);
        travellerStage.setScene(scene);
 
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                travellerStage.setScene(oldScene);
            }
        });
 
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                totalAmountLabel.setText("₹" + String.format("%.2f", traveller.totalCost) + "");
            }
        });
    }
 
    public void scheduleScene(Stage arbitraryStage, Scene oldScene) {
        AnchorPane apSchedule = new AnchorPane();
        apSchedule.setPrefHeight(358.0);
        apSchedule.setPrefWidth(679.0);
 
        ListView<Airplane> mainListView = new ListView<>(Schedule.schedule);
        mainListView.setLayoutX(23.0);
        mainListView.setLayoutY(54.0);
        mainListView.setPrefHeight(268.0);
        mainListView.setPrefWidth(317.0);
 
        ListView<Airplane> secondaryListView = new ListView<>();
        secondaryListView.setLayoutX(362.0);
        secondaryListView.setLayoutY(222.0);
        secondaryListView.setPrefHeight(95.0);
        secondaryListView.setPrefWidth(282.0);
 
        Label titleLabel = new Label("Schedule");
        titleLabel.setLayoutX(306.0);
        titleLabel.setLayoutY(6.0);
        titleLabel.setPrefHeight(32.0);
        titleLabel.setPrefWidth(92.0);
        titleLabel.setFont(Font.font("System Bold", 21.0));
 
        Label searchByLabel = new Label("Search by:");
        searchByLabel.setLayoutX(499.0);
        searchByLabel.setLayoutY(46.0);
 
        Label originLabel = new Label("Origin");
        originLabel.setLayoutX(413.0);
        originLabel.setLayoutY(72.0);
        originLabel.setPrefHeight(17.0);
        originLabel.setPrefWidth(40.0);
 
        Label destinationLabel = new Label("Destination");
        destinationLabel.setLayoutX(380.0);
        destinationLabel.setLayoutY(101.0);
 
        Label fromTimeLabel = new Label("From Time");
        fromTimeLabel.setLayoutX(385.0);
        fromTimeLabel.setLayoutY(128.0);
        fromTimeLabel.setPrefSize(67.0, 17.0);
 
        Label tillTimeLabel = new Label("Till Time");
        tillTimeLabel.setLayoutX(399.0);
        tillTimeLabel.setLayoutY(157.0);
 
 
        TextField originTextField = new TextField();
        originTextField.setLayoutX(462.0);
        originTextField.setLayoutY(68.0);
        originTextField.setPrefHeight(22.0);
        originTextField.setPrefWidth(145.0);
 
 
        TextField destinationTextField = new TextField();
        destinationTextField.setLayoutX(462.0);
        destinationTextField.setLayoutY(97.0);
        destinationTextField.setPrefHeight(22.0);
        destinationTextField.setPrefWidth(145.0);
 
 
        TextField fromTimeTextField = new TextField();
        fromTimeTextField.setLayoutX(462.0);
        fromTimeTextField.setLayoutY(124.0);
        fromTimeTextField.setPrefSize(145.0, 22.0);
 
        TextField tillTimeTextField = new TextField();
        tillTimeTextField.setLayoutX(462.0);
        tillTimeTextField.setLayoutY(153.0);
        tillTimeTextField.setPrefSize(145.0, 22.0);
 
        Button searchButton = new Button("Search");
        searchButton.setLayoutX(454.0);
        searchButton.setLayoutY(188.0);
        searchButton.setMnemonicParsing(false);
 
        Button backButton = new Button("Back");
        backButton.setLayoutX(515.0); 
        backButton.setLayoutY(188.0);
        backButton.setMnemonicParsing(false);
 
        Button clearButton = new Button("Clear");
        clearButton.setLayoutX(563.0);
        clearButton.setLayoutY(188.0);
        clearButton.setMnemonicParsing(false);
 
        Button updateButton = new Button("Update");
        updateButton.setLayoutX(590.0); 
        updateButton.setLayoutY(325.0);
        updateButton.setMnemonicParsing(false);
 
        apSchedule.getChildren().addAll(
            mainListView, titleLabel, searchByLabel, originLabel, destinationLabel,
            fromTimeLabel, tillTimeLabel, originTextField, destinationTextField,
            fromTimeTextField, tillTimeTextField, searchButton, clearButton, secondaryListView, backButton, updateButton
        );
 
        Scene scSchedule = new Scene(apSchedule);
 
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                mainListView.setItems(FXCollections.observableArrayList());
                mainListView.setItems(Schedule.schedule);
                fromTimeTextField.clear();
                tillTimeTextField.clear();
                originTextField.clear();
                destinationTextField.clear();
                secondaryListView.setItems(FXCollections.observableArrayList());
            }
        });
 
 
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                originTextField.clear();
                destinationTextField.clear();
                fromTimeTextField.clear();
                tillTimeTextField.clear();
                secondaryListView.setItems(FXCollections.observableArrayList());
            }
        });
 
 
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                arbitraryStage.setScene(oldScene);
            }
        });
 
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                secondaryListView.setItems(FXCollections.observableArrayList());
                FilteredList<Airplane> filteredListRef = new FilteredList<>(Schedule.schedule, i -> {
                    boolean originMatch = i.origin.toLowerCase().contains(originTextField.getText().toLowerCase());
                    boolean destinationMatch = i.destination.toLowerCase().contains(destinationTextField.getText().toLowerCase());
                    SimpleDateFormat ddmmyyyy = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date fromTime = ddmmyyyy.parse(fromTimeTextField.getText());
                        Date tillTime = ddmmyyyy.parse(tillTimeTextField.getText());
                        Date arrivalDate = ddmmyyyy.parse(ddmmyyyy.format(i.arrival.getTime()));
                        boolean timeMatch = !arrivalDate.before(fromTime) && !arrivalDate.after(tillTime);
                        return(originMatch && destinationMatch && timeMatch);
                    } catch (ParseException e) {
                        return (originMatch && destinationMatch);
                    }
                }
                );
                secondaryListView.setItems(filteredListRef);
            }
        });
 
        arbitraryStage.setScene(scSchedule);
    }
 
    public void bookPrompt(Stage primaryStage, Scene oldScene) {
        AnchorPane apBook = new AnchorPane();
        apBook.setPrefSize(496, 259);
 
        ListView<Airplane> listView = new ListView<>(Schedule.schedule);
        listView.setLayoutX(20);
        listView.setLayoutY(31);
        listView.setPrefSize(458, 140);
 
        Label scheduleLabel = new Label("Schedule");
        scheduleLabel.setLayoutX(20);
        scheduleLabel.setLayoutY(14);
 
        Label flightNameLabel = new Label("Flight Name:");
        flightNameLabel.setLayoutX(20);
        flightNameLabel.setLayoutY(177);
 
        TextField flightNameField = new TextField();
        flightNameField.setLayoutX(20);
        flightNameField.setLayoutY(196);
        flightNameField.setPrefWidth(130);
 
        Label statusLabel = new Label();
        statusLabel.setLayoutX(169);
        statusLabel.setLayoutY(200);
        statusLabel.setPrefSize(167, 17);
 
        Button updateButton = new Button("Update");
        updateButton.setLayoutX(317);
        updateButton.setLayoutY(221);
 
        Button backButton = new Button("Back");
        backButton.setLayoutX(437);
        backButton.setLayoutY(221);
 
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                primaryStage.setScene(oldScene);
            }
        });
 
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                listView.setItems(FXCollections.observableArrayList());
                listView.setItems(Schedule.schedule);
            }
        });
 
        Button bookButton = new Button("Book");
        bookButton.setLayoutX(384);
        bookButton.setLayoutY(221);
 
        apBook.getChildren().addAll(listView, scheduleLabel, flightNameLabel, flightNameField, statusLabel, backButton, bookButton, updateButton);
        Scene scene = new Scene(apBook);
        primaryStage.setScene(scene);
 
        bookButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                int index = Manager.searchEntry(flightNameField.getText());
                if (index == -1) {
                    statusLabel.setText("Flight was not found.");
                } else {
                    Airplane foundAirplane = Schedule.schedule.get(index);
                    if (foundAirplane.countBooked() == foundAirplane.seats.length) {
                        statusLabel.setText("Flight is fully booked.");
                    } else {
                        traveller.airplane = foundAirplane;
                        bookScene(primaryStage, scene, oldScene);
                    }
                }
            }  
        });
    }
 
    public void bookScene(Stage primaryStage, Scene oldScene, Scene Dashboard) {
        AnchorPane apBook = new AnchorPane();
        apBook.setPrefSize(242, 290);
 
        Label titleLabel = new Label("Seats");
        titleLabel.setFont(Font.font("System Bold", 20));
        titleLabel.setLayoutX(95);
        titleLabel.setLayoutY(14);
 
        Label flightNameLabel = new Label("Flight Name:");
        flightNameLabel.setLayoutX(15);
        flightNameLabel.setLayoutY(52);
 
        Label flightLabel = new Label(traveller.airplane.name);
        flightLabel.setLayoutX(95);
        flightLabel.setLayoutY(52);
        flightLabel.setPrefSize(123, 17);
 
        ToggleButton[] seatButtons = new ToggleButton[traveller.airplane.seats.length];
 
        int baseY = 76 + (((seatButtons.length - 1) / 5) * 35);
 
        Label priceLabel = new Label("Price per Seat:");
        priceLabel.setLayoutX(16);
        priceLabel.setLayoutY(baseY+33);
 
        Label totalCostLabel = new Label("Total Cost:");
        totalCostLabel.setLayoutX(16);
        totalCostLabel.setLayoutY(baseY+54);
 
        Label totalCostValueLabel = new Label();
        totalCostValueLabel.setLayoutX(82);
        totalCostValueLabel.setLayoutY(baseY+54);
        totalCostValueLabel.setPrefSize(134, 17);
 
        Label statusLabel = new Label("Status:");
        statusLabel.setLayoutX(17);
        statusLabel.setLayoutY(baseY+75);
 
        Label statusValueLabel = new Label();
        statusValueLabel.setLayoutX(62);
        statusValueLabel.setLayoutY(baseY+75);
        statusValueLabel.setPrefSize(154, 17);
 
        Label priceValueLabel = new Label();
        priceValueLabel.setLayoutX(105);
        priceValueLabel.setLayoutY(baseY+33);
        priceValueLabel.setPrefSize(113, 17);
 
        Button backButton = new Button("Back");
        backButton.setLayoutX(15);
        backButton.setLayoutY(baseY + 100);
 
        Button nextButton = new Button("Next");
        nextButton.setLayoutX(171);
        nextButton.setLayoutY(baseY + 100);
 
        new concurrentTraveller(traveller.airplane);
 
        priceValueLabel.textProperty().bind(traveller.airplane.seatPrice.asString("%.2f"));
        for (int i = 0; i < seatButtons.length; i++) {
            seatButtons[i] = new ToggleButton();
            traveller.airplane.seats[i].ref = seatButtons[i];
            seatButtons[i].setText(traveller.airplane.seats[i].position + "");
            seatButtons[i].setPrefSize(40, 25);
            int row = i / 5;
            int col = i % 5;
            seatButtons[i].setLayoutX(14 + col * 43);
            seatButtons[i].setLayoutY(76 + row * 35);
            seatButtons[i].disableProperty().bind(traveller.airplane.seats[i].booked);
 
            BooleanBinding areAllSeatsBookedCheck = new BooleanBinding() {
                {
                    for (int i = 0; i < traveller.airplane.seats.length; i++) {
                        super.bind(traveller.airplane.seats[i].booked);
                    }
                }
                protected boolean computeValue() {
                    boolean check = true;
                    for (int i = 0; i < traveller.airplane.seats.length; i++) {
                        if (traveller.airplane.seats[i].booked.get() == false) {
                            check = false;
                            break;
                        }
                    }
                    return(check);
                }
            };
 
 
            areAllSeatsBookedCheck.addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        if (newValue == true) {
                            throw new AllSeatsBooked("Fully booked.");
                        }
                    } catch (AllSeatsBooked exc) {
                        statusValueLabel.setText(exc.toString());
                    }
                }
            );
 
            seatButtons[i].setOnAction(new EventHandler<ActionEvent>() {
               public void handle(ActionEvent ae) {
                ToggleButton currentButton = (ToggleButton) ae.getSource();
                if ((currentButton.isSelected()) && (!currentButton.isDisabled())) {
                    for (int j = 0; j < traveller.airplane.seats.length; j++) {
                        if (traveller.airplane.seats[j].ref == currentButton) {
                            Seat currentSeat = traveller.airplane.seats[j];
                            int success = traveller.bookSeats(currentSeat);
                            if (success == 1) {
                                backButton.setDisable(true);
                                if (areAllSeatsBookedCheck.get() == false)
                                statusValueLabel.setText(currentSeat.position + " successfully booked.");
                                totalCostValueLabel.setText(String.format("%.2f", traveller.totalCost));
                            } else {
                                if (traveller.airplane.countBooked() < traveller.airplane.seats.length) {
                                    statusValueLabel.setText(currentSeat.position + " already booked.");
                                }
                            }
                            break;
                        }
                    }
                }
               } 
            });
            apBook.getChildren().add(seatButtons[i]);
 
        }
        apBook.getChildren().addAll(titleLabel, flightNameLabel, flightLabel, backButton, nextButton, priceValueLabel, priceLabel, statusLabel, statusValueLabel, totalCostLabel, totalCostValueLabel);
        Scene scene = new Scene(apBook);
        primaryStage.setScene(scene);
 
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (traveller.seats_booked.isEmpty() == false) {
                    addonScene(primaryStage, Dashboard);
                } else {
                    primaryStage.setScene(oldScene);
                }
            }
        });
 
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                primaryStage.setScene(oldScene);
            } 
        });
    }
 
    public void userWindow() {
        Stage userStage = new Stage();
        userStage.setTitle("Traveller Window");
        GridPane gpName = new GridPane();
        gpName.setAlignment(Pos.CENTER);
        gpName.setHgap(10);
        gpName.setVgap(10);
        Scene scName = new Scene(gpName, 400, 300);
        Label lblName = new Label("Enter Name:");
        TextField tfName = new TextField();
        Button btnSubmit = new Button("Submit");
        Button btnExit = new Button("Exit");
        gpName.add(lblName, 0, 0);
        gpName.add(tfName, 1, 0);
        gpName.add(btnSubmit, 0, 1);
        gpName.add(btnExit, 1, 1);
 
        GridPane gpDashboard = new GridPane();
        gpDashboard.setAlignment(Pos.CENTER);
        gpDashboard.setHgap(10);
        gpDashboard.setVgap(10);
        Scene scDashBoard = new Scene(gpDashboard, 400, 300);
        Label lblWelcome = new Label();
        Button btnSchedule = new Button("Schedule");
        Button btnBook = new Button("Book");
        Button btnInvoice = new Button("Invoice");
        Button btnBack = new Button("Back");
        gpDashboard.add(lblWelcome, 0, 0);
        gpDashboard.add(btnSchedule, 0, 1);
        GridPane.setHalignment(btnSchedule, HPos.CENTER);
        gpDashboard.add(btnBook, 0, 2);
        GridPane.setHalignment(btnBook, HPos.CENTER);
        gpDashboard.add(btnInvoice, 0, 3);
        GridPane.setHalignment(btnInvoice, HPos.CENTER);
        gpDashboard.add(btnBack, 0, 4);
        GridPane.setHalignment(btnBack, HPos.CENTER);
 
        GridPane gpBook = new GridPane();
        gpBook.setAlignment(Pos.CENTER);
        gpBook.setHgap(10);
        gpBook.setVgap(10);
        Scene scBook = new Scene(gpBook,400,300);
        Label lblBookFlightName = new Label("Enter Flight Name");
        Label lblBookFlightResponse = new Label();
        TextField tfBookFlightName = new TextField();
        Button btnSubmitFlightName = new Button("Submit");
        Button btnBookBack = new Button("Back");
        gpBook.add(lblBookFlightName,0,0);
        gpBook.add(tfBookFlightName,1,0);
        gpBook.add(lblBookFlightResponse,1,1);
        gpBook.add(btnSubmitFlightName,0,2);
        gpBook.add(btnBookBack,1,2);
 
        GridPane gpAlreadyBooked = new GridPane();
        gpAlreadyBooked.setAlignment(Pos.CENTER);
        gpAlreadyBooked.setHgap(10);
        gpAlreadyBooked.setVgap(10);
        Scene scAlreadyBooked = new Scene(gpAlreadyBooked,200,200);
        Label lblAlreadyBooked = new Label("You have already booked tickets");
        Button btnAlreadyBookedBack = new Button("Back");
        gpAlreadyBooked.add(lblAlreadyBooked,0,0);
        gpAlreadyBooked.add(btnAlreadyBookedBack,0,1);
        GridPane.setHalignment(lblAlreadyBooked, HPos.CENTER);
 
        btnSubmit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent o) {
                traveller = new Traveller(tfName.getText());
                lblWelcome.setText("Welcome " + traveller.getPassengerName());
                userStage.setScene(scDashBoard);
            }
        });
 
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent o) {
                userStage.close();
            }
        });
 
        btnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                userStage.setScene(scName);
                tfName.setText("");
            }
        });
 
        btnBookBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                userStage.setScene(scDashBoard);
            }
        });
 
        btnAlreadyBookedBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                userStage.setScene(scDashBoard);
            }
        });
 
        btnSchedule.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                scheduleScene(userStage, scDashBoard);
            }
        });
 
        btnBook.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (traveller.seats_booked.isEmpty() == false) { 
                    userStage.setScene(scAlreadyBooked);
                } else {
                    bookPrompt(userStage, scDashBoard);
                }   
            }
        });
 
        btnInvoice.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                invoiceScene(userStage, scDashBoard);
            }
        });
 
        btnSubmitFlightName.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                int index_airplane = Manager.searchEntry(tfBookFlightName.getText());
                if(index_airplane == -1)
                    lblBookFlightResponse.setText("No such airplane found");
                else {
                    Airplane found_airplane = Schedule.schedule.get(index_airplane);
                    if (found_airplane.countBooked() == found_airplane.seats.length) {
                        lblBookFlightResponse.setText("All the seats have been booked");
                    }
                }
            }
        });
 
        userStage.setScene(scName);
        userStage.show();
    }
 
    public void adminWindow() {
        Stage adminStage = new Stage();
        adminStage.setTitle("Manager Window");
 
        GridPane gpLogin = new GridPane();
        gpLogin.setAlignment(Pos.CENTER);
        gpLogin.setHgap(10);
        gpLogin.setVgap(10);
        Scene scLogin = new Scene(gpLogin, 400, 300);
        Label lblPassword = new Label("Enter Password");
        Label lblPasswordResponse = new Label();
        PasswordField tfPassword = new PasswordField();
        Button btnPasswordSubmit = new Button("Submit");
        Button btnExit = new Button("Exit");
        gpLogin.add(lblPassword, 0, 0);
        gpLogin.add(tfPassword, 1, 0);
        gpLogin.add(lblPasswordResponse, 1, 1);
        gpLogin.add(btnPasswordSubmit, 0, 2);
        gpLogin.add(btnExit, 1, 2);
 
        GridPane gpDashboard = new GridPane();
        gpDashboard.setAlignment(Pos.CENTER);
        gpDashboard.setHgap(10);
        gpDashboard.setVgap(10);
        Scene scDashboard = new Scene(gpDashboard, 400, 400);
        Label lblWelcomeAdmin = new Label("Welcome Manager");
        Button btnAdd = new Button("Add Flights");
        Button btnDepart = new Button("Depart Flights");
        Button btnDelete = new Button("Delete Flights");
        Button btnLogout = new Button("Logout");
        Button btnSchedule = new Button("Schedule");
        Button btnReport = new Button("Report");
        gpDashboard.add(lblWelcomeAdmin, 0, 0);
        gpDashboard.add(btnAdd, 0, 1);
        GridPane.setHalignment(btnAdd, HPos.CENTER);
        gpDashboard.add(btnDepart, 0, 2);
        GridPane.setHalignment(btnDepart, HPos.CENTER);
        gpDashboard.add(btnDelete,0,3);
        GridPane.setHalignment(btnLogout, HPos.CENTER);
        gpDashboard.add(btnSchedule,0,4);
        GridPane.setHalignment(btnSchedule, HPos.CENTER);
        gpDashboard.add(btnReport,0,5);
        GridPane.setHalignment(btnReport, HPos.CENTER);
        gpDashboard.add(btnLogout,0,6);
        GridPane.setHalignment(btnDelete, HPos.CENTER);
 
 
        GridPane gpAddFlight = new GridPane();
        gpAddFlight.setAlignment(Pos.CENTER);
        gpAddFlight.setHgap(10);
        gpAddFlight.setVgap(10);
        Scene scAddFlight = new Scene(gpAddFlight, 450, 650);
        Label lblEnterDetails = new Label("Enter Flight Details:");
        gpAddFlight.add(lblEnterDetails, 0, 0);
        Label lblFlightName = new Label("Name:");
        gpAddFlight.add(lblFlightName, 0, 1);
        Label lblFlightIntDome = new Label("International/Domestic");
        gpAddFlight.add(lblFlightIntDome, 0, 2);
        Label lblFlightOrigin = new Label("Origin:");
        gpAddFlight.add(lblFlightOrigin, 0, 3);
        Label lblFlightDestination = new Label("Destination:");
        gpAddFlight.add(lblFlightDestination, 0, 4);
        Label lblFlightDateArrival = new Label("Date of Arrival");
        gpAddFlight.add(lblFlightDateArrival, 0, 5);
        Label lblFlightTimeArrival = new Label("Time of Arrival");
        gpAddFlight.add(lblFlightTimeArrival, 0, 6);
        Label lblFlightDateDeparture = new Label("Date oF Departure");
        gpAddFlight.add(lblFlightDateDeparture, 0, 7);
        Label lblFlightTimeDeparture = new Label("Time of Departure");
        gpAddFlight.add(lblFlightTimeDeparture, 0, 8);
        Label lblFlightCapacity = new Label("Seat Capacity");
        gpAddFlight.add(lblFlightCapacity, 0, 9);
        Label lblFlightPrice = new Label("Seat Price");
        gpAddFlight.add(lblFlightPrice, 0, 10);
        Label lblFlightDetailsResponse = new Label();
        gpAddFlight.add(lblFlightDetailsResponse, 1, 11);
        TextField tfFlightName = new TextField();
        gpAddFlight.add(tfFlightName, 1, 1);
        TextField tfFlightType = new TextField();
        gpAddFlight.add(tfFlightType, 1, 2);
        TextField tfFlightOrigin = new TextField();
        gpAddFlight.add(tfFlightOrigin, 1, 3);
        TextField tfFlightDestination = new TextField();
        gpAddFlight.add(tfFlightDestination, 1, 4);
        TextField tfFlightDateArrival = new TextField();
        tfFlightDateArrival.setPromptText("dd-mm-yyyy");
        gpAddFlight.add(tfFlightDateArrival, 1, 5);
        TextField tfFlightTimeArrival = new TextField();
        tfFlightTimeArrival.setPromptText("hh:mm");
        gpAddFlight.add(tfFlightTimeArrival, 1, 6);
        TextField tfFlightDateDeparture = new TextField();
        tfFlightDateDeparture.setPromptText("dd-mm-yyyy");
        gpAddFlight.add(tfFlightDateDeparture, 1, 7);
        TextField tfFlightTimeDeparture = new TextField();
        tfFlightTimeDeparture.setPromptText("hh:mm");
        gpAddFlight.add(tfFlightTimeDeparture, 1, 8);
        TextField tfFlightCapacity = new TextField();
        gpAddFlight.add(tfFlightCapacity, 1, 9);
        TextField tfFlightPrice = new TextField();
        gpAddFlight.add(tfFlightPrice, 1, 10);
        Button btnSubmitFlightDetails = new Button("Submit");
        gpAddFlight.add(btnSubmitFlightDetails, 0, 12);
        Button btnClearFlightDetails = new Button("Clear");
        gpAddFlight.add(btnClearFlightDetails, 1, 12);
        Button btnBackFlightDetails = new Button("Back");
        gpAddFlight.add(btnBackFlightDetails, 0, 13);
 
        AnchorPane apDepart = new AnchorPane();
        apDepart.setPrefSize(354, 273);
        Scene scDepart = new Scene(apDepart);
 
        Label titleLabel = new Label("Depart Flight");
        titleLabel.setLayoutX(14);
        titleLabel.setLayoutY(6);
        titleLabel.setFont(Font.font("System Bold", 16));
 
        ListView<Airplane> listView = new ListView<>(Schedule.schedule);
        listView.setLayoutX(17);
        listView.setLayoutY(48);
        listView.setPrefSize(322, 112);
 
        Label scheduleLabel = new Label("Schedule");
        scheduleLabel.setLayoutX(17);
        scheduleLabel.setLayoutY(29);
 
        Label flightNameLabel = new Label("Flight Name");
        flightNameLabel.setLayoutX(17);
        flightNameLabel.setLayoutY(179);
 
        TextField flightNameField = new TextField();
        flightNameField.setLayoutX(100);
        flightNameField.setLayoutY(175);
        flightNameField.setPrefSize(149, 25);
 
        Label placeholderLabel = new Label();
        placeholderLabel.setLayoutX(20);
        placeholderLabel.setLayoutY(237);
        placeholderLabel.setPrefSize(182, 17);
        placeholderLabel.setFont(Font.font(13));
 
        Button backButton = new Button("Back");
        backButton.setLayoutX(299);
        backButton.setLayoutY(234);
 
        Button deleteButton = new Button("Depart");
        deleteButton.setLayoutX(234);
        deleteButton.setLayoutY(234);
 
        Button updateButton = new Button("Update");
        updateButton.setLayoutX(166);
        updateButton.setLayoutY(234);
 
        apDepart.getChildren().addAll(titleLabel, listView, scheduleLabel, flightNameLabel, flightNameField, placeholderLabel, backButton, deleteButton, updateButton);
 
        GridPane gpDelete = new GridPane();
        gpDelete.setAlignment(Pos.CENTER);
        gpDelete.setHgap(10);
        gpDelete.setVgap(10);
        Scene scDelete = new Scene(gpDelete, 400, 300);
        Label lblDeleteFlight = new Label("Enter Flight name:");
        Label lblDeleteResponse = new Label();
        TextField tfDeleteFlight = new TextField();
        Button btnSubmitDelete = new Button("Submit");
        Button btnBackDelete = new Button("Back");
        gpDelete.add(lblDeleteFlight, 0, 0);
        gpDelete.add(tfDeleteFlight, 1, 0);
        gpDelete.add(lblDeleteResponse, 1, 1);
        gpDelete.add(btnSubmitDelete, 0, 2);
        gpDelete.add(btnBackDelete, 1, 2);
 
        btnPasswordSubmit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (tfPassword.getText().equals("pass123"))
                    adminStage.setScene(scDashboard);
                else
                    lblPasswordResponse.setText("Incorrect Password");
            }
        });
 
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.close();
            }
        });
 
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scAddFlight);
            }
        });
 
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                listView.setItems(FXCollections.observableArrayList());
                listView.setItems(Schedule.schedule);
                flightNameField.clear();
            }
        });
 
        btnLogout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scLogin);
                tfPassword.setText("");
            }
        });
 
        btnSubmitFlightDetails.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (tfFlightName.getText().isEmpty() || tfFlightType.getText().isEmpty()
                        || tfFlightOrigin.getText().isEmpty() ||
                        tfFlightDestination.getText().isEmpty() || tfFlightDestination.getText().isEmpty()
                        || tfFlightDateArrival.getText().isEmpty()
                        || tfFlightTimeArrival.getText().isEmpty() || tfFlightDateDeparture.getText().isEmpty()
                        || tfFlightDateDeparture.getText().isEmpty() ||
                        tfFlightPrice.getText().isEmpty() || tfFlightCapacity.getText().isEmpty())
                    lblFlightDetailsResponse.setText("Please fill all the details");
                else {
                    Manager.addEntry(tfFlightName.getText(), tfFlightType.getText(),
                            Integer.parseInt(tfFlightCapacity.getText()), Double.parseDouble(tfFlightPrice.getText()),
                            tfFlightOrigin.getText(), tfFlightDestination.getText(),
                            Integer.parseInt(tfFlightTimeArrival.getText().substring(3)),
                            Integer.parseInt(tfFlightTimeArrival.getText().substring(0, 2)),
                            Integer.parseInt(tfFlightDateArrival.getText().substring(0, 2)),
                            Integer.parseInt(tfFlightDateArrival.getText().substring(3, 5)),
                            Integer.parseInt(tfFlightDateArrival.getText().substring(6)),
                            Integer.parseInt(tfFlightTimeDeparture.getText().substring(3)),
                            Integer.parseInt(tfFlightTimeDeparture.getText().substring(0, 2)),
                            Integer.parseInt(tfFlightDateDeparture.getText().substring(0, 2)),
                            Integer.parseInt(tfFlightDateDeparture.getText().substring(3, 5)),
                            Integer.parseInt(tfFlightDateDeparture.getText().substring(6)));
 
                    lblFlightDetailsResponse.setText("Flight Details Added");
                    tfFlightName.setText("");
                }
            }
        });
 
        btnClearFlightDetails.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                tfFlightCapacity.setText("");
                tfFlightDateArrival.setText("");
                tfFlightDateDeparture.setText("");
                tfFlightDestination.setText("");
                tfFlightName.setText("");
                tfFlightOrigin.setText("");
                tfFlightPrice.setText("");
                tfFlightTimeArrival.setText("");
                tfFlightTimeDeparture.setText("");
                tfFlightType.setText("");
            }
        });
 
        btnBackFlightDetails.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scDashboard);
            }
        });
 
        btnSchedule.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ae) {
                scheduleScene(adminStage, scDashboard);
            }
        });
        btnDepart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scDepart);
            }
        });
 
        btnReport.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                reportScene(adminStage, scDashboard);
            }
        });
 
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                boolean success = Manager.setDeparted(flightNameField.getText());
                if (success) {
                    placeholderLabel.setText("Successfully departed.");
                } else {
                    placeholderLabel.setText("Not found.");
                }
            }
        });
 
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scDashboard);
            }
        });
 
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                deleteScene(adminStage, scDashboard);
            }
        });
 
 
        btnBackDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scDashboard);
            }
        });
 
        adminStage.setScene(scLogin);
        adminStage.show();
    }
}
