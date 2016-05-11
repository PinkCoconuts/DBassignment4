#Concurrency Booking Simulator (please read this before using this software)

Developed by Madalina Dragan and Boyko Surlev
    
>This code is part of our education in Copenhagen Business Academy - Software Development - Semester №6 
(or Semester №1 of Bachelor) - Database Assignment №4 - Concurency.

##Used technologies

Java, Oracle SQL, Threading (2x Observer Pattern and Basic threading)

##What is the purpose of this?

The purpose of this code is to simulate reservations and bookings of seats in an airplane flight. It includes a 
Master Thread which spawns User Threads. The User Threads will try to make reservation and bookings on a 
random seat in the specified airplane flight.

##How do I run this?
>The current implementation includes two solution - an Observer Pattern solution and a Simple Threading solution.

Before running any of the solutions make sure that you have a connection to a running SQL Server. Go to any 
*Thread.java class which you will use during the execution of this code and change the databaseHost, databaseUsername
and databasePassword variables, so that they can match with the Oracle SQL Database, which you would like to use.

After doing this you can run one of the two Threading solutions implemented in this code

###To run the Observer Pattern Treading solutions 
  Run the observerPattern.masterThread.java's main method and wait for 4-8 minutes (~600 User Threads). At the end 
  you will receive statistics regarding the executed operations

###To run the Simple Threading solution 
  Run the simpleThreading.simpleThreads.java's main method and wait for 2-6 minutes (~750 User Threads). At the end 
  you will receive statistics regarding the executed operations

>Our observations conclude that the usage of the Simple Thread solution is much stable, faster and CPU friendly.

####Disclaimer
    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE 
    INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE 
    FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM 
    LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, 
    ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

