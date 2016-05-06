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
    public boolean bookAll( Connection connection, Logger logger, String planeId ) {
        try {
            connection.setAutoCommit( false );
        } catch ( SQLException e ) {
            logger.severe( "Error : bookAll() SQLException : Set of auto commit to "
                    + "false failed : " + e );
            return false;
        }

        try {
            String selectSQL = "SELECT SEAT_NO "
                    + "FROM SEAT "
                    + "WHERE PLANE_NO= ? AND BOOKED IS NULL "
                    + "FOR UPDATE";

            PreparedStatement preparedStatement = connection.prepareStatement( selectSQL );

            preparedStatement.setString( 1, planeId );

            ResultSet rs = preparedStatement.executeQuery();

            String seatId;
            int reservationId = 0;
            while ( rs.next() ) {

                seatId = rs.getString( "SEAT_NO" );

                preparedStatement = null;

                Date date = new Date();

                try {
                    reservationId++;
                    String updateSQL = "UPDATE SEAT "
                            + "SET RESERVED= ?, BOOKED= ?, BOOKING_TIME= ? "
                            + "WHERE PLANE_NO= ? AND SEAT_NO= ?";

                    preparedStatement = connection.prepareStatement( updateSQL );

                    preparedStatement.setInt( 1, reservationId );
                    preparedStatement.setInt( 2, reservationId );
                    preparedStatement.setLong( 3, date.getTime() );
                    preparedStatement.setString( 4, planeId );
                    preparedStatement.setString( 5, seatId );

                    preparedStatement.executeUpdate();
                } catch ( SQLException e ) {
                    logger.severe( "Error : bookAll() SQLException on update: "
                            + e.getMessage() );
                    return false;
                } finally {
                    try {
                        if ( preparedStatement != null ) {
                            preparedStatement.close();
                        }
                    } catch ( SQLException e ) {
                        //Not fatal, do not return
                        logger.warning( "Error : bookAll() PreparedStatement was "
                                + "not closed successfully on update : " + e );

                    }
                }
                logger.info( "bookAll() Successfully booked a seat " + seatId
                        + " in plane " + planeId );
            }
            /*
             * The seat is succesfully booked
             */
            logger.info( "bookAll() Successfully booked all free seats" );
            connection.commit();
            return true;
        } catch ( SQLException e ) {
            logger.severe( "Error : bookAll() SQLException on select for update: "
                    + e.getMessage() );
            return false;
        }
    }

    @Override
    public boolean clearAllBookings( Connection connection, Logger logger,
            String planeId ) {
        try {
            connection.setAutoCommit( false );
        } catch ( SQLException e ) {
            logger.severe( "Error : clearAllBookings() SQLException : Set of auto "
                    + "commit to false failed : " + e );
            return false;
        }

        try {
            String selectSQL = "SELECT SEAT_NO "
                    + "FROM SEAT "
                    + "WHERE ( PLANE_NO= ? AND BOOKED IS NOT NULL ) "
                    + "OR ( PLANE_NO= ? AND RESERVED IS NOT NULL ) "
                    + "FOR UPDATE";

            PreparedStatement preparedStatement = connection.prepareStatement( selectSQL );

            preparedStatement.setString( 1, planeId );
            preparedStatement.setString( 2, planeId );

            ResultSet rs = preparedStatement.executeQuery();

            String seatId;
            while ( rs.next() ) {

                seatId = rs.getString( "SEAT_NO" );

                preparedStatement = null;

                try {
                    String updateSQL = "UPDATE SEAT "
                            + "SET RESERVED= ?, BOOKED= ?, BOOKING_TIME= ? "
                            + "WHERE PLANE_NO= ? AND SEAT_NO= ?";

                    preparedStatement = connection.prepareStatement( updateSQL );

                    preparedStatement.setNull( 1, java.sql.Types.INTEGER );
                    preparedStatement.setNull( 2, java.sql.Types.INTEGER );
                    preparedStatement.setNull( 3, java.sql.Types.INTEGER );
                    preparedStatement.setString( 4, planeId );
                    preparedStatement.setString( 5, seatId );

                    preparedStatement.executeUpdate();
                } catch ( SQLException e ) {
                    logger.severe( "Error : clearAllBookings() SQLException on update: "
                            + e.getMessage() );
                    return false;
                } finally {
                    try {
                        if ( preparedStatement != null ) {
                            preparedStatement.close();
                        }
                    } catch ( SQLException e ) {
                        //Not fatal, do not return
                        logger.warning( "Error : clearAllBookings() PreparedStatement "
                                + "was not closed successfully on update : " + e );

                    }
                }
                logger.info( "clearAllBookings() Successfully unbooked/unregistered "
                        + "a seat " + seatId + " in plane " + planeId );
            }
            /*
             * The seat is succesfully booked
             */
            logger.info( "clearAllBookings() Successfully unbooked/unregistered "
                    + "all free seats" );
            connection.commit();
            return true;
        } catch ( SQLException e ) {
            logger.severe( "Error : clearAllBookings() SQLException on select for "
                    + "update: " + e.getMessage() );
            return false;
        }
    }

    @Override
    public boolean isAllBooked( Connection connection, Logger logger, String planeId ) {
        PreparedStatement preparedStatement = null;
        try {
            String selectSQL = "SELECT BOOKED FROM SEAT WHERE PLANE_NO = ?";

            preparedStatement = connection.prepareStatement( selectSQL );

            preparedStatement.setString( 1, planeId );

            ResultSet rs = preparedStatement.executeQuery();
            int booked = 0;
            while ( rs.next() ) {
                booked = rs.getInt( "BOOKED" );
                if ( booked <= 0 ) {
                    logger.info( "isAllBooked() Reports that NOT all seats are booked." );
                    return false;
                }
            }
        } catch ( SQLException e ) {
            logger.severe( "Error : isAllBooked() SQLException on selection of "
                    + "booked seats: " + e.getMessage() );
            return false;
        } finally {
            try {
                if ( preparedStatement != null ) {
                    preparedStatement.close();
                }
            } catch ( SQLException e ) {
                //Not fatal, do not return
                logger.warning( "Error : isAllBooked() PreparedStatement was "
                        + "not closed successfully on update : " + e );

            }
        }
        logger.info( "isAllBooked() Reports that all seats are booked." );
        return true;
    }

    @Override
    public boolean isAllReserved( Connection connection, Logger logger, String planeId ) {
        PreparedStatement preparedStatement = null;
        try {
            String selectSQL = "SELECT RESERVED FROM SEAT WHERE PLANE_NO = ?";

            preparedStatement = connection.prepareStatement( selectSQL );

            preparedStatement.setString( 1, planeId );

            ResultSet rs = preparedStatement.executeQuery();
            int reserved = 0;
            while ( rs.next() ) {
                reserved = rs.getInt( "RESERVED" );
                if ( reserved <= 0 ) {
                    logger.info( "isAllReserved() Reports that NOT all seats are reserved." );
                    return false;
                }
            }
        } catch ( SQLException e ) {
            logger.severe( "Error : isAllReserved() SQLException on selection of "
                    + "reserved seats: " + e.getMessage() );
            return false;
        } finally {
            try {
                if ( preparedStatement != null ) {
                    preparedStatement.close();
                }
            } catch ( SQLException e ) {
                //Not fatal, do not return
                logger.warning( "Error : isAllReserved() PreparedStatement was "
                        + "not closed successfully on update : " + e );

            }
        }
        logger.info( "isAllReserved() Reports that all seats are reserved." );
        return true;
    }
}
