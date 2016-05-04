package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/*
 * TODO before running this code :
 * Install Oracle SQL Database - http://blog.whitehorses.nl/2014/03/18/installing-java-oracle-11g-r2-express-edition-and-sql-developer-on-ubuntu-64-bit/
 * JDBC Drivers for this database - http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html
 * Example code - http://www.mkyong.com/jdbc/connect-to-oracle-db-via-jdbc-driver-java/
 * Local host solution - http://stackoverflow.com/questions/18192521/ora-12505-tnslistener-does-not-currently-know-of-sid-given-in-connect-descript
 * Add ojdbc.jar to Libraries of Project : Right click -> Properties -> Libraries -> Add JAR
 */
public class DatabaseConnector {

    private String databaseHost;
    private String databaseUsername;
    private String databasePassword;
    private String jdbcDriverName = "oracle.jdbc.driver.OracleDriver";

    public DatabaseConnector( String databaseHost, String databaseUsername, String databasePassword, String jdbcDriverName ) {
        this.databaseHost = databaseHost;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        if ( jdbcDriverName != null ) {
            this.jdbcDriverName = jdbcDriverName;
        }
    }

    public Boolean testJDBCdriver( Logger logger ) {
        try {
            Class.forName( jdbcDriverName );
        } catch ( ClassNotFoundException e ) {
            if ( logger != null ) {
                logger.severe( "SQL Exception while trying to test the JDBC Driver " + e );
            } else {
                System.out.println( "SQL Exception while trying to test the JDBC Driver " + e );
            }
            return false;
        }
        return true;
    }

    public Connection getConnection( Logger logger ) {
        Connection connection = null;
        if ( testJDBCdriver( logger ) ) {
            try {
                connection = DriverManager.getConnection( databaseHost, databaseUsername, databasePassword );

            } catch ( SQLException e ) {
                if ( logger != null ) {
                    logger.severe( "SQL Exception while trying to connect the connection to db " + e );
                } else {
                    System.out.println( "SQL Exception while trying to connect the connection to db " + e );
                }
                return null;
            }
            if ( logger != null ) {
                logger.info( "Connection with database established successfully!" );
            } else {
                System.out.println( "Connection with database established successfully!" );
            }

            try {
                connection.setAutoCommit( true );
                // termination by the garbage collector
            } catch ( SQLException ex ) {
                logger.severe( "SQL Exception while trying to connect to db " + ex );
                return null;
            }

            return connection;
        }
        return null;
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
                logger.info( "Connection with database closed successfully!" );
            } else {
                System.out.println( "Connection with database closed successfully!" );
            }
            return true;
        }

        if ( logger != null ) {
            logger.info( "Connection not initialized, cannot close!" );
        } else {
            System.out.println( "Connection not initialized, cannot close!" );
        }
        return false;
    }
}