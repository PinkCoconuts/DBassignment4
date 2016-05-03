package dataSource;

import entities.Seat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            if(seat_no.equals("")){
                System.out.println("Null seat number. Could not find seat");
                return null;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return seat;
    }
}
