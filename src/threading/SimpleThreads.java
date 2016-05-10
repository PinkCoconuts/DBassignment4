package threading;

import java.util.HashMap;
import java.util.Map;
import utilities.Protocol;

public class SimpleThreads {

    private static Map<String, UserThread> userThreadStack = new HashMap();
    private static int threadsCounter = 0, reservedSeats = 0, bookWithNoRes = 0,
            bookWithAnotherUserRes = 0, bookWithTimeout = 0, bookAlreadyBooked = 0,
            internalError = 0;

    private static void finalResults() {
        System.out.println( "\nThe plane is fully booked"
                + "\n\t Number of UserThreads started (total) : " + threadsCounter
                + "\n\t Number of successful bookings : " + reservedSeats
                + "\n\t Number of bookings without a reservation : " + bookWithNoRes
                + "\n\t Number of bookings where the customer was not the one, who was holding the reservation : " + bookWithAnotherUserRes
                + "\n\t Number of bookings where there was a timeout : " + bookWithTimeout
                + "\n\t Number of bookings of occupied seats : " + bookAlreadyBooked
                + "\n\t Counter of unexpected behaviour in the code : " + internalError );
    }

    static void threadMessage( String message ) {

        switch ( message ) {
            case Protocol.successfulBooking:
                reservedSeats++;
                System.out.println( "ReservedSeats : " + reservedSeats );
                break;
            case Protocol.unsuccessfulBooking_NotReserved:
                bookWithNoRes++;
                break;
            case Protocol.unsuccessfulBooking_ReservedByAnotherUser:
                bookWithAnotherUserRes++;
                break;
            case Protocol.unsuccessfulBooking_Timeout:
                bookWithTimeout++;
                break;
            case Protocol.unsuccessfulBooking_AlreadyBooked:
                bookAlreadyBooked++;
                break;
            default:
                internalError++;
                break;
        }

        String threadName = Thread.currentThread().getName();
        System.out.format( "%s: %s%n", threadName, message + " /// threads running : " + userThreadStack.size() );

        userThreadStack.remove( threadName );

        Thread t = Thread.currentThread();
        t.interrupt();

        if ( reservedSeats < 96 ) {
            nextThreadStarter();
        } else {
            if ( userThreadStack.size() == 0 ) {
                finalResults();
            }
        }
    }

    private static void nextThreadStarter() {
        threadsCounter++;
        String nextThreadName = "Thread" + threadsCounter;
        UserThread nextUserThread = new UserThread( nextThreadName );
        Thread thread = new Thread( nextUserThread );
        thread.setName( nextThreadName );
        userThreadStack.put( nextThreadName, nextUserThread );

        thread.start();

        //threadMessage( "Started thread " + thread.getName() + ", alive-status :" + thread.isAlive() + " size : " + userThreadStack.size() );
    }

    public static void main( String args[] ) throws InterruptedException {
        for ( int i = 0; i < 10; i++ ) {
            nextThreadStarter();
        }
    }
}
