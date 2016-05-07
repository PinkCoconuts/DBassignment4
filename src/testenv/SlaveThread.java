package testenv;

import dataSource.Reservation;
import entities.Seat;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.DatabaseConnector;
import utilities.PerformanceLogger;
import utilities.Protocol;

class SlaveThread extends Observable implements Runnable {

    private String planeId;
    private String userThreadName;
    private int userThreadID;

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
    private static Reservation reservationMapper;

    //Protocol
    private Protocol protocol = new Protocol();

    public SlaveThread( Logger logger, String planeId, String userThreadName, int userThreadID ) {
        this.logger = logger;
        this.planeId = planeId;
        //ThreadName = distinguisher only for Threading functionality
        this.userThreadName = userThreadName;
        //ThreadID = identifier for a client, which will be used in the db
        this.userThreadID = userThreadID;

        databaseConnector = new DatabaseConnector( databaseHost[ 1 ], databaseUsername[ 1 ], databasePassword[ 1 ], null );
        initializeConnection();

        //Create an instance of the Reservation class
        reservationMapper = new Reservation();
    }

    private boolean initializeConnection() {
        if ( connection != null ) {
            System.out.println( "Connection already existing" );
            //logger.info( "Connection with database is already existing!" );
            return true;
        } else {
            connection = databaseConnector.getConnection( logger );

//            try {
//                connection.setAutoCommit( true );
//                // termination by the garbage collector
//            } catch ( SQLException ex ) {
//                logger.severe( "SQL Exception while trying to connect to db " + ex );
//                return false;
//            }
//            logger.info( "Connection with database initialized" );
        }
        return true;
    }

    private void informMaster( String information ) {
        databaseConnector.closeConnection( connection, logger );
        String response = information;
        setChanged();
        notifyObservers( response );
    }

    public void run() {

        //2. Reserve a seat
        Seat seat = reservationMapper.reserve( connection, logger, planeId, userThreadID );
        String seatId = (seat != null ? seat.getSeat_no() : "A1");
//        if ( seat == null ) {
//            informMaster( protocol.unsuccessfulReservation );
//            return;
//        }
        Random rand = new Random();
        //3. Pause for random number of seconds
        int secsToSleep = (rand.nextInt( 7 ) + 1);
        //System.out.println( "Hi, I am " + userThreadID + " and I will sleep for " + secsToSleep + " secs." );
        try {
            Thread.sleep( secsToSleep * 1000 );
        } catch ( InterruptedException ex ) {
            logger.severe( "Sleep exeption during 'user delay' simulation " + ex );
            informMaster( protocol.internalError );
            return;
        }

        //4. Choose if book or not
        int toBook = (rand.nextInt( 3 ) + 1);
        //System.out.println( "Hi, I am " + userThreadID + " and I rolled " + toBook + " for booking." );
        if ( toBook >= 3 ) {

            //System.out.println( "Hi, I am " + userThreadID + " and I will try to book seat :" + seatId );
            int responseBook = reservationMapper.book( connection, logger, planeId, seatId, userThreadID );

            String response = "";
            System.out.println( "SlaveThread : Response : " + responseBook );
            switch ( responseBook ) {
                case 0:
                    response = protocol.successfulBooking;
                    break;
                case -1:
                    response = protocol.unsuccessfulBooking_NotReserved;
                    break;
                case -2:
                    response = protocol.unsuccessfulBooking_ReservedByAnotherUser;
                    break;
                case -3:
                    response = protocol.unsuccessfulBooking_Timeout;
                    break;
                case -4:
                    response = protocol.unsuccessfulBooking_AlreadyBooked;
                    break;
                default:
                    response = protocol.internalError;
                    break;
            }

            informMaster( response );
            return;
        } else {
            try {
                //System.out.println( "Hi, I am " + userThreadID + " and I will NOT try to book seat :" + seat.getSeat_no() );
                Thread.sleep( 5000 );
            } catch ( InterruptedException ex ) {
                Logger.getLogger( SlaveThread.class.getName() ).log( Level.SEVERE, null, ex );
            }
            try {
                reservationMapper.book( connection, logger, planeId, seatId, userThreadID );
            } catch ( Exception e ) {
                System.out.println( "e is : " + e );
                logger.warning( "WHAT THE FUCK IS THIS SHIT : " + e );
            }
            informMaster( protocol.refusalToBookASeat );
            return;
        }

    }
}
