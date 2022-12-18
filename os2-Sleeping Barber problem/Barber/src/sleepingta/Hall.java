
package sleepingta;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Hall {
    private final ReentrantLock mutex = new ReentrantLock();
    private int waitingChairs, noOfTA, availableTA;
    private int TotalAnswerdQuestions, BackLaterCounter;
    private List<Student> StudentList;
    private List<Student> StudentBackLater;
    private Semaphore Availabe;
    private Random r = new Random();
    private Session form;

    public Hall(int nChairs, int nTA, int nStudent, Session form) {
        this.waitingChairs = nChairs;
        this.noOfTA = nTA;
        this.availableTA = nTA;
        this.form = form;
        Availabe = new Semaphore(availableTA);
        this.StudentList = new LinkedList<Student>();
        this.StudentBackLater = new ArrayList<Student>(nStudent);
    }

    

    public int getTotalAnswerdQuestions() {
        return TotalAnswerdQuestions;
    }

    public int getBackLaterCounter() {
        return BackLaterCounter;
    }
    
    public void AnswerQuestion(int TA_ID){
        Student student;
        
        
        synchronized(StudentList){
            while (StudentList.size() == 0) {
                form.SleepTA(TA_ID);
                System.out.println("\nBarber "+TA_ID+" is waiting "
                		+ "for the customers and sleeps");
                try {
                    StudentList.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Hall.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        
            student = (Student)((LinkedList<?>)StudentList).poll();
            System.out.println("Customer "+student.getStudentId()+
            		" finds barber available and ask for a shave"
            		+ "from barber "+TA_ID);
        }
            int Delay;
            try {
                if (Availabe.tryAcquire() && StudentList.size() == waitingChairs){
                Availabe.acquire();
                }
                form.BusyTA(TA_ID);
                System.out.println("barber "+TA_ID+" shave for customer "+
            		student.getStudentId());
                
                double val = r.nextGaussian() * 2000 + 4000;				
        	Delay = Math.abs((int) Math.round(val));				
        	Thread.sleep(Delay);
                
                System.out.println("\nCompleted shaving "+
        			student.getStudentId()+" by barber " + 
        			TA_ID +" in "+(int)(Delay/1000)+ " minutes.");
                mutex.lock();
                try {
                    TotalAnswerdQuestions++;
                } finally {
                    mutex.unlock();
                }
                
                if (StudentList.size() > 0) {
                    System.out.println("barber "+TA_ID+					
            			" Calls a customer to enter shop ");
                    form.ReturnChair(TA_ID);
                }
                Availabe.release();
                
            } catch (InterruptedException e) {
            }
            
            
            
        }
        
        
    
    
    
    public void EnterHall(Student student){
        System.out.println("\nCustomer "+student.getStudentId()+
        		" tries to enter shop to shave at "
        		+student.getInTime());
        
        synchronized(StudentList){
            if (StudentList.size() == waitingChairs) {
                
                System.out.println("\nNo chair available "
                		+ "for customer "+student.getStudentId()+
                		" so he leaves and will come back later");
                
                StudentBackLater.add(student);
                mutex.lock();
                try {
                    BackLaterCounter++;
                } finally {
                    mutex.unlock();
                }
                return;
            }
            else if (Availabe.availablePermits() > 0 ) {
                ((LinkedList<Student>)StudentList).offer(student);
                StudentList.notify();
            }
            else{
                try {
                    ((LinkedList<Student>)StudentList).offer(student);
                    form.TakeChair();
                    System.out.println("All barbers are busy so customer "+
                            student.getStudentId()+
                            " takes a chair in the waiting room");
                    
                    if (StudentList.size() == 1) {
                        StudentList.notify();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Hall.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
        }
    }
    
    public List<Student> Backlater(){
        return StudentBackLater;
    }
    
    
    
}
