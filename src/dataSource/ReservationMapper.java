package dataSource;

import entities.Seat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.DatabaseConnector;

public class ReservationMapper {

    public Seat reserve( Connection connection, Logger logger, String planeId, long customerId ) {
        try {
            connection.setAutoCommit( false );
        } catch ( SQLException e ) {
            logger.severe( "Error : reserve() SQLException : Set of auto commit to false failed : " + e );
        }

        //Returns false always
        lockSeatTBL( connection, logger );

        PreparedStatement preparedStatement = null;
        try {
            String updateSQL = "UPDATE SEAT "
                    + "SET RESERVED= ? , BOOKING_TIME= ? "
                    + "WHERE PLANE_NO= ? AND RESERVED IS NULL AND ROWNUM = 1";

            preparedStatement = connection.prepareStatement( updateSQL );

            preparedStatement.setLong( 1, customerId );
            preparedStatement.setLong( 2, 111111 );
            preparedStatement.setString( 3, planeId );

            preparedStatement.executeUpdate();

        } catch ( SQLException e ) {

            logger.severe( "Error : reserve() SQLException : " + e.getMessage() );
        } finally {
            try {
                if ( preparedStatement != null ) {
                    preparedStatement.close();
                }
            } catch ( SQLException e ) {
                logger.warning( "Error : reserve() PreparedStatement was not closed "
                        + "successfully : " + e );
            }
        }
        logger.info( "Successfully reserved a free seat in plane " + planeId + " for customer " + customerId );
        return getSeat( connection, logger, planeId, customerId );
    }

    private boolean lockSeatTBL( Connection connection, Logger logger ) {
        int result = 0;
        PreparedStatement preparedStatement = null;
        String SQLString = "LOCK TABLE SEAT in exclusive mode";

        try {

            preparedStatement = connection.prepareStatement( SQLString );
            result = preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            logger.severe( "Error : lockSeatTBL() SQLException : " + e.getMessage() );
        } finally {
            try {
                preparedStatement.close();
            } catch ( SQLException e ) {
                logger.warning( "Error : lockSeatTBL() PreparedStatement was not closed "
                        + "successfully : " + e.getMessage() );
            }
        }
        System.out.println( "Was locked ? " + (result == 1) );
        return result == 1;
    }

