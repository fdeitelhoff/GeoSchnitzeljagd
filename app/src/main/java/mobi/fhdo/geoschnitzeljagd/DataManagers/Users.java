package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import mobi.fhdo.geoschnitzeljagd.Model.User;

public class Users extends DataManager {
    public Users(Context ctx) {
        super(ctx);
    }

    public User Login(User user) {
        SQLiteDatabase database = null;
        User loggedInUser = null;

        try {
            database = getReadableDatabase();

            Cursor userCursor = database.rawQuery(
                    "SELECT ID, Username, Password FROM User WHERE Username=? AND Password=?",
                    new String[]{user.getUsername(), user.getPassword()});

            while (userCursor.moveToNext()) {
                loggedInUser = new User(userCursor.getInt(0),
                        userCursor.getString(1),
                        userCursor.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return loggedInUser;
    }
}
