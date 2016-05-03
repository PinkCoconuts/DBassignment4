package dbassignment4;

import dataSource.Reservation;
import entities.Seat;

public class DBassignment4 {

    private static Reservation reservation;

    public static void main(String[] args) {
        reservation = new Reservation("cphbs96", "cphbs96");
//        Seat seat = reservation.reserve("CR9", 1200);
//        System.out.println("Seat number: "+ seat.getSeat_no());
//        System.out.println("Result from the booked method: "+ reservation.book("CR9", "A1", 1200));
//        reservation.bookAll("CR9");
//        reservation.clearAllBookings("CR9");
//        System.out.println("Are all booked? "+ reservation.isAllBooked("CR9"));
        System.out.println("Are all reserved? "+ reservation.isAllReserved("CR9"));
        reservation.releaseConnection(reservation.getConnection());
    }

}
