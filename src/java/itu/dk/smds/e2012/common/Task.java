package itu.dk.smds.e2012.common;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
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
    public String conditions;
    public String responses;
    
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
            String description, String attendant, boolean required){
        this.id = id;
        this.name = name;
        this.date = date;
        this.status = status;
        this.description = description;
        this.attendantId = attendant;
        this.required = required;
        this.conditions = "";
        this.responses = "";
    }
    
    public String print(){
        String task = "";
        task +="Id: "+id +", Name: " + name + ", Date: "+ date +", Status: " + status +
                ", Required: " + required + ", Description: " + description + ", Attendant: " + attendantId;        
        return task;
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Task) {
            Task task = (Task) obj;
            if ((this.id.equals(task.id)) && (this.name.equals(task.name))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public void addCondition(String condition) {
        if (this.conditions.equals("")) {
            this.conditions += condition;
        } else {
            this.conditions += ", " + condition;
        }
    }
    
    public void addResponse(String response) {
        if (this.responses.equals("")) {
            this.responses += response;
        } else {
            this.responses += ", " + response;
        }
    }
}