    public Seat getSeat( Connection connection, Logger logger, String plane_no, long id ) {
        Seat seat = new Seat();
        PreparedStatement preparedStatement = null;

        try {

            String selectSQL = "SELECT SEAT_NO FROM SEAT WHERE RESERVED = ?";

            preparedStatement = connection.prepareStatement( selectSQL );
            preparedStatement.setLong( 1, id );

            ResultSet rs = preparedStatement.executeQuery();

            if ( rs.next() ) {
                seat.setSeat_no( rs.getString( "SEAT_NO" ) );
                seat.setPlane_no( plane_no );
                seat.setReserved( id );
                //We need to verify if the reservation is expired
                //so we need to set a timestamp or something similar
                //seat.setBooked_time( 111111 );
            }

            connection.commit();
        } catch ( SQLException e ) {
            logger.severe( "Error : getSeat() SQLException : " + e.getMessage() );
        } finally {
            try {
                if ( preparedStatement != null ) {
                    preparedStatement.close();
                }
            } catch ( SQLException e ) {
                logger.warning( "Error : getSeat() PreparedStatement was not closed "
                        + "successfully : " + e );
            }
        }

        logger.info( "Successfully selected a seat with seat number : " + seat.getSeat_no() );
        return seat;
    }

//    public int book( String plane_no, String seat_no, long id ) {
//        try {
//            //check if the seat is reserved
//            String selectSQL = "SELECT RESERVED, BOOKED FROM SEAT "
//                    + "WHERE PLANE_NO = ?"
//                    + "AND SEAT_NO = ?";
//            PreparedStatement preparedStatementSelect = connection.prepareStatement( selectSQL );
//            preparedStatementSelect.setString( 1, plane_no );
//            preparedStatementSelect.setString( 2, seat_no );
//            ResultSet rs = preparedStatementSelect.executeQuery();
//            long reserved = 0,
//                    booked = 0;
//            while ( rs.next() ) {
//                reserved = rs.getLong( "RESERVED" );
//                booked = rs.getLong( "BOOKED" );
//            }
//            if ( reserved == 0 ) {
//                return -1;
//            } else if ( reserved != id ) {
//                return -2;
//            } else if ( booked != 0 ) {
//                return -4;
//            } else {
//                //update the seat record
//                String updateSQL = "UPDATE SEAT "
//                        + "SET BOOKED= ? "
//                        + "WHERE PLANE_NO= ?"
//                        + "AND SEAT_NO= ?"
//                        + "AND RESERVED= ?"
//                        + "AND BOOKING_TIME> 0";
//                PreparedStatement preparedStatementUpdate = connection.prepareStatement( updateSQL );
//                preparedStatementUpdate.setLong( 1, id );
//                preparedStatementUpdate.setString( 2, plane_no );
//                preparedStatementUpdate.setString( 3, seat_no );
//                preparedStatementUpdate.setLong( 4, id );
//                preparedStatementUpdate.executeUpdate();
//            }
//        } catch ( SQLException ex ) {
//            System.out.println( ex.getMessage() );
//            return -5;
//        }
//        return 0;
//    }
//    public void bookAll( String plane_no ) {
//        try {
//            String updateSQL = "UPDATE SEAT "
//                    + "SET BOOKED= ? "
//                    + "WHERE PLANE_NO= ? "
//                    + "AND RESERVED IS NULL "
//                    + "AND BOOKED IS NULL";
//            PreparedStatement preparedStatementUpdate = connection.prepareStatement( updateSQL );
//            preparedStatementUpdate.setLong( 1, 999999 );
//            preparedStatementUpdate.setString( 2, plane_no );
//            preparedStatementUpdate.executeUpdate();
//        } catch ( SQLException ex ) {
//            System.out.println( ex );
//        }
//    }
//
//    public void clearAllBookings( String plane_no ) {
//        try {
//            String updateSQL = "UPDATE SEAT "
//                    + "SET BOOKED= ? "
//                    + ", RESERVED= ? "
//                    + "WHERE PLANE_NO= ?";
//            PreparedStatement preparedStatementUpdate = connection.prepareStatement( updateSQL );
//            preparedStatementUpdate.setLong( 1, 0 );
//            preparedStatementUpdate.setLong( 2, 0 );
//            preparedStatementUpdate.setString( 3, plane_no );
//            preparedStatementUpdate.executeUpdate();
//        } catch ( SQLException ex ) {
//            System.out.println( ex );
//        }
//    }
//
//    public boolean isAllBooked( String plane_no ) {
//        try {
//            String selectSQL = "SELECT BOOKED FROM SEAT WHERE PLANE_NO = ?";
//            PreparedStatement preparedStatementSelect = connection.prepareStatement( selectSQL );
//            preparedStatementSelect.setString( 1, plane_no );
//            ResultSet rs = preparedStatementSelect.executeQuery();
//            long booked = 0;
//            while ( rs.next() ) {
//                booked = rs.getLong( "BOOKED" );
//                if ( !(booked > 0) ) //in case it is not booked, the attribute "booked" can be either null or 0
//                {
//                    return false;
//                }
//            }
//        } catch ( SQLException ex ) {
//            System.out.println( ex );
//        }
//        return true;
//    }
//
//    public boolean isAllReserved( String plane_no ) {
//        try {
//            String selectSQL = "SELECT RESERVED FROM SEAT WHERE PLANE_NO = ?";
//            PreparedStatement preparedStatementSelect = connection.prepareStatement( selectSQL );
//            preparedStatementSelect.setString( 1, plane_no );
//            ResultSet rs = preparedStatementSelect.executeQuery();
//            long reserved = 0;
//            while ( rs.next() ) {
//                reserved = rs.getLong( "RESERVED" );
//                if ( !(reserved > 0) ) //in case it is not booked, the attribute "booked" can be either null or 0
//                {
//                    return false;
//                }
//            }
//        } catch ( SQLException ex ) {
//            System.out.println( ex );
//        }
//        return true;
//    }
}
