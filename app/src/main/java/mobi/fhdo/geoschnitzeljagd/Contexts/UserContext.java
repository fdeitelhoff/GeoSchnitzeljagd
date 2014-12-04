package mobi.fhdo.geoschnitzeljagd.Contexts;

import mobi.fhdo.geoschnitzeljagd.Model.User;

/**
 * Created by Fabian Deitelhoff on 04.12.2014.
 */
public class UserContext {

    private static UserContext instance = null;
    private User user;

    private UserContext() {
    }

    public static UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }

        return instance;
    }

    public User getLoggedInUser() {
        return user;
    }

    public void userLoggedIn(User user) {
        this.user = user;
    }
}
