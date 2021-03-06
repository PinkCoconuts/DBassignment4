package simpleThreading;

import dataSource.Reservation;
import entities.Seat;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Logger;
import static simpleThreading.SimpleThreads.threadMessage;
import utilities.DatabaseConnector;
import utilities.Protocol;

class UserThread implements Runnable {

    private String planeId;
    private int threadId;
    private int chosenProbability;

    //Database Connection
    private DatabaseConnector databaseConnector = null;
    private Connection connection = null;

    //Database authentication
    private static String[] databaseHost = { "jdbc:oracle:thin:@127.0.0.1:1521:XE", "jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:dat" };
    private static String[] databaseUsername = { "bobkoo", "cphbs96" };
    private static String[] databasePassword = { "qwerty12345", "cphbs96" };

    //Logger functionality
    private Logger logger = null;

    //Mappers
    private Reservation reservationMapper = null;

    private boolean initializeConnection( Logger logger ) {
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
            //logger.info( "Connection with database initialized" );
        }
        return true;
    }

    public boolean closeConnection( Connection connection, Logger logger ) {
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

    public UserThread( Logger logger, String planeId, int threadId, int chosenProbability ) {
        this.logger = logger;
        this.planeId = planeId;
        this.threadId = threadId;
        this.chosenProbability = chosenProbability;
        
        databaseConnector = new DatabaseConnector( databaseHost[ 1 ], databaseUsername[ 1 ], databasePassword[ 1 ], null );
        initializeConnection( logger );

        reservationMapper = new Reservation();
    }

    public void run() {
        String response = "No information " + threadId;
        if ( connection != null ) {
            Random rand = new Random();

            //2. Reserve a seat
            Seat seat = null;

            int shouldReserve = (rand.nextInt( 3 ) + 1);
            if ( shouldReserve > 1 ) {
                seat = reservationMapper.reserve( connection, logger, planeId, threadId );
            }
            String seatId = (seat != null ? seat.getSeat_no() : "A1");
            
            //3. Pause for random number of seconds
            int userDelayImitator = (rand.nextInt( chosenProbability ) + 1);

            try {
                Thread.sleep( userDelayImitator * 1000 );
            } catch ( InterruptedException ex ) {
                response = Protocol.internalError + "First";
            }

            //4. Choose if book or not
            int toBook = (rand.nextInt( 3 ) + 1);
            if ( toBook >= 3 ) {
                int responseBook = reservationMapper.book( connection, logger, planeId, seatId, threadId );
                System.out.println( "responseBook is : " + responseBook );
                switch ( responseBook ) {
                    case 0:
                        response = Protocol.successfulBooking;
                        break;
                    case -1:
                        response = Protocol.unsuccessfulBooking_NotReserved;
                        break;
                    case -2:
                        response = Protocol.unsuccessfulBooking_ReservedByAnotherUser;
                        break;
                    case -3:
                        response = Protocol.unsuccessfulBooking_Timeout;
                        break;
                    case -4:
                        response = Protocol.unsuccessfulBooking_AlreadyBooked;
                        break;
                    default:
                        response = Protocol.internalError + "Second";
                        break;
                }
            } else {
                response = Protocol.refusalToBookASeat;
            }
        }
        threadMessage( response );
        closeConnection( connection, logger );
    }
}
