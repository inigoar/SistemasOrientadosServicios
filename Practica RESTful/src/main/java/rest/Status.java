package rest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "status")
public class Status {
    private int id;
    private String username;
    private String content;
    private String date;


    public Status() {

    }

    public Status(int id, String username, String content, String date) {
        super();
        this.username = username;
        this.id = id;
        this.content = content;
        this.date = date;
    }

    @XmlAttribute(required = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}