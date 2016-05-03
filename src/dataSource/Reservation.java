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

public class Reservation {

    private static Connection connection;

    public Reservation(final String id, final String pw) {
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:dat", id, pw);
        } catch (SQLException e) {
            System.out.println("Remember to insert your Oracle ID and PW " + e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void releaseConnection(Connection con) {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public Seat reserve(String plane_no, long id) {
        Seat seat = new Seat();
        try {
            //update the seat record
            String updateSQL = "UPDATE SEAT "
                    + "SET RESERVED= ? "
                    + ", BOOKING_TIME= ? " //should not be 111111
                    + "WHERE PLANE_NO= ? "
                    + "AND RESERVED IS NULL "
                    + "AND ROWNUM < 2";
            PreparedStatement preparedStatementUpdate = connection.prepareStatement(updateSQL);
            preparedStatementUpdate.setLong(1, id);
            preparedStatementUpdate.setLong(2, 111111);
            preparedStatementUpdate.setString(3, plane_no);
            preparedStatementUpdate.executeUpdate();

            //set the values for the object to be returned
            String selectSQL = "SELECT SEAT_NO FROM SEAT WHERE RESERVED = ?";
            PreparedStatement preparedStatementSelect = connection.prepareStatement(selectSQL);
            preparedStatementSelect.setLong(1, id);
            ResultSet rs = preparedStatementSelect.executeQuery();
            String seat_no = "";
            while (rs.next()) {
                seat_no = rs.getString("SEAT_NO");
            }
            seat.setPlane_no(plane_no);
            seat.setSeat_no(seat_no);
            seat.setReserved(id);
            seat.setBooked_time(111111); //should not be 111111
            if (seat_no.equals("")) {
                System.out.println("Null seat number. Could not find seat");
                return null;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return seat;
    }

    public int book(String plane_no, String seat_no, long id) {
        try {
            //check if the seat is reserved
            String selectSQL = "SELECT RESERVED, BOOKED FROM SEAT "
                    + "WHERE PLANE_NO = ?"
                    + "AND SEAT_NO = ?";
            PreparedStatement preparedStatementSelect = connection.prepareStatement(selectSQL);
            preparedStatementSelect.setString(1, plane_no);
            preparedStatementSelect.setString(2, seat_no);
            ResultSet rs = preparedStatementSelect.executeQuery();
            long reserved = 0,
                    booked = 0;
            while (rs.next()) {
                reserved = rs.getLong("RESERVED");
                booked = rs.getLong("BOOKED");
            }
            if (reserved == 0) {
                return -1;
            } else if (reserved != id) {
                return -2;
            } else if (booked != 0) {
                return -4;
            } else {
                //update the seat record
                String updateSQL = "UPDATE SEAT "
                        + "SET BOOKED= ? "
                        + "WHERE PLANE_NO= ?"
                        + "AND SEAT_NO= ?"
                        + "AND RESERVED= ?"
                        + "AND BOOKING_TIME> 0";
                PreparedStatement preparedStatementUpdate = connection.prepareStatement(updateSQL);
                preparedStatementUpdate.setLong(1, id);
                preparedStatementUpdate.setString(2, plane_no);
                preparedStatementUpdate.setString(3, seat_no);
                preparedStatementUpdate.setLong(4, id);
                preparedStatementUpdate.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return -5;
        }
        return 0;
    }

    public void bookAll(String plane_no) {
        try {
            String updateSQL = "UPDATE SEAT "
                    + "SET BOOKED= ? "
                    + "WHERE PLANE_NO= ? "
                    + "AND RESERVED IS NULL "
                    + "AND BOOKED IS NULL";
            PreparedStatement preparedStatementUpdate = connection.prepareStatement(updateSQL);
            preparedStatementUpdate.setLong(1, 999999);
            preparedStatementUpdate.setString(2, plane_no);
            preparedStatementUpdate.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        }

    }
}
