package dataSource;

import entities.Seat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.DatabaseConnector;

public class Reservation implements ReservationInterface {

    private long MAX_DURATION = MILLISECONDS.convert( 30, SECONDS );

    @Override
    public void Reservation( String user, String pw ) {
        //The connection with the database should not be established in a 
        //mapper-like class. Sorry, this implementation will be done separately
        //in a DatabaseConnector class. Check utilities package.
    }

    @Override
    public Seat reserve( Connection connection, Logger logger, String planeId,
            long customerId ) {
        try {
            connection.setAutoCommit( false );
        } catch ( SQLException e ) {
            logger.severe( "Error : reserve() SQLException : Set of auto commit "
                    + "to false failed : " + e );
            return null;
        }

        Seat seat = new Seat();
        PreparedStatement preparedStatement = null;

        try {

            /*
             * SELECT ... FOR UPDATE Query, which select a row in a table and locks
             * the specific role until a commit. Works only if AutoCommit is set
             * to false.
             */
            String selectSQL = "SELECT SEAT_NO FROM SEAT "
                    + "WHERE ROWNUM = 1 AND RESERVED IS NULL "
                    + "FOR UPDATE";

            preparedStatement = connection.prepareStatement( selectSQL );

            ResultSet rs = preparedStatement.executeQuery();

            if ( rs.next() ) {

                Date date = new Date();
                seat.setSeat_no( rs.getString( "SEAT_NO" ) );
                seat.setPlane_no( planeId );
                seat.setReserved( customerId );
                seat.setBooked_time( date.getTime() );

                logger.info( "Successfully selected for update a seat with seat "
                        + "number : " + seat.getSeat_no() );
                try {

                    String updateSQL = "UPDATE SEAT "
                            + "SET RESERVED= ? , BOOKING_TIME= ? "
                            + "WHERE PLANE_NO= ? AND SEAT_NO = ?";

                    preparedStatement = connection.prepareStatement( updateSQL );

                    preparedStatement.setLong( 1, seat.getReserved() );
                    preparedStatement.setLong( 2, seat.getBooked_time() );
                    preparedStatement.setString( 3, seat.getPlane_no() );
                    preparedStatement.setString( 4, seat.getSeat_no() );

                    preparedStatement.executeUpdate();

                } catch ( SQLException e ) {
                    logger.severe( "Error : reserve() SQLException on update: "
                            + e.getMessage() );
                    return null;
                } finally {
                    try {
                        if ( preparedStatement != null ) {
                            preparedStatement.close();
                        }
                    } catch ( SQLException e ) {
                        //Not fatal, do not return
                        logger.warning( "Error : reserve() PreparedStatement was "
                                + "not closed successfully on update : " + e );

                    }
                }
                logger.info( "Successfully reserved a free seat in plane " + planeId
                        + " for customer " + customerId );

                connection.commit();
                return seat;
            }
        } catch ( SQLException e ) {
            logger.severe( "Error : getSeat() SQLException on select for update: "
                    + e.getMessage() );
            return null;
        }
        return null;
    }

