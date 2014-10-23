package mobi.fhdo.geoschnitzeljagd.Model;

/**
 * Created by Fabian Deitelhoff on 23.10.2014.
 */
public class User {

    private int id;
    private String username;
    private String password;

    public User(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    public User(int id, String username, String password) {
        this(username, password);

        setId(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
