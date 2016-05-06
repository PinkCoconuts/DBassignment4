package controller;

import dataSource.Reservation;
import entities.Seat;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import utilities.DatabaseConnector;
import utilities.PerformanceLogger;

public class Controller {

    //Logger functionality
    private String loggerName = "chillMaster";
    private String loggerPath = "/AirplaneSimulatorLogger.log";
    private PerformanceLogger performanceLogger = null;
    private Logger logger = null;

    //Database Connection
    private DatabaseConnector databaseConnector = null;
    private Connection connection = null;
    //Database authentication
    private static String[] databaseHost = { "jdbc:oracle:thin:@127.0.0.1:1521:XE", "jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:dat" };
    private static String[] databaseUsername = { "bobkoo", "cphbs96" };
    private static String[] databasePassword = { "qwerty12345", "cphbs96" };

    //Mappers
    private static Reservation reservationMapper;

    public Controller() {
        //Logger functionality
        performanceLogger = new PerformanceLogger();
        logger = performanceLogger.initLogger( loggerName, loggerPath );

        databaseConnector = new DatabaseConnector( databaseHost[ 1 ], databaseUsername[ 1 ], databasePassword[ 1 ], null );

        reservationMapper = new Reservation();
    }

    public Boolean initializeConnection() {
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

    public Seat reserveSeat( String planeId, long customerId ) {
        return reservationMapper.reserve( connection, logger, planeId, customerId );
    }

    public int bookSeat( String planeId, String seatId, long customerId ) {
        return reservationMapper.book( connection, logger, planeId, seatId, customerId );
    }

    public void closeConnection() {
        databaseConnector.closeConnection( connection, logger );
    }

}
