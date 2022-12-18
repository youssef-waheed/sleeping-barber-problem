
package sleepingta;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Student implements Runnable{
    
    private int StudentId;
    private Hall hall;
    private Date inTime;

    public Student(Hall hall) {
        this.hall = hall;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public void setStudentId(int StudentId) {
        this.StudentId = StudentId;
    }

    public Date getInTime() {
        return inTime;
    }

    public int getStudentId() {
        return StudentId;
    }
    
    
    
    
    @Override
    public void run(){
        try {
            AskQuestion();
        } catch (InterruptedException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private synchronized void AskQuestion() throws InterruptedException {							//customer is added to the list
       
        hall.EnterHall(this);
    }
    
}
