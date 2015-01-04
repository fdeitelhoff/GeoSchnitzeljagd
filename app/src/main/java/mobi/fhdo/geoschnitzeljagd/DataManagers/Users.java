package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.sql.Timestamp;
import java.util.UUID;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserLoginException;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserNotExistsException;
import mobi.fhdo.geoschnitzeljagd.Model.User;

public class Users extends DataManager
{
    public Users(Context ctx)
    {
        super(ctx);
    }

    public User Login(User user) throws UserLoginException
    {
        SQLiteDatabase database = null;
        Cursor userCursor = null;
        User loggedInUser = null;

        try
        {
            database = getReadableDatabase();

            userCursor = database.rawQuery(
                    "SELECT UID, username, password, timestamp FROM User WHERE username=? AND password=?",
                    new String[]{user.getUsername(), user.getPassword()});

            if (userCursor.getCount() != 1)
            {
                throw new UserLoginException("Der Benutzername oder das Passwort stimmen nicht überein. Bitte versuchen Sie es erneut.");
            }

            while (userCursor.moveToNext())
            {
                loggedInUser = new User(UUID.fromString(userCursor.getString(0)),
                        userCursor.getString(1),
                        userCursor.getString(2), new Timestamp(userCursor.getLong(3)));
            }
        }
        finally
        {
            if (userCursor != null)
            {
                userCursor.close();
            }

            if (database != null)
            {
                database.close();
            }
        }

        return loggedInUser;
    }

    public User Get(UUID userId) throws UserNotExistsException
    {
        if (userId == null)
        {
            throw new IllegalArgumentException("Die UserID darf nicht gleich Null sein!");
        }

        SQLiteDatabase database = null;
        Cursor userCursor = null;
        User user = null;

        try
        {
            database = getReadableDatabase();

            userCursor = database.rawQuery(
                    "SELECT UID, username, password, timestamp FROM User WHERE UID=?",
                    new String[]{userId.toString()});

            if (userCursor.getCount() != 1)
            {
                throw new UserNotExistsException("Der Benutzer mit der ID '" + userId + "' existiert nicht!");
            }

            while (userCursor.moveToNext())
            {
                user = new User(UUID.fromString(userCursor.getString(0)),
                        userCursor.getString(1),
                        userCursor.getString(2),
                        new Timestamp(userCursor.getLong(3)));
            }
        }
        finally
        {
            if (userCursor != null)
            {
                userCursor.close();
            }

            if (database != null)
            {
                database.close();
            }
        }

        return user;
    }

    public boolean CreateOrUpdate(User user) throws Exception
    {
        SQLiteDatabase database = null;
        Cursor userCursor = null;

        try
        {
            database = getReadableDatabase();

            userCursor = database.rawQuery(
                    "SELECT UID, username, password, timestamp FROM User WHERE UID=?",
                    new String[]{user.getId().toString()});

            // Wenn es noch keinen User gibt
            if (userCursor.getCount() != 1)
            {
                String sql = "Insert into " + _USER + " (" + _UID + "," + _USERNAME + "," + _PASSWORD + "," + _TIMESTAMP + ") values('" + user.getId().toString() + "','" + user.getUsername().toString() + "','" + user.getPassword().toString() + "','" + user.getTimestamp() + "')";
                database.execSQL(sql);
            } else
            {
                String sql = "Update " + _USER +
                        " set " + _USERNAME + " = '" + user.getUsername() + "',"
                        + _PASSWORD + " = '" + user.getPassword() + "',"
                        + _TIMESTAMP + " = '" + user.getTimestamp() + "'"
                        + " where " + _UID + " = '" + user.getId() + "'";
                database.execSQL(sql);
            }


        }
        finally
        {
            if (userCursor != null)
            {
                userCursor.close();
            }

            if (database != null)
            {
                database.close();
            }
        }

        return true;
    }

    public boolean Delete(User user) throws Exception
    {
        SQLiteDatabase database = null;
        Cursor userCursor = null;

        try
        {
            database = getReadableDatabase();

            String sql = "Delete From " + _USER + "WHERE UID='" + user.getId().toString() + "'";
            database.execSQL(sql);

        }
        finally
        {
            if (userCursor != null)
            {
                userCursor.close();
            }

            if (database != null)
            {
                database.close();
            }
        }

        return true;
    }

    public boolean DeleteAllUsers() throws Exception
    {
        SQLiteDatabase database = null;
        Cursor userCursor = null;

        try
        {
            database = getReadableDatabase();

            String sql = "Delete From " + _USER;
            database.execSQL(sql);
        }
        finally
        {
            if (userCursor != null)
            {
                userCursor.close();
            }

            if (database != null)
            {
                database.close();
            }
        }

        return true;
    }
}
