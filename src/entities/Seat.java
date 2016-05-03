package entities;

public class Seat {
    private String plane_no;
    private String seat_no;
    private long reserved;
    private long booked;
    private long booked_time;

    public Seat() {
    }

    public Seat(String plane_no, String seat_no, long reserved, long booked, long booked_time) {
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

    public long getReserved() {
        return reserved;
    }

    public void setReserved(long reserved) {
        this.reserved = reserved;
    }

    public long getBooked() {
        return booked;
    }

    public void setBooked(long booked) {
        this.booked = booked;
    }

    public long getBooked_time() {
        return booked_time;
    }

    public void setBooked_time(long booked_time) {
        this.booked_time = booked_time;
    }
    
    
}
