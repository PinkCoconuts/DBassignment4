package test;

import controller.Controller;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tester {

    public static void main( String[] args ) {
        Controller controller = new Controller();
        Logger logger = controller.getLogger();
        controller.initializeConnection( logger );

        //Test functionality here
        controller.reserveSeat( "CR9", 1200 );

        //Sleep for 1 sec (acts like a lock)
        try {
            Thread.sleep( 1000 );
        } catch ( InterruptedException ex ) {
            Logger.getLogger( Tester.class.getName() ).log( Level.SEVERE, null, ex );
        }

        controller.reserveSeat( "CR9", 1350 );

        controller.closeConnection();
    }
}
