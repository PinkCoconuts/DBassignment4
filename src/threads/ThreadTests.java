/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

/**
 *
 * @author mady
 */
public class ThreadTests {

    public static void main(String[] args) {
        MasterThread masterThread = new MasterThread();
        masterThread.run();
        UserThread threadReserve1 = new UserThread();
        UserThread threadReserve2 = new UserThread();
        UserThread threadReserve3 = new UserThread();
        UserThread threadReserve4 = new UserThread();
        UserThread threadReserve5 = new UserThread();
        UserThread threadReserve6 = new UserThread();
        UserThread threadReserve7 = new UserThread();
        UserThread threadReserve8 = new UserThread();
        UserThread threadReserve9 = new UserThread();
        UserThread threadReserve10 = new UserThread();
        UserThread threadReserve11 = new UserThread();
        UserThread threadReserve12 = new UserThread();
        masterThread.addReserveThread(threadReserve1);
        masterThread.addReserveThread(threadReserve2);
        masterThread.addReserveThread(threadReserve3);
        masterThread.addReserveThread(threadReserve4);
        masterThread.addReserveThread(threadReserve5);
        masterThread.addReserveThread(threadReserve6);
        masterThread.addReserveThread(threadReserve7);
        masterThread.addReserveThread(threadReserve8);
        masterThread.addReserveThread(threadReserve9);
        masterThread.addReserveThread(threadReserve10);
        masterThread.addReserveThread(threadReserve11);
        masterThread.addReserveThread(threadReserve12);
    }
}
