package rest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "friend")

public class Friend {

    private int id;

    private String username;

    private String friendname;


    public Friend() {


    }


    public Friend(int id, String username, String friendname) {

        super();

        this.username = username;

        this.id = id;

        this.friendname = friendname;

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

    public String getFriendname() {

        return friendname;

    }

    public void setFriendname(String friendname) {

        this.friendname = friendname;

    }

}