package rest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "user")

public class User {

    private String username;

    private String fname;

    private String lname;

    private String email;

    private String date;


    public User() {


    }


    public User(String username, String fname, String lname, String email, String date) {

        super();

        this.username = username;

        this.fname = fname;

        this.lname = lname;

        this.email = email;

        this.date = date;

    }

    @XmlAttribute(required = false)

    public String getUsername() {

        return username;

    }

    public void setUsername(String username) {

        this.username = username;

    }

    public String getFname() {

        return fname;

    }

    public void setFname(String fname) {

        this.fname = fname;

    }

    public String getLname() {

        return lname;

    }

    public void setLname(String lname) {

        this.lname = lname;

    }

    public String getEmail() {

        return email;

    }

    public void setEmail(String email) {

        this.email = email;

    }

    public String getDate() {

        return date;

    }

    public void setDate(String date) {

        this.date = date;

    }

}
