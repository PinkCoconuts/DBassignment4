package dataSource;

import entities.Seat;
import java.sql.Connection;
import java.util.logging.Logger;

/*
 * This interface includes all method from the requirements. However, we decided
 * to include two parameters more to each method - Connection and Logger objects
 */
public interface ReservationInterface {

    /*
     * A constructor that take two parameters, username and password, and creates 
     * the connection to the database.
     *
     * Here you can see a pretty nice explanation why this is a bad practice : 
     *  http://stackoverflow.com/questions/2804041/constructor-in-an-interface
     * ...if anyone would ever read/check this assignment *passive aggression*
     */
    public void Reservation( String user, String pw );

    /*
     * Where plane_no is the plane to reserve a seat on, and id is a unique 
     * identification of the customer that want to make the reservation. The 
     * function should return a free seat on the plane – a free seat is a seat 
     * that is not booked or reserved (i.e. that is a seat without a reservation 
     * or booking or a seat with an invalid reservation timeout). 
     *
     * The function should write to the database that this seat is reserved by the 
     * specific customer id, this is done by setting the RESERVED attribute equal 
     * to the id and use the BOOKING_TIME to keep track of timeout on the reservation. 
     */
    public Seat reserve( Connection connection, Logger logger, String plane_no, long id );

    /*
     * where plane_no is the plane to reserve a seat on, seat_no the seat to book, 
     * and id is a unique identification of the customer that want to make the booking. 
     * When we register the booking we need to check that the user holds the 
     * reservation and that the reservation did not time out. A booking would be 
     * stored with this information
     *
     * This function should return one of the following: 
     *      o 0 if the seat is successfully booked 
     *      o -1 if the seat is not reserved (it should not be possible to book 
     * a seat without a previous reservation) 
     *      o -2 if the seat is not reserved by the customer id (a seat can only 
     * be booked by the customer that have the reservation on the seat) 
     *      o -3 reservation timeout, i.e. user to slow to make a booking 
     *      o -4 if the seat is already occupied (that’s not good since this is 
     * a overbooking) 
     *      o -5 for all other errors 
     * (NOTE: You can use an enum instead of these numbers if you prefer) 
     * If the function successfully book the seat the BOOKED attribute should be 
     * assigned the customer id and the BOOKING_TIME should be the time of the booking.
     */
    public int book( Connection connection, Logger logger, String plane_no, String seat_no, long id );

    /*
     * furthermore some helper functions to do the testing (and unit testing):
     *
     * book all seats for a plane.
     */
    public boolean bookAll( Connection connection, Logger logger, String plane_no );

    /*
     * reset all bookings (and reservations) for a plane.
     */
    public boolean clearAllBookings( Connection connection, Logger logger, String plane_no );

    /*
     * returns true if all seats ARE booked on a plane, false otherwise. 
     */
    public boolean isAllBooked( Connection connection, Logger logger, String plane_no );

    /*
     * returns true if all free seats are reserved on a plane, false otherwise.
     */
    public boolean isAllReserved( Connection connection, Logger logger, String plane_no );
}
