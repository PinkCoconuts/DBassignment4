package threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MasterThread extends Thread {

    private HashMap<Integer, UserThread> userThreadsHashMap;
    private HashMap<Integer, UserThread> waitingThreadsHashMap;
    private int numberOfUserThreadsStarted;
    private int indexRunningThreadsList = 0, indexWaitingThreadsList = 0; //the index on which the next running thread should be added in the hashMap

    public MasterThread() {
        this.userThreadsHashMap = new HashMap<Integer, UserThread>();
        this.waitingThreadsHashMap = new HashMap<Integer, UserThread>();
        this.numberOfUserThreadsStarted = 0;
    }

    @Override
    public void run() {
        System.out.println( "RUN IS TRIGGERED MASTER THREAD" );

        UserThread threadReserve1 = new UserThread( "threadReserve1" );
        UserThread threadReserve2 = new UserThread( "threadReserve2" );
        UserThread threadReserve3 = new UserThread( "threadReserve3" );
        UserThread threadReserve4 = new UserThread( "threadReserve4" );
        UserThread threadReserve5 = new UserThread( "threadReserve5" );
        UserThread threadReserve6 = new UserThread( "threadReserve6" );
        UserThread threadReserve7 = new UserThread( "threadReserve7" );
        UserThread threadReserve8 = new UserThread( "threadReserve8" );
        UserThread threadReserve9 = new UserThread( "threadReserve9" );
        UserThread threadReserve10 = new UserThread( "threadReserve10" );
        UserThread threadReserve11 = new UserThread( "threadReserve11" );
        UserThread threadReserve12 = new UserThread( "threadReserve12" );
        UserThread threadReserve13 = new UserThread( "threadReserve13" );
        UserThread threadReserve14 = new UserThread( "threadReserve14" );
        UserThread threadReserve15 = new UserThread( "threadReserve15" );
        UserThread threadReserve16 = new UserThread( "threadReserve16" );
        UserThread threadReserve17 = new UserThread( "threadReserve17" );
        UserThread threadReserve18 = new UserThread( "threadReserve18" );
        UserThread threadReserve19 = new UserThread( "threadReserve19" );
        UserThread threadReserve20 = new UserThread( "threadReserve20" );
        UserThread threadReserve21 = new UserThread( "threadReserve21" );
        UserThread threadReserve22 = new UserThread( "threadReserve22" );
        UserThread threadReserve23 = new UserThread( "threadReserve23" );
        UserThread threadReserve24 = new UserThread( "threadReserve24" );
        UserThread threadReserve25 = new UserThread( "threadReserve25" );
        UserThread threadReserve26 = new UserThread( "threadReserve26" );
        UserThread threadReserve27 = new UserThread( "threadReserve27" );

        addReserveThread( threadReserve1 );
        addReserveThread( threadReserve2 );
        addReserveThread( threadReserve3 );
        addReserveThread( threadReserve4 );
        addReserveThread( threadReserve5 );
        addReserveThread( threadReserve6 );
        addReserveThread( threadReserve7 );
        addReserveThread( threadReserve8 );
        addReserveThread( threadReserve9 );
        addReserveThread( threadReserve10 );
        addReserveThread( threadReserve11 );
        removeThread( threadReserve2 );
        addReserveThread( threadReserve12 );
        addReserveThread( threadReserve13 );
        addReserveThread( threadReserve14 );
        removeThread( threadReserve4 );
        addReserveThread( threadReserve15 );
        addReserveThread( threadReserve16 );
        addReserveThread( threadReserve17 );
        addReserveThread( threadReserve18 );
        addReserveThread( threadReserve19 );
        addReserveThread( threadReserve20 );
        addReserveThread( threadReserve21 );
        addReserveThread( threadReserve22 );
        addReserveThread( threadReserve23 );
        addReserveThread( threadReserve24 );
        addReserveThread( threadReserve25 );
        addReserveThread( threadReserve26 );
        addReserveThread( threadReserve27 );
    }

    public void addReserveThread( UserThread userThread ) {
        checkForTerminatedThreads();
        if ( userThreadsHashMap.size() < 10 ) {
            try {
                userThreadsHashMap.put( indexRunningThreadsList, userThread );
                indexRunningThreadsList++;
                userThread.start();
                userThread.join();
            } catch ( InterruptedException ex ) {
                System.out.println( "Exception: " + ex );
            }
        } else {
            waitingThreadsHashMap.put( indexWaitingThreadsList, userThread );
            indexWaitingThreadsList++;
        }
    }

    public void checkForTerminatedThreads() {
        for ( Map.Entry<Integer, UserThread> entry : userThreadsHashMap.entrySet() ) {
            Integer key = entry.getKey();
            if ( entry.getValue().getIsStopped() ) {
                removeThread( entry.getValue() );
            }
        }
    }

    public void removeThread( UserThread userThread ) {
        userThread.terminate();
        userThreadsHashMap.remove( returnKeyForValue( userThread ) );
        if ( waitingThreadsHashMap.size() > 0 ) {
            Map.Entry<Integer, UserThread> entry = waitingThreadsHashMap.entrySet().iterator().next();
            int key = entry.getKey();
            UserThread waitingUserThread = entry.getValue();
            waitingThreadsHashMap.remove( key );
            addReserveThread( waitingUserThread ); //adding the first waiting thread
        }
    }

    public int returnKeyForValue( UserThread userThread ) {
        for ( int currKey : userThreadsHashMap.keySet() ) {
            if ( userThreadsHashMap.get( currKey ).equals( userThread ) ) {
                return currKey;
            }
        }
        return 0;
    }

}
