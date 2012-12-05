package itu.dk.smds.e2012.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.*;

/**
 * Class handling the server
 */
public class TaskManagerTCPServer extends ReceiverAdapter{

    private static Cal cal = CalSerializer.getCal();
    private String serverPassword = 
            "thvoid13z6c31h1i714v7bd78s181a899p6j9b70g7ihasdnfpjdsb";
    private Encrypter serverTokenServiceEncrypter;
    
    private JChannel channel;
    private JChannel tokenChannel;
    private Address tokAdd;
    private Address serAdd;
    
    /**
     * Main method for initializing the server
     * @param args the command line arguments
     */
    public void start(String[] args) throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);                   
        channel.connect("ServerCluster");
        serAdd = channel.getAddress();
        serverTokenServiceEncrypter = Encrypter.getInstance(serverPassword);
        channel.send(new Message(null, null, new Object[] {"SENC", serverPassword}));
        /*
        tokenChannel = new JChannel();
        tokenChannel.setReceiver(this);
        tokenChannel.connect("TokenCluster");
        tokAdd = tokenChannel.getAddress();
        */
        
        
        //channel.send(new Message(null, null, new Object[] {"SENC", serverPassword}));
        eventLoop();
        //tokenChannel.close();
        channel.close();
    }
        
    private void eventLoop(){
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            try {
            System.out.print("> "); System.out.flush();
            String line = in.readLine().toLowerCase();
                if(line.startsWith("end") || line.startsWith("close")) {
                    break;
                }
                if(line.startsWith("deleteall")) {
                    deleteAll();
                }
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
        try{
            try{
                String rec = (String) msg.getObject();
                System.out.println("REC "+ rec);
                if("deleteall".equals(rec)){
                    deleteAll();
                }
            } catch(Exception e) {
                //Do nothing, internal command for reseting task list
            }
            
            Object[] rec  = (Object[]) msg.getObject();
            if(rec[0].equals("SENC") || rec[0].equals("GetT") || rec[0].equals("NewT")){
                return;
            }
            System.out.println("The Client message: " + (String) rec[2]);
            String en = serverTokenServiceEncrypter.decryptEncryption((String) rec[2]);
            System.out.println("The Client has sent a With a password that works");
            String[] recIn = en.split(",");
            DateFormat df = DateFormat.getDateInstance();
            Date serverTime = new Date(new Date().getTime()+600000);
            Date time;
            time = df.parse(recIn[1]);
            if(time.after(serverTime)){
                Operation operation = new Operation(msg);
                Thread operationThread = new Thread(operation);
                operationThread.start();
            }
            
        } catch (Exception e){
            System.out.println("Error while parsing command " +e);
                    
        }
    }    
    
    private class Operation implements Runnable {
        
        Message msg;
        String type;
        
        public Operation(Message msg) {
            this.msg = msg;
            
            try{
                
                Object[] receiver = (Object[]) msg.getObject();
                type = receiver[0].toString();
                
            } catch (Exception e){
                type="NULL";
            }
        }
        
        public void run() {
            if (type.equals("POST")) {
                post(msg);
            } else if (type.equals("PUT")) {
                put(msg);
            } else if (type.equals("GET")) {
                get(msg);
            } else if (type.equals("DELETE")) {
                delete(msg);
            } else if(type.equals("NULL")){
                //Do nothing, not a request
            }
        }
        
        private void post(Message msg){

            // Internal logic for creating a task
            try {
                Object[] arg = (Object[]) msg.getObject();
                Task task = (Task) arg[1];
                synchronized(task) {
                    cal.POST(task);
                }
            } catch (ClassCastException ex) {
                Logger.getLogger(TaskManagerTCPServer.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Task creation failed");
            }
        }

        private void put(Message msg){
            // Internal logic for creating a task
            try {
                Object[] arg = (Object[]) msg.getObject();
                Task task = (Task) arg[1];
                cal.PUT(task);
            } catch (ClassCastException ex) {
                Logger.getLogger(TaskManagerTCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void get(Message msg){
            List<Task> list = cal.GET();
            Object[] arg = new Object[]{"SENT",list};
            try{
                channel.send(new Message(null, null, arg));
            } catch (Exception e){
                System.out.println("Unable to send task list");
            }
        }

        private void delete(Message msg){
            try{
                Object[] arg = (Object[]) msg.getObject();
                String str = (String) arg[1];
                synchronized(str) {
                    cal.DELETE(str);
                }
            } catch (ClassCastException e){
                System.out.println("Couldn't delete task");
            }
        }
    }
    
    private void deleteAll(){
            try{
                //System.out.println(msg.getScope());
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
