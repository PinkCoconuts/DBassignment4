package threads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MasterThread extends Thread {

    private List<UserThread> userThreadsList;
    private List<UserThread> waitingUserThreadsList;
    private int numberOfUserThreadsStarted;

    public MasterThread() {
        this.userThreadsList = new ArrayList();
        this.waitingUserThreadsList = new ArrayList();
        this.numberOfUserThreadsStarted = 0;
    }

    @Override
    public void run() {
        System.out.println("The master thread started"); //here we should call the reserve class
    }

    public void addReserveThread(UserThread userThread) {
        if (userThreadsList.size() > 0) {
            checkForTerminatedThreads();
        }
        if (userThreadsList.size() < 10) {
            userThreadsList.add(userThread);
            userThread.start();
            try {
                userThread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("The thread can't sleep, because: " + ex);
            }
        } else {
            waitingUserThreadsList.add(userThread);
        }
    }

    public void checkForTerminatedThreads() {
        for (UserThread userThread : userThreadsList) {
            if (userThread.getIsStopped()) {
                removeThread(userThread);
            }
        }
    }

    public void removeThread(UserThread userThread) {
//        userThreadsList.remove(userThread);
        /*if (waitingUserThreadsList.size() != 0) {
            System.out.println("" + waitingUserThreadsList.size());
            UserThread waitingUserThread = waitingUserThreadsList.get(0);
            addReserveThread(userThread);
        } */
    }

}
