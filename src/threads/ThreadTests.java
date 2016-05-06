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
        UserThread threadReserve1 = new UserThread("threadReserve1");
        UserThread threadReserve2 = new UserThread("threadReserve2");
        UserThread threadReserve3 = new UserThread("threadReserve3");
        UserThread threadReserve4 = new UserThread("threadReserve4");
        UserThread threadReserve5 = new UserThread("threadReserve5");
        UserThread threadReserve6 = new UserThread("threadReserve6");
        UserThread threadReserve7 = new UserThread("threadReserve7");
        UserThread threadReserve8 = new UserThread("threadReserve8");
        UserThread threadReserve9 = new UserThread("threadReserve9");
        UserThread threadReserve10 = new UserThread("threadReserve10");
        UserThread threadReserve11 = new UserThread("threadReserve11");
        UserThread threadReserve12 = new UserThread("threadReserve12");
        UserThread threadReserve13 = new UserThread("threadReserve13");
        UserThread threadReserve14 = new UserThread("threadReserve14");
        UserThread threadReserve15 = new UserThread("threadReserve15");
        UserThread threadReserve16 = new UserThread("threadReserve16");
        UserThread threadReserve17 = new UserThread("threadReserve17");
        UserThread threadReserve18 = new UserThread("threadReserve18");
        UserThread threadReserve19 = new UserThread("threadReserve19");
        UserThread threadReserve20 = new UserThread("threadReserve20");
        UserThread threadReserve21 = new UserThread("threadReserve21");
        UserThread threadReserve22 = new UserThread("threadReserve22");
        UserThread threadReserve23 = new UserThread("threadReserve23");
        UserThread threadReserve24 = new UserThread("threadReserve24");
        UserThread threadReserve25 = new UserThread("threadReserve25");
        UserThread threadReserve26 = new UserThread("threadReserve26");
        UserThread threadReserve27 = new UserThread("threadReserve27");

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
        masterThread.removeThread(threadReserve2);
        masterThread.addReserveThread(threadReserve12);
        masterThread.addReserveThread(threadReserve13);
        masterThread.addReserveThread(threadReserve14);
        masterThread.removeThread(threadReserve4);
        masterThread.addReserveThread(threadReserve15);
        masterThread.addReserveThread(threadReserve16);
        masterThread.addReserveThread(threadReserve17);
        masterThread.addReserveThread(threadReserve18);
        masterThread.addReserveThread(threadReserve19);
        masterThread.addReserveThread(threadReserve20);
        masterThread.addReserveThread(threadReserve21);
        masterThread.addReserveThread(threadReserve22);
        masterThread.addReserveThread(threadReserve23);
        masterThread.addReserveThread(threadReserve24);
        masterThread.addReserveThread(threadReserve25);
        masterThread.addReserveThread(threadReserve26);
        masterThread.addReserveThread(threadReserve27);

    }
}
