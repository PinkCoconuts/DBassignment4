package threads;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserThread extends Thread {

    private boolean isStopped;
    Random random;
    private String threadName;

    public UserThread(String name) {
        this.isStopped = false;
        random = new Random();
        this.threadName = name;
    }

    public String getThreadName() {
        return threadName;
    }

    public boolean getIsStopped() {
        return isStopped;
    }

    @Override
    public void run() {
        int sleeptime = getTimeToSleep();
        if (!isStopped) {
            try {
                sleep(500);
            } catch (InterruptedException ex) {
                System.out.println("Exception: " + ex);
            }
            System.out.println("The thread " + getThreadName() + " is started");
//        here we should have a switch and call the appropiate method (reserve, book, or whatever) 
        }
//        terminate();
    }

    public void terminate() {
        this.isStopped = true;
        System.out.println("The thread " + getThreadName() + " is stopped");
    }

    private int getTimeToSleep() {
        return random.nextInt(2500) + 500;
    }

}
