package itu.dk.smds.e2012.common;
import java.io.*;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.*;

/**
 * Class responsible for serialization of the cal class, creating an xml file.
 *
 * @author GIGAR
 */

public class CalSerializer {
    // assign path to the Xml, 
    static String path = System.getProperty("user.dir") + "/web/WEB-INF/task-manager-xml.xml";
    
    /**
     * Method responsible for converting a cal object to an xml document.
     * @param cal, the object to be serialized
     * @throws IOException 
     */
    public static void makeXmlFile(Cal cal) throws IOException{
        try {
            // create an instance context class, to serialize/deserialize.
            JAXBContext jaxbContext = JAXBContext.newInstance(Cal.class);

          
            // Serialize cal object into xml.
            StringWriter writer = new StringWriter();

            // We can use the same context object, as it knows how to 
            //serialize or deserialize Cal class.
            jaxbContext.createMarshaller().marshal(cal, writer);

            System.out.println("Printing serialized cal Xml before saving into file!");
            
            // Print the serialized Xml to Console.
            System.out.println(writer.toString());
            
            // Finally save the Xml back to the file.
            SaveFile(writer.toString(), path);

        } catch (JAXBException ex) {
            Logger.getLogger(CalSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method for saving the xml file
     * @param xml, the xml to be saved
     * @param path, the path location of the file
     * @throws IOException 
     */
    private static void SaveFile(String xml, String path) throws IOException {
        File file = new File(path);
        
        // create a bufferedwriter to write Xml
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        output.write(xml);
        output.close();
    }
    
    public static Cal getCal(){
        Cal returnCal = null;
        try {
        // create an instance context class, to serialize/deserialize.
        JAXBContext jaxbContext = JAXBContext.newInstance(Cal.class);

        // Create a file input stream for the university Xml.
        FileInputStream stream = new FileInputStream(path);

        // deserialize university xml into java objects.
         returnCal = (Cal) jaxbContext.createUnmarshaller().unmarshal(stream);
        }
        catch (Exception e) { System.out.println("Coud not properly initialize: " + e); }
        return returnCal;
    }
    
    public static String taskToXmlString(Task task) throws Exception{
        StringWriter writer = new StringWriter();
        try {
        JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);
        

        jaxbContext.createMarshaller().marshal(task, writer);
        }
        catch (Exception e) {
            System.out.println("Error, could not create task:" + e);
        }
        return writer.toString();
    }
    

    /**
     * Turns an object into an XML string.
     * Note: If the object does not have the prerequisites for JAXBContext,
     * the XML String may not be created properly.
     */
    public static String userToXmlString(User user) throws Exception {
        StringWriter writer = new StringWriter();
        try {
        JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
        
        jaxbContext.createMarshaller().marshal(user, writer);
        }
        catch (Exception e) {
            System.out.println("Error, could not create user:" + e);
        }
        return writer.toString();
    }
    
    /**
     * Turns an XML string into a Task object.
     * @param xml, the XML string to be made into a Task
     */
    public static Task makeTaskFromXML (String xml) throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream (xml.getBytes()); 
        JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);
        
        //JAXB doesn't support converting a String of XML into an Object,
        //So we have to convert it to a Stream first.
        //Note: Could alternatively make the object directly from an
        //InputStream
        Task task = (Task) jaxbContext.createUnmarshaller().unmarshal( input );
        return task;
    }
    
    /**
     * Turns an XML string into a User object.
     * @param xml, the XML string to be made into an User
     */
    public static User makeUserFromXML (String xml) throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream (xml.getBytes()); 
        JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
        
        User user = (User) jaxbContext.createUnmarshaller().unmarshal( input );
        return user;
    }
    /**
     * Main method - Can be used for testing
     */
    /*public static void main(String[] args)
    {
        Task task = new Task("id","name","date","status","desc","att");
        String taskXML = null;
        try {
            taskXML = taskToXmlString(task);
        }
        catch (Exception e)
        {
        }
        System.out.println(taskXML);
        
        Task newTask = null;
        try {
            newTask = makeTaskFromXML(taskXML);
        }
        catch (Exception e)
        {
        }
        System.out.println(task.print());
        System.out.println(newTask.print());
        assert(task.print() == newTask.print());
        assert(task.toString() != newTask.toString());
    }*/
}