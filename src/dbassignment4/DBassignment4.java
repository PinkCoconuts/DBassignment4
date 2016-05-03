package dbassignment4;

import dataSource.Reservation;
import entities.Seat;

public class DBassignment4 {

    private static Reservation reservation;

    public static void main(String[] args) {
        reservation = new Reservation("cphbs96", "cphbs96");
//        Seat seat = reservation.reserve("CR9", 1200);
//        System.out.println("Seat number: "+ seat.getSeat_no());
        reservation.book("CR9", "A1", 1200);
        reservation.releaseConnection(reservation.getConnection());
    }

}
