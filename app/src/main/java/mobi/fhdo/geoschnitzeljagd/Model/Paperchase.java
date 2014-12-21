package mobi.fhdo.geoschnitzeljagd.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Paperchase implements Serializable {

    private UUID id;
    private User user;
    private String name;
    private List<Mark> marks;

    public Paperchase(UUID id, User user, String name) {
        this(user, name);
        this.id = id;
    }

    public Paperchase(User user, String name) {
        this.user = user;
        this.name = name;

        marks = new ArrayList<Mark>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
