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
    }

    public void addReserveThread(UserThread userThread) {
        checkForTerminatedThreads();
        if (userThreadsHashMap.size() < 10) {
            try {
                userThreadsHashMap.put(indexRunningThreadsList, userThread);
                indexRunningThreadsList++;
                userThread.start();
                userThread.join();
            } catch (InterruptedException ex) {
                System.out.println("Exception: " + ex);
            }
        } else {
            waitingThreadsHashMap.put(indexWaitingThreadsList, userThread);
            indexWaitingThreadsList++;
        }
    }

    public void checkForTerminatedThreads() {
        for (Map.Entry<Integer, UserThread> entry : userThreadsHashMap.entrySet()) {
            Integer key = entry.getKey();
            if (entry.getValue().getIsStopped()) {
                removeThread(entry.getValue());
            }
        }
    }

    public void removeThread(UserThread userThread) {
        userThread.terminate();
        userThreadsHashMap.remove(returnKeyForValue(userThread));
        if (waitingThreadsHashMap.size() > 0) {
            Map.Entry<Integer, UserThread> entry = waitingThreadsHashMap.entrySet().iterator().next();
            int key = entry.getKey();
            UserThread waitingUserThread = entry.getValue();
            waitingThreadsHashMap.remove(key);
            addReserveThread(waitingUserThread); //adding the first waiting thread
        }
    }

    public int returnKeyForValue(UserThread userThread) {
        for (int currKey : userThreadsHashMap.keySet()) {
            if (userThreadsHashMap.get(currKey).equals(userThread)) {
                return currKey;
            }
        }
        return 0;
    }

}
