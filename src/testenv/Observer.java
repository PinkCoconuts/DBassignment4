package testenv;

import dataSource.Reservation;
import java.util.Observable;
import static java.lang.System.out;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import utilities.DatabaseConnector;
import utilities.PerformanceLogger;
import utilities.Protocol;

class Observer {

    //Database Connection
    private DatabaseConnector databaseConnector = null;
    private Connection connection = null;

    //Database authentication
    private static String[] databaseHost = { "jdbc:oracle:thin:@127.0.0.1:1521:XE", "jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:dat" };
    private static String[] databaseUsername = { "bobkoo", "cphbs96" };
    private static String[] databasePassword = { "qwerty12345", "cphbs96" };

    //Logger functionality
    private String loggerName = "chillMaster";
    private String loggerPath = "/AirplaneSimulatorLogger.log";
    private PerformanceLogger performanceLogger = null;
    private Logger logger = null;

    //Mappers
    private static Reservation reservationMapper;

    //DB Dependencies
    private static String planeID = "CR9";

    //Observer
    private boolean isMainThreadAlive = true;
    private int currentlyRunningSlaves = 0;
    //private List<SlaveThread> slaveThreadList = new ArrayList();

    //Protocol
    private Protocol protocol = new Protocol();
    private int nextThreadID = 0;

    public static void main( String[] args ) {
        new Observer().observer();
    }

    private boolean initializeConnection() {
        if ( connection != null ) {
            System.out.println( "Connection already existing" );
            logger.info( "Connection with database is already existing!" );
            return true;
        } else {
            connection = databaseConnector.getConnection( logger );

            try {
                connection.setAutoCommit( true );
                // termination by the garbage collector
            } catch ( SQLException ex ) {
                logger.severe( "SQL Exception while trying to connect to db " + ex );
                return false;
            }
            logger.info( "Connection with database initialized" );
        }
        return true;
    }

    private void observerLogic() {
        System.out.println( "The isMainThreadAlive is : " + isMainThreadAlive );
        while ( isMainThreadAlive ) {
            if ( currentlyRunningSlaves < 10 ) {
                isMainThreadAlive = !reservationMapper.isAllBooked( connection, logger, planeID )
                        && !reservationMapper.isAllReserved( connection, logger, planeID );
                System.out.println( "I go in : currentlyRunningSlaves : " + currentlyRunningSlaves + " lastTreadID : " + nextThreadID );
                for ( int i = 1; i < (10 - currentlyRunningSlaves); i++ ) {
                    nextThreadID++;

                    SlaveThread eventSource = new SlaveThread( logger, planeID, "UserThreadNo" + nextThreadID, nextThreadID );
                    //slaveThreadList.add( eventSource );
                    eventSource.addObserver( ( Observable obj, Object arg ) -> {
                        out.println( "\nReceived response: " + arg );
                        currentlyRunningSlaves--;
                        observerLogic();
                    } );

                    System.out.println( "Starting thread " + nextThreadID );
                    new Thread( eventSource ).start();
                    currentlyRunningSlaves++;
                }
            }
        }
    }

    private void observer() {
        //Logger functionality
        performanceLogger = new PerformanceLogger();
        logger = performanceLogger.initLogger( loggerName, loggerPath );

        databaseConnector = new DatabaseConnector( databaseHost[ 1 ], databaseUsername[ 1 ], databasePassword[ 1 ], null );
        initializeConnection();

        reservationMapper = new Reservation();

        reservationMapper.clearAllBookings( connection, logger, planeID );

//        int nextThreadID = 0;
        observerLogic();
    }
}
