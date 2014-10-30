package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserLoginException;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserNotExistsException;
import mobi.fhdo.geoschnitzeljagd.Model.User;

public class Users extends DataManager {
    public Users(Context ctx) {
        super(ctx);
    }

    public User Login(User user) throws UserLoginException {
        SQLiteDatabase database = null;
        Cursor userCursor = null;
        User loggedInUser = null;

        try {
            database = getReadableDatabase();

            userCursor = database.rawQuery(
                    "SELECT UID, username, password FROM User WHERE username=? AND password=?",
                    new String[]{user.getUsername(), user.getPassword()});

            if (userCursor.getCount() != 1) {
                throw new UserLoginException("Der Benutzername oder das Passwort stimmen nicht überein. Bitte versuchen Sie es erneut.");
            }

            while (userCursor.moveToNext()) {
                loggedInUser = new User(userCursor.getInt(0),
                        userCursor.getString(1),
                        userCursor.getString(2));
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }

            if (database != null) {
                database.close();
            }
        }

        return loggedInUser;
    }

    public User Get(Integer userId) throws UserNotExistsException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Die UserID darf nicht kleiner gleich Null sein!");
        }

        SQLiteDatabase database = null;
        Cursor userCursor = null;
        User user = null;

        try {
            database = getReadableDatabase();

            userCursor = database.rawQuery(
                    "SELECT UID, username, password FROM User WHERE UID=?",
                    new String[]{userId.toString()});

            if (userCursor.getCount() != 1) {
                throw new UserNotExistsException("Der Benutzer mit der ID '" + userId + "' existiert nicht!");
            }

            while (userCursor.moveToNext()) {
                user = new User(userCursor.getInt(0),
                        userCursor.getString(1),
                        userCursor.getString(2));
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }

            if (database != null) {
                database.close();
            }
        }

        return user;
    }
}
