/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

/**
 *
 * @author mady
 */
public class Seat {
    private String plane_no;
    private String seat_no;
    private int reserved;
    private int booked;
    private int booked_time;

    public Seat() {
    }

    public Seat(String plane_no, String seat_no, int reserved, int booked, int booked_time) {
        this.plane_no = plane_no;
        this.seat_no = seat_no;
        this.reserved = reserved;
        this.booked = booked;
        this.booked_time = booked_time;
    }

    public String getPlane_no() {
        return plane_no;
    }

    public void setPlane_no(String plane_no) {
        this.plane_no = plane_no;
    }

    public String getSeat_no() {
        return seat_no;
    }

    public void setSeat_no(String seat_no) {
        this.seat_no = seat_no;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getBooked() {
        return booked;
    }

    public void setBooked(int booked) {
        this.booked = booked;
    }

    public int getBooked_time() {
        return booked_time;
    }

    public void setBooked_time(int booked_time) {
        this.booked_time = booked_time;
    }
    
    
}
