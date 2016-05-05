package test;

import controller.Controller;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tester {

    public static void main( String[] args ) {
        Controller controller = new Controller();
        controller.initializeConnection();

        //Test functionality here
        System.out.println( "#1 : " + controller.reserveSeat( "CR9", 1225 ).getSeat_no() );

        //Sleep for 1 sec (acts like a lock)
//        try {
//            Thread.sleep( 1000 );
//        } catch ( InterruptedException ex ) {
//            Logger.getLogger( Tester.class.getName() ).log( Level.SEVERE, null, ex );
//        }
        System.out.println( "#2 : " + controller.reserveSeat( "CR9", 1325 ).getSeat_no() );

        controller.closeConnection();
    }
}
