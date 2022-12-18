
package sleepingta;


public class TeacherAssistant implements Runnable{
    
    private Hall hall;
    private int TA_ID;

    public TeacherAssistant(Hall hall, int TA_ID) {
        this.hall = hall;
        this.TA_ID = TA_ID;
    }
    
    @Override
    public void run(){
        while (true) {            
            hall.AnswerQuestion(TA_ID);
        }
    }
    
}
