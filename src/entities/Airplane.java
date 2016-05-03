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
public class Airplane {

    private String plane_no;
    private String model;
    private int seats;

    public Airplane(String plane_no, String model, int seats) {
        this.plane_no = plane_no;
        this.model = model;
        this.seats = seats;
    }

    public String getPlane_no() {
        return plane_no;
    }

    public void setPlane_no(String plane_no) {
        this.plane_no = plane_no;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    
}
