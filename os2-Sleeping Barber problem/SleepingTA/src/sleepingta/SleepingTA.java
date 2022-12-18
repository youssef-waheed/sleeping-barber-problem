
package sleepingta;


import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SleepingTA {
    
    private int noOfStudents;
    private int noOfChairs;
    private int noOfTA;

    public SleepingTA(int noOfStudents, int noOfChairs, int noOfTA) {
        this.noOfStudents = noOfStudents;
        this.noOfChairs = noOfChairs;
        this.noOfTA = noOfTA;
    }
    
    
    
    public void Start(Session form) throws InterruptedException{
        ExecutorService exec = Executors.newFixedThreadPool(12);
        Hall hall = new Hall(noOfChairs, noOfTA, noOfStudents, form);
        Random r = new Random();
        
        System.out.println("Hall is opened with "+noOfTA+" TAs");
        
        long startTime  = System.currentTimeMillis();
        
        for (int i = 1; i <= noOfTA; i++) {
            TeacherAssistant TA = new TeacherAssistant(hall, i);
            Thread thTA = new Thread(TA);
            exec.execute(thTA);
        }
        
        for (int i = 1; i <= noOfStudents; i++) {
            try {
                Student student = new Student(hall);
                student.setInTime(new Date());
                student.setStudentId(i);
                Thread thStudent = new Thread(student);
                exec.execute(thStudent);
                
                double val = r.nextGaussian() * 2000 + 2000;			
                int Delay = Math.abs((int) Math.round(val));		
                Thread.sleep(Delay);
                
                
            } catch (InterruptedException ex) {
                Logger.getLogger(SleepingTA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        List<Student> backLater = hall.Backlater();
        if (backLater.size() > 0 ) {
            for (int i = 0; i < backLater.size(); i++) {
            try {
                Student student = backLater.get(i);
                student.setInTime(new Date());
                Thread thStudent = new Thread(student);
                exec.execute(thStudent);
                
                double val = r.nextGaussian() * 2000 + 2000;			
                int Delay = Math.abs((int) Math.round(val));		
                Thread.sleep(Delay);
                
                
            } catch (InterruptedException ex) {
                Logger.getLogger(SleepingTA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        }
        
        exec.awaitTermination(12, SECONDS);
        exec.shutdown();
        
        long elapsedTime = (System.currentTimeMillis() - startTime)/1000;
        
        System.out.println("Hall is closed");
        System.out.println("\nTotal time elapsed in seconds"
        		+ " for Answering "+noOfStudents+" students' Questions by "
        		+noOfTA+" TAs with "+noOfChairs+
        		" chairs in the waiting room is: "
        		+elapsedTime);
        System.out.println("\nTotal students: "+noOfStudents+
        		"\nTotal students served: "+hall.getTotalAnswerdQuestions()
        		+"\nTotal studets returned: "+hall.getBackLaterCounter());
    }
    
}