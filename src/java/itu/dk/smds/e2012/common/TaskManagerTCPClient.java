package itu.dk.smds.e2012.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import javax.swing.JOptionPane;
import org.jgroups.*;
import org.jgroups.stack.IpAddress;

/**
 * The task manager client
 */
public class TaskManagerTCPClient extends ReceiverAdapter {

    private JChannel channel;
    private Encrypter clientTokenServiceEncrypter;
    private String accessToken;
    private String host;
    
    /**
     * The method for starting the client
     * @param args the command line arguments
     */
    private void start() {
        try {
            channel = new JChannel();
            channel.setReceiver(this);
            channel.connect("ServerCluster1");
            
            promptForHost();
            clientTokenServiceEncrypter = TokenService.getNewEncrypter(host.substring(0, host.indexOf('@')));
            String cryptedToken = getNewToken();
            accessToken = clientTokenServiceEncrypter.decryptEncryption(cryptedToken);
            
            eventLoop();
            channel.close();
        } catch (Exception e){
            
        }
    }
    
    public void promptForHost() {
        host = JOptionPane.showInputDialog("Enter username@hostname",
                        System.getProperty("user.name")
                        + "@localhost");
    }
    
    public String getNewToken() {
        String passwd = JOptionPane.showInputDialog("Enter password:");
        
        return TokenService.getNewToken(host, passwd);
    }
    
    public String getToken() 
    { return accessToken; }
    
    private void eventLoop(){
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
//        //Create task
//        try{
//            Message msg = new Message(null, null, new Object[]{"GET","0001"});
//            channel.send(msg);
//        } catch (Exception e){
//            System.out.println("Error occured whilst parsing object");
//        }
//        try{               
//            
//            Message msg = new Message(null, null, new Object[]{"POST",new Task("0001" , "Do MDS Mandatory Exercise 1","18-09-2012",
//                    "initialized","Task Manager simple setup", "Mikkel; Alex; Niklas; Morten")});
//            channel.send(msg);
//        } catch (Exception e){
//            System.out.println("Error occured whilst parsing object");
//        }
//        
//        try{
//            Message msg = new Message(null, null, new Object[]{"POST",new Task("0002" , "Clean up code","26-09-2012",
//                "initialized","Code needs to shine", "Mikkel; Alex; Niklas; Morten")});
//            channel.send(msg);
//        } catch (Exception e){
//            System.out.println("Error occured whilst parsing object");
//        }
//        
//        try{
//            Message msg = new Message(null, null, new Object[]{"POST",new Task("0003", "Mess with your dog", "30-10-2012",
//                "initialized","It is getting restless", "Mikkel; Alex; Niklas; Morten")});
//            channel.send(msg);
//        } catch (Exception e){
//            System.out.println("Error occured whilst parsing object");
//        }
//        
//        // Change a task
//        try{
//            Message msg = new Message(null, null, new Object[]{"PUT",new Task("0001", "Do MDS Mandatory Exercise 1","18-09-2012",
//                    "done","Task Manager simple setup", "Mikkel")});
//            channel.send(msg);
//        } catch (Exception e){
//            System.out.println("Error occured whilst parsing object");
//        }
//        
//        // Delete a task
//        try{
//            Message msg = new Message(null, null, new Object[]{"DELETE","0003"});
//            channel.send(msg);
//        } catch (Exception e){
//            System.out.println("Error occured whilst parsing object");
//        }
//        
//        // get a list og task
//        try{
//            Message msg = new Message(null, null, new Object[]{"GET","0001"});
//            channel.send(msg);
//        } catch (Exception e){
//            System.out.println("Error occured whilst parsing object");
//        }
//        
//        try {
//            Message msg = new Message(null, null, new Object[]{"POST", 
//                new Task("0022", "Eat your vegetables","18-09-2012",
//                    "done","Task Manager simple setup", "Mikkel")});
//            channel.send(msg);
//            
//            msg = new Message(null, null, new Object[]{"PUT", 
//                new Task("0022", "Eat more rice","18-09-2012",
//                    "done","Task Manager simple setup", "Mikkel")});
//            channel.send(msg);
//            
//            msg = new Message(null, null, new Object[]{"DELETE","0022"});
//            channel.send(msg);
//        } catch (Exception e) {
//            System.out.println("Error occured whilst parsing object");
//        }
        
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
            
            Message msg = new Message(null, null, new Object[] {"POST", handin2});
            Message msg2 = new Message(null, null, new Object[] {"POST", review2});
            
            channel.send(msg);
            channel.send(msg2);
        } catch (Exception e) {
        }
        
        
        while(true) {
            try {
                System.out.print("> "); System.out.flush();

                String line = in.readLine().toLowerCase();

                if(line.startsWith("end") || line.startsWith("close")) {
                    break;
                }

                int count = 0;
                Message msg;
                //line="[" + "Client" + "] " + line;
                if(count < 1){
                    msg = new Message(null,null,new String[]{"Hej","ObjectJob"});
                    count++;
                } else {
                    msg=new Message(null, null, line);
                }
                
                channel.send(msg);

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
        }

    }
    
    public static void main(String[] args){
        new TaskManagerTCPClient().start();
    }
}
