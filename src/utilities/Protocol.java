package utilities;

public class Protocol {

    //0 Booked 
    public static final String successfulBooking = "BOOKED#";

    //-1 Not booked error
    public static final String unsuccessfulBooking_NotReserved = "NOTRESERVEDBYME#";

    //-2 Not booked error
    public static final String unsuccessfulBooking_ReservedByAnotherUser = "ALREADYRESERVED#";

    //-3 Not booked error
    public static final String unsuccessfulBooking_Timeout = "UNBOOKED#";

    //-4 Not booked error
    public static final String unsuccessfulBooking_AlreadyBooked = "ALREADYBOOKED#";

    //Booking refusal
    public static final String refusalToBookASeat = "INOWANNANO#";

    //Reserved
    public static final String successfulReservation = "RESERVED#";

    //Not Reserved
    public static final String unsuccessfulReservation = "FULL#";

    //General error message
    public static final String internalError = "OUCH#";
}
