package utilities;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PerformanceLogger {

    public Logger initLogger( String loggerName, String loggerPath ) {
        File file = new File( System.getProperty( "user.dir" ) + loggerPath );
        Logger logger = Logger.getLogger( loggerName );

        if ( !file.exists() ) {
            try {
                file.createNewFile();
            } catch ( IOException e ) {
                System.out.println( "Error : Logger creation failed!" + e );
                Logger.getLogger( PerformanceLogger.class.getName() ).log( Level.SEVERE, null, e );
            }
        }

        FileHandler fh;
        try {
            fh = new FileHandler( System.getProperty( "user.dir" ) + loggerPath, true );
            logger.addHandler( fh );
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter( formatter );

        } catch ( SecurityException | IOException e ) {
            System.out.println( "SecurityException or IOException while trying to "
                    + "create and add a handler to the logger : " + e );
            Logger.getLogger( PerformanceLogger.class.getName() ).log( Level.SEVERE, null, e );
        }
        logger.info( "***** NEW SESSION *****" );
        logger.info( logger.getName() + " Logger started!\n" );

        return logger;
    }
}
