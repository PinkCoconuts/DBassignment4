package controller;

import dataSource.ReservationMapper;
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
    private static ReservationMapper reservationMapper;

    public Controller() {
        //Logger functionality
        performanceLogger = new PerformanceLogger();
        logger = performanceLogger.initLogger( loggerName, loggerPath );

        databaseConnector = new DatabaseConnector( databaseHost[ 0 ], databaseUsername[ 0 ], databasePassword[ 0 ], null );

        reservationMapper = new ReservationMapper();
    }

    public Logger getLogger() {
        return logger;
    }

    public Boolean initializeConnection( Logger logger ) {
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

    public Seat reserveSeat( String plane_no, long id ) {
        return reservationMapper.reserve( connection, logger, plane_no, id );
    }

    public void closeConnection() {
        databaseConnector.closeConnection( connection, logger );
    }

}
