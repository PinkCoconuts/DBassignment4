package threading;

import controller.Controller;
import dataSource.Reservation;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import utilities.DatabaseConnector;
import utilities.PerformanceLogger;
import utilities.Protocol;

public class SimpleThreads {

    private static Map<String, UserThread> userThreadStack = new HashMap();
    private static int threadsCounter = 0, reservedSeats = 0, bookWithNoRes = 0,
            bookWithAnotherUserRes = 0, bookWithTimeout = 0, bookAlreadyBooked = 0,
            internalError = 0;

    //Logger functionality
    private static String loggerName = "chillMaster";
    private static String loggerPath = "/AirplaneSimulatorLogger.log";
    private static PerformanceLogger performanceLogger = null;
    private static Logger logger = null;

    //DB Dependencies
    private static String planeID = "CR9";

    //Database Connection
    private static DatabaseConnector databaseConnector = null;
    private static Connection connection = null;

    //Database authentication
    private static String[] databaseHost = { "jdbc:oracle:thin:@127.0.0.1:1521:XE", "jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:dat" };
    private static String[] databaseUsername = { "bobkoo", "cphbs96" };
    private static String[] databasePassword = { "qwerty12345", "cphbs96" };

    //Mappers
    private static Reservation reservationMapper = null;

    private static void finalResults() {
        System.out.println( "\nThe plane is fully booked"
                + "\n\t Number of UserThreads started (total) : " + threadsCounter
                + "\n\t Number of successful bookings : " + reservedSeats
                + "\n\t Number of bookings without a reservation : " + bookWithNoRes
                + "\n\t Number of bookings where the customer was not the one, who was holding the reservation : " + bookWithAnotherUserRes
                + "\n\t Number of bookings where there was a timeout : " + bookWithTimeout
                + "\n\t Number of bookings of occupied seats : " + bookAlreadyBooked
                + "\n\t Counter of unexpected behaviour in the code : " + internalError );
    }

    static void threadMessage( String message ) {

        switch ( message ) {
            case Protocol.successfulBooking:
                reservedSeats++;
                System.out.println( "ReservedSeats : " + reservedSeats );
                break;
            case Protocol.unsuccessfulBooking_NotReserved:
                bookWithNoRes++;
                break;
            case Protocol.unsuccessfulBooking_ReservedByAnotherUser:
                bookWithAnotherUserRes++;
                break;
            case Protocol.unsuccessfulBooking_Timeout:
                bookWithTimeout++;
                break;
            case Protocol.unsuccessfulBooking_AlreadyBooked:
                bookAlreadyBooked++;
                break;
            default:
                internalError++;
                break;
        }

        String threadName = Thread.currentThread().getName();
        System.out.format( "%s: %s%n", threadName, message + " /// threads running : " + userThreadStack.size() );

        userThreadStack.remove( threadName );

        Thread t = Thread.currentThread();
        t.interrupt();

        if ( reservedSeats < 86 ) {
            nextThreadStarter( 2 );
        } else {
            if ( reservedSeats <= 96 ) {
                if ( userThreadStack.size() < 5 && reservedSeats != 96 ) {
                    nextThreadStarter( 2 );
                }
                if ( userThreadStack.isEmpty() && reservedSeats == 96 ) {
                    finalResults();
                }
            }
        }
    }

    private static void nextThreadStarter( int chosenProbability ) {
        threadsCounter++;
        String nextThreadName = "Thread" + threadsCounter;
        UserThread nextUserThread = new UserThread( logger, planeID, threadsCounter, chosenProbability );
        Thread thread = new Thread( nextUserThread );
        thread.setName( nextThreadName );
        userThreadStack.put( nextThreadName, nextUserThread );

        thread.start();
    }

    private static boolean initializeConnection( Logger logger ) {
        if ( connection != null ) {
            System.out.println( "Connection already existing" );
            //logger.info( "Connection with database is already existing!" );
            return true;
        } else {
            connection = databaseConnector.getConnection( logger );

            try {
                connection.setAutoCommit( false );
                // termination by the garbage collector
            } catch ( SQLException ex ) {
                logger.severe( "SQL Exception while trying to connect to db " + ex );
                return false;
            }
            logger.info( "Connection with database initialized" );
        }
        return true;
    }

    public static boolean closeConnection( Connection connection, Logger logger ) {
        if ( connection != null ) {

            try {
                connection.close();
            } catch ( SQLException e ) {
                if ( logger != null ) {
                    logger.severe( "SQL Exception while trying to close the connection to db " + e );
                } else {
                    System.out.println( "SQL Exception while trying to close the connection to db " + e );
                }
                return false;
            }
            if ( logger != null ) {
                //logger.info( "Connection with database closed successfully!" );
            } else {
                System.out.println( "Connection with database closed successfully!" );
            }
            return true;
        }

        if ( logger != null ) {
            //logger.info( "Connection not initialized, cannot close!" );
        } else {
            System.out.println( "Connection not initialized, cannot close!" );
        }
        return false;
    }

    public static void main( String args[] ) throws InterruptedException {
        //Logger functionality
        performanceLogger = new PerformanceLogger();
        logger = performanceLogger.initLogger( loggerName, loggerPath );

        reservationMapper = new Reservation();

        databaseConnector = new DatabaseConnector( databaseHost[ 1 ], databaseUsername[ 1 ], databasePassword[ 1 ], null );
        initializeConnection( logger );

        boolean clearStatus = reservationMapper.clearAllBookings( connection, logger, planeID );

        closeConnection( connection, logger );
        if ( clearStatus ) {
            for ( int i = 0; i < 10; i++ ) {
                nextThreadStarter( 6 );
            }
        } else {
            System.out.println( "Error : Not cleaned properly" );
        }
    }
}
