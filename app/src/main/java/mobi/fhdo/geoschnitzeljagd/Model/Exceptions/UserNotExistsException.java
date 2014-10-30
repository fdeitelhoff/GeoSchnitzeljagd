package mobi.fhdo.geoschnitzeljagd.Model.Exceptions;

/**
 * Created by Fabian Deitelhoff on 23.10.2014.
 * <p/>
 * Exception when a user not exists in a Database.
 */
public class UserNotExistsException extends Exception {

    public UserNotExistsException(String message) {
        super(message);
    }
}
