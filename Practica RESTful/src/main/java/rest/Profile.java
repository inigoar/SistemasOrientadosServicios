package rest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;


public class Profile {

    private User user;

    private Status lastStatus;

    private int friendsNumber;

    private ArrayList<Status> friendStatus;


    public Profile() {

        this.friendStatus = new ArrayList<Status>();

    }


    public Profile(User user, Status lastStatus, int friendsNumber) {

        super();

        this.user = user;

        this.lastStatus = lastStatus;

        this.friendsNumber = friendsNumber;

        this.friendStatus = new ArrayList<Status>();

    }

    @XmlAttribute(required = false)

    public User getUser() {

        return user;

    }

    public void setUser(User user) {

        this.user = user;

    }

    public Status getLastStatus() {

        return lastStatus;

    }

    public void setLastStatus(Status lastStatus) {

        this.lastStatus = lastStatus;

    }

    public int getFriendsNumber() {

        return friendsNumber;

    }

    public void setFriendsNumber(int friendsNumber) {

        this.friendsNumber = friendsNumber;

    }

    @XmlElementWrapper(name = "friendsStatus")

    @XmlElement(name = "status")

    //@XmlTransient

    public ArrayList<Status> getFriendStatus() {

        return friendStatus;

    }

    public void setFriendStatus(ArrayList<Status> friendStatus) {

        this.friendStatus = friendStatus;

    }

}