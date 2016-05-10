package threading;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static threading.SimpleThreads.threadMessage;
import utilities.Protocol;

class UserThread implements Runnable {

    private String userThreadName;

    public UserThread( String userThreadName ) {
        this.userThreadName = userThreadName;
    }

    public String getUserThreadName() {
        return userThreadName;
    }

    public void run() {

        Random rand = new Random();
        int returnCode = rand.nextInt( 6 );

        try {
            Thread.sleep( (returnCode + 1) * 1000 );
        } catch ( InterruptedException ex ) {
            Logger.getLogger( SimpleThreads.class.getName() ).log( Level.SEVERE, null, ex );
        }

        String response;

        switch ( returnCode ) {
            case 0:
                response = Protocol.successfulBooking;
                break;
            case 1:
                response = Protocol.unsuccessfulBooking_NotReserved;
                break;
            case 2:
                response = Protocol.unsuccessfulBooking_ReservedByAnotherUser;
                break;
            case 3:
                response = Protocol.unsuccessfulBooking_Timeout;
                break;
            case 4:
                response = Protocol.unsuccessfulBooking_AlreadyBooked;
                break;
            default:
                response = Protocol.internalError;
                break;
        }

        threadMessage( response );
    }
}
