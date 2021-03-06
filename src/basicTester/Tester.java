package basicTester;

import controller.Controller;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Tester {

    private static String planeID = "CR9";

    public static void main( String[] args ) {
        //init controller
        Controller controller = new Controller();

        //init db connection
        controller.initializeConnection();

        //Core methods test sequence
        //controller.reserveSeat( planeID, 3535 );
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
        
        //Test core methods with full db
        //controller.bookAllSeats( planeID );
        //controller.reserveSeat( planeID, 1400 );
        //controller.bookSeat( planeID, "A1", 1400 );
        
        //System.out.println( "DUDE : " + controller.areAllSeatsBooked(planeID ) );        
        //long MAX_DURATION = MILLISECONDS.convert( 5, SECONDS );
        //System.out.println( "MAX_DURATION is : " + MAX_DURATION );
        
        //close db conneciton
        controller.closeConnection();
    }
}
