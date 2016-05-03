/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataSource;

import entities.Seat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author mady
 */
public class Reservation {

    private static Connection connection;

    public Reservation(final String id, final String pw) {
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:dat", id, pw);
        } catch (SQLException e) {
            System.out.println("Remember to insert your Oracle ID and PW "+ e);
        }
    }

    public static void releaseConnection(Connection con) {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public void reserve(String plane_no, long id) {
        Seat seat= new Seat();
        try{
        Statement stmt = connection.createStatement();
        String sql = "UPDATE SEAT "
                + "SET RESERVED = '" 
                + id + "' ,  BOOKING_TIME= 111111 "
                + "WHERE PLANE_NO = '"
                + plane_no + "' "
                + "AND ROWNUM < 2";
        stmt.executeUpdate(sql); 
//        seat.setReserved(id);
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }
}
