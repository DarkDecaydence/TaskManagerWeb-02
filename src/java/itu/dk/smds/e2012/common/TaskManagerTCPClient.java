package itu.dk.smds.e2012.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.jgroups.*;

/**
 * The task manager client
 */
public class TaskManagerTCPClient extends ReceiverAdapter {

    private JChannel channel;
    private JChannel tokenChannel;
    private Encrypter clientTokenServiceEncrypter;
    private String accessToken;
    //private String host;
    
    /**
     * The method for starting the client
     * @param args the command line arguments
     */
    private void start() {
        try {
            channel = new JChannel();
            channel.setReceiver(this);
            channel.connect("ServerCluster");
            
            String[] details = promptForDetails();
            requestNewToken(details[0], details[1]);
            
            eventLoop();
            channel.close();
        } catch (Exception e){
            
        }
    }
    
    public String[] promptForDetails() {
        String[] details = new String[2];
        details[0] = JOptionPane.showInputDialog("Enter username@hostname",
                        System.getProperty("user.name")
                        + "@localhost");
        
        details[1] = JOptionPane.showInputDialog("Enter password:");
        
        return details;
    }
    
    private void requestNewToken(String user, String passwd) throws Exception {
        Message msg = new Message(null, null, new Object[] {"GetT", user, passwd} );
        channel.send(msg);
    }
    
    public String getToken() 
    { return accessToken; }
    
    private void eventLoop(){
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

//        try {
//            Task handin2 = new Task("handin-02", "Submit assignment-02",
//                    "21-12-2012", "not-executed",
//                    "Work on mandatory assignment and send hand-in to TA-01",
//                    "Mikkel, Alexander, Niklas, Morten", false);
//            
//            Task review2 = new Task("review-02", "Review and check assignment-02",
//                    "23-12-2012", "not-executed",
//                    "Check the assignment sent by students and approve/reject.",
//                    "TA-01, Rao", false);
//            
//            handin2.addResponse(review2.id);
//            review2.addCondition(handin2.id);
//            
//            Message msg = new Message(null, null, new Object[] {"POST", handin2, accessToken});
//            Message msg2 = new Message(null, null, new Object[] {"POST", review2,accessToken});
//            
//            channel.send(msg);
//            channel.send(msg2);
//        } catch (Exception e) {
//        }
        
        
        while(true) {
            int count = 0;
            while(count<1){
                if(accessToken != null){
                try {
                    Task handin2 = new Task("handin-02", "Submit assignment-02",
                            "21-12-2012", "not-executed",
                            "Work on mandatory assignment and send hand-in to TA-01",
                            "Mikkel, Alexander, Niklas, Morten", false);

                    Task review2 = new Task("review-02", "Review and check assignment-02",
                            "23-12-2012", "not-executed",
                            "Check the assignment sent by students and approve/reject.",
                            "TA-01, Rao", false);

                    handin2.addResponse(review2.id);
                    review2.addCondition(handin2.id);

                    Message msg = new Message(null, null, new Object[] {"POST", handin2, accessToken});
                    Message msg2 = new Message(null, null, new Object[] {"POST", review2,accessToken});

                    channel.send(msg);
                    channel.send(msg2);
                } catch (Exception e) {
                    System.out.println(e);
                }
                count++;
                }
            }
            try {
                System.out.print("> "); System.out.flush();

                String line = in.readLine().toLowerCase();

                if(line.startsWith("end") || line.startsWith("close")) {
                    break;
                }
            } catch(Exception e) {
            }            
        }
    }
    
    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    @Override
    public void receive(Message msg) {
        Object[] receiver = (Object[]) msg.getObject();
        
        if("SENT".equals(receiver[0].toString())){
            List<Task> arg = (List<Task>) receiver[1];
            for(Task t : arg){
                System.out.println("Task: " + t.print());
            }
        } else if("NewT".equals(receiver[0].toString())) {
            System.out.println("The token have arrived");
            accessToken = (String) receiver[1];
            Date date = new Date();
            String timestamp = date.toString();
            String user = "msto";
            String host = "itu.dk";
            accessToken = user + "@"+ host + ", " + timestamp;
            if(accessToken == null){
                System.out.println("Bite me");
            }
        }

    }
    
    public static void main(String[] args){
        new TaskManagerTCPClient().start();
    }
}
