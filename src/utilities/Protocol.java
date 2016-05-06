package utilities;

public class Protocol {
    
    //0 Booked 
    public static String successfulBooking = "BOOKED#";

    //-1 Not booked error
    public static String unsuccessfulBooking_NotReserved = "NOTRESERVEDBYME#";

    //-2 Not booked error
    public static String unsuccessfulBooking_ReservedByAnotherUser = "ALREADYRESERVED#";

    //-3 Not booked error
    public static String unsuccessfulBooking_Timeout = "UNBOOKED#";

    //-4 Not booked error
    public static String unsuccessfulBooking_AlreadyBooked = "ALREADYBOOKED#";
    
    //Booking refusal
    public static String refusalToBookASeat = "INOWANNANO#";

    //Reserved
    public static String successfulReservation = "RESERVED#";

    //Not Reserved
    public static String unsuccessfulReservation = "FULL#";

    //General error message
    public static String internalError = "OUCH#";
}
