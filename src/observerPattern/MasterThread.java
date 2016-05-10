package observerPattern;

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

class MasterThread {

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
    private boolean allBooked = false;
    private int currentlyRunningSlaves = 0;

    //Protocol
    private Protocol protocol = new Protocol();

    //Statistics
    int numberOfUserThreadStarted = 0;
    int numberOfSuccesfullyBookedSeats = 0;
    int numberOfBookingsWithoutReservationOfAnyone = 0;
    int numberOfBookingsWhileSeatIsReservedBySomeoneElse = 0;
    int numberOfBookingsOfOccupiedSeats = 0;

    public static void main( String[] args ) {
        new MasterThread().observer();
    }

    private boolean initializeConnection() {
        if ( connection != null ) {
            System.out.println( "Connection already existing" );
            //logger.info( "Connection with database is already existing!" );
            return true;
        } else {
            connection = databaseConnector.getConnection( logger );
        }
        return true;
    }

    private void printOutStats() {
        System.out.println( "\nThe plane is fully booked Slaves : " + currentlyRunningSlaves
                + "\n\t Number of UserThreads started (total) : " + numberOfUserThreadStarted
                + "\n\t Number of successful bookings : " + numberOfSuccesfullyBookedSeats
                + "\n\t Number of bookings without a reservation : " + numberOfBookingsWithoutReservationOfAnyone
                + "\n\t Number of bookings where the customer was not the one, who was holding the reservation : " + numberOfBookingsWhileSeatIsReservedBySomeoneElse
                + "\n\t Number of bookings of occupied seats : " + numberOfBookingsOfOccupiedSeats );
    }

    private void observerLogic() {
        while ( allBooked == false ) {

            if ( currentlyRunningSlaves < 10 ) {
                allBooked = reservationMapper.isAllBooked( connection, logger, planeID );
                if ( allBooked == true || numberOfSuccesfullyBookedSeats >= 96 ) {

                    //Debugging
                    System.out.println( "Yes all of them are booked, chill! AllBooked ? "
                            + allBooked + ", bookedSeats : " + numberOfSuccesfullyBookedSeats );
                    //

                    printOutStats();
                    System.exit( 0 );
                }
                //Debugging
                //System.out.println( "CurrentSlaves : " + currentlyRunningSlaves );
                //System.out.println( "Are all reserved : " + allBooked );
                //System.out.println( "Last Thread ID is : " + numberOfUserThreadStarted );
                //

                for ( int i = 1; i < (10 - currentlyRunningSlaves); i++ ) {
                    numberOfUserThreadStarted++;

                    SlaveThread eventSource = new SlaveThread( logger, planeID, "UserThreadNo" + numberOfUserThreadStarted, numberOfUserThreadStarted );

                    eventSource.addObserver( ( Observable obj, Object arg ) -> {
                        if ( arg.equals( Protocol.successfulBooking ) ) {
                            numberOfSuccesfullyBookedSeats++;

                            //Debugging
                            System.out.println( "Booked seats incremented : " + numberOfSuccesfullyBookedSeats );
                            //

                        } else if ( arg.equals( Protocol.unsuccessfulBooking_NotReserved ) ) {
                            numberOfBookingsWithoutReservationOfAnyone++;
                        } else if ( arg.equals( Protocol.unsuccessfulBooking_ReservedByAnotherUser ) ) {
                            numberOfBookingsWhileSeatIsReservedBySomeoneElse++;
                        } else if ( arg.equals( Protocol.unsuccessfulBooking_AlreadyBooked ) ) {
                            numberOfBookingsOfOccupiedSeats++;
                        }

                        //Debugging
                        out.println( "\nReceived response: " + arg );
                        //

                        currentlyRunningSlaves--;
                        observerLogic();
                    } );

                    System.out.println( "Starting thread " + numberOfUserThreadStarted );
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
