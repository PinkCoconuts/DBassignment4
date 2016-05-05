package threads;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserThread extends Thread {

    private boolean isStopped;

    public UserThread() {
        this.isStopped= false;
    }

    public boolean getIsStopped() {
        return isStopped;
    }

    @Override
    public void run() {
        while (!isStopped) {
            System.out.println("The thread is started");
            this.isStopped= true;
        }
//        here we should have a switch and call the appropiate method (reserve, book, or whatever) 
    }

}
