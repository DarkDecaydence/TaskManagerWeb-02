package itu.dk.smds.e2012.common;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
/**
 * Class for creating users
 */
/**
 * Constructor for creating a standard user
 */
@XmlRootElement(name = "user")
public class User implements Serializable {
    @XmlAttribute(name = "name")
    public String name;
    @XmlAttribute(name = "password")
    public String password;
    
    /**
     * serialization constructor
     */
    public User(){}
    public User(String name, String password){
        this.name = name;
        this.password = password;
    }
}
