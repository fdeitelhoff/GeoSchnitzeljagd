package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserLoginException;
import mobi.fhdo.geoschnitzeljagd.Model.User;

public class Users extends DataManager {
    public Users(Context ctx) {
        super(ctx);
    }

    public User Login(User user) throws UserLoginException {
        SQLiteDatabase database = null;
        User loggedInUser = null;

        try {
            database = getReadableDatabase();

            Cursor userCursor = database.rawQuery(
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
            //} catch (Exception e) {
            //    e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return loggedInUser;
    }
}
