package mobi.fhdo.geoschnitzeljagd.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabian Deitelhoff on 25.11.2014.
 */
public class Paperchase implements Serializable {

    private int id;
    private User user;
    private String name;
    private List<Mark> marks;

    public Paperchase(int id, User user, String name) {
        this(user, name);
        this.id = id;
    }

    public Paperchase(User user, String name) {
        this.user = user;
        this.name = name;

        marks = new ArrayList<Mark>();
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

    public void addMark(Mark mark) {
        marks.add(mark);
    }

    public void removeMark(Mark mark) {
        marks.remove(mark);
    }

    public List<Mark> getMarks() {
        return marks;
    }
}
