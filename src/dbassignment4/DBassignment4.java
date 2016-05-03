package dbassignment4;

import dataSource.Reservation;
import entities.Seat;

public class DBassignment4 {

    private static Reservation reservation;
    public static void main(String[] args) {
        reservation= new Reservation("cphbs96", "cphbs96");
        Seat seat= reservation.reserve("CR9", 1000);
        reservation.releaseConnection(reservation.getConnection());
    }
    
}
