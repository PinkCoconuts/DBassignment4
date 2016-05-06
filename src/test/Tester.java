package test;

import controller.Controller;

public class Tester {

    private static String planeID = "CR9";

    public static void main( String[] args ) {
        //init controller
        Controller controller = new Controller();

        //init db connection
        controller.initializeConnection();

        //Core methods test sequence
        //controller.reserveSeat( planeID, 1333 );
        //controller.reserveSeat( planeID, 1400 );
        //controller.bookSeat( planeID, "A1", 1400 );
        
        //Helper methods test sequence
        //controller.areAllSeatsBooked( planeID );
        //controller.areAllSeatsReserved( planeID );
        //controller.bookAllSeats( planeID );
        //controller.areAllSeatsBooked( planeID );
        //controller.areAllSeatsReserved( planeID );
        //controller.clearAllBookings( planeID );
        //controller.areAllSeatsBooked( planeID );
        //controller.areAllSeatsReserved( planeID );

        //close db conneciton
        controller.closeConnection();
    }
}
