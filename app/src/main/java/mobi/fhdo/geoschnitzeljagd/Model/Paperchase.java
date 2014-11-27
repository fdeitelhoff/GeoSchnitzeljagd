package mobi.fhdo.geoschnitzeljagd.Model;

import java.io.Serializable;

/**
 * Created by Fabian Deitelhoff on 25.11.2014.
 */
public class Paperchase implements Serializable {

    private int id;
    private User user;
    private String name;
    // TODO: Add the marks!
    //private List<Mark> marks;

    public Paperchase(int id, User user, String name) {
        this.id = id;
        this.user = user;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
