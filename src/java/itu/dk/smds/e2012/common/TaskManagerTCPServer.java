package itu.dk.smds.e2012.common;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.*;

/**
 * Class handling the server
 */
public class TaskManagerTCPServer extends ReceiverAdapter{
    /*
    private static Socket socket;
    private ServerSocket serverSocket;
    private static DataInputStream dis;
    */
    private static Cal cal = CalSerializer.getCal();
    
    private static JChannel channel;
    
    /**
     * Main method for initializing the server
     * @param args the command line arguments
     */
    public void start(String[] args) throws Exception {
                
                channel = new JChannel();
                channel.setReceiver(this);
                //System.out.println("Channel (Name): " + channel.getName());
                //System.out.println("Channel (Address):" + channel.getAddressAsString());                   
                channel.connect("TaskGroup");
                eventLoop();
                channel.close();
    }
        
    private void eventLoop(){    
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            try {
            System.out.print("> "); System.out.flush();
            String line=in.readLine().toLowerCase();
                if(line.startsWith("end") || line.startsWith("close")) {
                    break;
                }

            Message msg=new Message(null, null, line);

            channel.send(msg);

            } catch(Exception e) {
            }

        }
    }
        
    @Override
    public void viewAccepted(View new_view){
        System.out.println("** view: " + new_view);
    }
    
    @Override
    public void receive(Message msg){
        Object[] receiver;
        
        try{
            try{
                String rec = (String) msg.getObject();
                //System.out.println("REC "+ rec);
                if("deleteall".equals(rec)){
                    deleteAll(msg);
                }
            } catch(Exception e) {
            }
            receiver = (Object[]) msg.getObject();
            // Debugging line
            //System.out.println("Idetifier: " + receiver[0] + ", XML: " + receiver[1].toString());
        
        if("POST".equals(receiver[0].toString())){
                    post(msg);
                } else if("PUT".equals(receiver[0])){
                    put(msg);
                } else if("GET".equals(receiver[0])){
                    get(msg);
                } else if("DELETE".equals(receiver[0])){
                    delete(msg);
                } 
        } catch (Exception e){
            System.out.println("Error while parsing command");
            // Send message back to client using "send"
        }
    }    

    private static void post(Message msg){

        // Internal logic for creating a task
        try {
            Object[] arg = (Object[]) msg.getObject();
            Task task = (Task) arg[1];
            cal.POST(task);
        } catch (ClassCastException ex) {
            Logger.getLogger(TaskManagerTCPServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Task creation failed");
        }
    }
    
    private static void put(Message msg){
        // Internal logic for creating a task
        try {
            Object[] arg = (Object[]) msg.getObject();
            Task task = (Task) arg[1];
            cal.PUT(task);
        } catch (ClassCastException ex) {
            Logger.getLogger(TaskManagerTCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void get(Message msg){
        List<Task> list = cal.GET();
        Object[] arg = new Object[]{"SENT",list};
        try{
            channel.send(new Message(null, null, arg));
        } catch (Exception e){
            System.out.println("Unable to send task list");
        }
    }
    
    private static void delete(Message msg){
        try{
            Object[] arg = (Object[]) msg.getObject();
            String str = (String) arg[1];
            cal.DELETE(str);
        } catch (ClassCastException e){
            System.out.println("Couldn't delete task");
        }
    }
    
    private static void deleteAll(Message msg){
        try{
            System.out.println(msg.getScope());
            cal.deleteAllTask();
        } catch (ClassCastException e){
            System.out.println("Couldn't delete task");
        }
    }
    
    /**
     * Creates an user object
     * no longer used.
     * @param name, the name of the user
     * @param password, the password of the user
     */
    private static void createUser(String name, String password){
        cal.addUser(new User(name, password));
    }
    
    /**
     * Method for creating an xml file
     * no longer used
     */
    private static void calToXml(){
        try{
            CalSerializer.makeXmlFile(cal);
        } catch(IOException e) {
            System.out.println("No file printed");
        }
    }
    
    
    public static void main(String[] args) throws Exception{
        new TaskManagerTCPServer().start(args);
    }
}
