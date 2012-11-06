package itu.dk.smds.e2012.common;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
/**
 * Class responsible for tasks
 */
@XmlRootElement(name = "task")
public class Task implements Serializable {
    @XmlAttribute(name = "id")
    public String id;
    @XmlAttribute(name = "name")
    public String name;
    @XmlAttribute(name = "date")
    public String date;
    @XmlAttribute(name = "status")
    public String status;
    @XmlAttribute(name = "required")
    public boolean required;
    
    public String description;
    public String attendantId;
    /**
     * Constructor for serialization purpose
     */
    public Task(){}
    /**
     * Constructor for creating a task
     * @param id, id of the task
     * @param name, name of the task
     * @param date, the date of the task
     * @param status, the status of the task
     * @param description, the task description
     * @param attendant, the attendants of the task 
     */
    public Task(String id, String name, String date, String status,
            String description, String attendant){
        this.id = id;
        this.name = name;
        this.date = date;
        this.status = status;
        this.description = description;
        this.attendantId = attendant;
    }
    
    public String print(){
        String task = "";
        task +="Id: "+id +", Name: " + name + ", Date: "+ date +", Status: " + status +
                ", Required: " + required + ", Description: " + description + ", Attendant: " + attendantId;        
        return task;
    }
    
    @Override
    public boolean equals(Object obj){
        try {
            Task task = (Task) obj;
            if (    (this.id.equals(task.id)) && 
                    (this.name.equals(task.name)) &&
                    (this.date.equals(task.date)) &&
                    (this.status.equals(task.status)) &&
                    (this.required == task.required) &&
                    (this.description.equals(task.description)) &&
                    (this.attendantId.equals(task.attendantId)))
            {
                return true;
            } else {
                return false;
            }
        } catch (ClassCastException e) {
            return false;
        }
    }
}
