package test;

import controller.Controller;

public class Tester {

    private static String planeID = "CR9";

    public static void main( String[] args ) {
        //init controller
        Controller controller = new Controller();

        //init db connection
        controller.initializeConnection();

        //Test functionality here
        //controller.reserveSeat( planeID, 1333 );
        //controller.reserveSeat( planeID, 1400 );
        //controller.bookSeat( planeID, "A1", 1400 );
        //controller.areAllSeatsBooked( planeID );
        //controller.bookAllSeats( planeID );
        //controller.areAllSeatsBooked( planeID );
        //controller.clearAllBookings( planeID );
        //controller.areAllSeatsBooked( planeID );

        //close db conneciton
        controller.closeConnection();
    }
}
