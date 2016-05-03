package dbassignment4;

import dataSource.Reservation;

public class DBassignment4 {

    private static Reservation reservation;
    public static void main(String[] args) {
        reservation= new Reservation("cphcd77", "cphcd77");
        reservation.reserve("CR9", 1000);
    }
    
}