    @Override
    public int book( Connection connection, Logger logger, String planeId, String seatId,
            long customerId ) {
        try {
            connection.setAutoCommit( false );
        } catch ( SQLException e ) {
            logger.severe( "Error : book() SQLException : Set of auto commit to "
                    + "false failed : " + e );
            return -5;
        }

        try {
            String selectSQL = "SELECT RESERVED, BOOKED, BOOKING_TIME "
                    + "FROM SEAT "
                    + "WHERE PLANE_NO = ? AND SEAT_NO = ? "
                    + "FOR UPDATE";

            PreparedStatement preparedStatement = connection.prepareStatement( selectSQL );

            preparedStatement.setString( 1, planeId );
            preparedStatement.setString( 2, seatId );

            ResultSet rs = preparedStatement.executeQuery();

            if ( rs.next() ) {
                int reserved = 0, booked = 0;
                long bookingTime = 0;
                Date now = new Date();

                reserved = rs.getInt( "RESERVED" );
                booked = rs.getInt( "BOOKED" );
                bookingTime = rs.getLong( "BOOKING_TIME" );

                /*
                 * If a seat is not reserved (It should not be possible to book a
                 * a seat without a previous reservation)
                 *
                 * If a int is null in Java, then it will be == 0;
                 */
                if ( reserved == 0 ) {
                    logger.severe( "Error : book() SEAT_NO= " + seatId + " was not "
                            + "reserved." );
                    connection.commit();
                    return -1;
                }
                /*
                 * If the seat is not reserved by the customerId (A seat can only
                 * be booked by the customer that have the reservation on the seat)
                 */
                if ( reserved != customerId ) {
                    logger.severe( "Error : book() Seat was not reserved for customerId "
                            + customerId + " but for customerId " + reserved );
                    connection.commit();
                    return -2;
                }
                /*
                 * Reservation timeout, i.e. user was too slow to make a booking
                 */
                if ( now.getTime() - bookingTime >= MAX_DURATION ) {
                    logger.severe( "Error : book() Reservation expired." );

                    preparedStatement = null;
                    try {
                        String updateSQL = "UPDATE SEAT "
                                + "SET BOOKED = ?, RESERVED = ?, BOOKING_TIME = ? "
                                + "WHERE PLANE_NO = ? AND SEAT_NO = ? AND RESERVED = ?";

                        preparedStatement = connection.prepareStatement( updateSQL );

                        preparedStatement.setNull( 1, java.sql.Types.INTEGER );
                        preparedStatement.setNull( 2, java.sql.Types.INTEGER );
                        preparedStatement.setNull( 3, java.sql.Types.INTEGER );
                        preparedStatement.setString( 4, planeId );
                        preparedStatement.setString( 5, seatId );
                        preparedStatement.setLong( 6, customerId );

                        preparedStatement.executeUpdate();
                    } catch ( SQLException e ) {
                        logger.severe( "Error : book() SQLException on update on "
                                + " clearing reservation because of reservation "
                                + "timeout: " + e.getMessage() );
                        return -5;
                    } finally {
                        try {
                            if ( preparedStatement != null ) {
                                preparedStatement.close();
                            }
                        } catch ( SQLException e ) {
                            //Not fatal, do not return
                            logger.warning( "Error : book() PreparedStatement was "
                                    + "not closed successfully on clear reservation "
                                    + "because of reservation timeout : " + e );
                        }
                    }
                    logger.info( "Successfully cleared reservation because of "
                            + "reservation timeout PLANE_NO= " + planeId + " AND "
                            + "SEAT_NO= " + seatId );

                    connection.commit();
                    return -3;
                }
                /*
                 * If the seat is already occupied (that's not good since this is
                 * a overbooking) aka Locking missing or error in the logic of the
                 * the program
                 */
                if ( booked != 0 ) {
                    logger.severe( "Error : book() Internal error - PLANE_NO = "
                            + planeId + " AND SEAT_NO = " + seatId + " is already "
                            + "occupied." );
                    connection.commit();
                    return -4;
                } else {
                    preparedStatement = null;
                    try {
                        String updateSQL = "UPDATE SEAT "
                                + "SET BOOKED= ? "
                                + "WHERE PLANE_NO= ? AND SEAT_NO= ? AND RESERVED= ?";

                        preparedStatement = connection.prepareStatement( updateSQL );

                        preparedStatement.setLong( 1, customerId );
                        preparedStatement.setString( 2, planeId );
                        preparedStatement.setString( 3, seatId );
                        preparedStatement.setLong( 4, customerId );

                        preparedStatement.executeUpdate();
                    } catch ( SQLException e ) {
                        logger.severe( "Error : book() SQLException on update: "
                                + e.getMessage() );
                        return -5;
                    } finally {
                        try {
                            if ( preparedStatement != null ) {
                                preparedStatement.close();
                            }
                        } catch ( SQLException e ) {
                            //Not fatal, do not return
                            logger.warning( "Error : book() PreparedStatement was "
                                    + "not closed successfully on update : " + e );

                        }
                    }
                    logger.info( "Successfully booked a free seat in plane " + planeId
                            + " for customer " + customerId );

                    /*
                     * The seat is succesfully booked
                     */
                    connection.commit();
                    return 0;
                }
            } else {
                /*
                 * If the row cannot be found then it should be treated as "-5"
                 * - "for all other errors"
                 */
                logger.severe( "Error : book() Cannot find Seat by PLANE_NO= "
                        + planeId + " AND SEAT_NO= " + seatId + " AND RESERVED= "
                        + customerId );
                connection.commit();
                return -5;
            }
        } catch ( SQLException e ) {
            logger.severe( "Error : book() SQLException on select for update: "
                    + e.getMessage() );
            return -5;
        }
    }

    @Override
    public boolean bookAll( Connection connection, Logger logger, String plane_no ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean clearAllBookings( Connection connection, Logger logger, String plane_no ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAllBooked( Connection connection, Logger logger, String plane_no ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAllReserved( Connection connection, Logger logger, String plane_no ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

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
