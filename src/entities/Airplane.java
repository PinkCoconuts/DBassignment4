package entities;

public class Airplane {

    private String plane_no;
    private String model;
    private long seats;

    public Airplane(String plane_no, String model, long seats) {
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

    public long getSeats() {
        return seats;
    }

    public void setSeats(long seats) {
        this.seats = seats;
    } 
}
