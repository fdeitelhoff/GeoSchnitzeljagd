package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mobi.fhdo.geoschnitzeljagd.Model.Mark;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.Model.User;

/**
 * Created by Fabian Deitelhoff on 25.11.2014.
 */
public class Paperchases extends DataManager {

    private Marks marks;

    public Paperchases(Context ctx) {
        super(ctx);

        marks = new Marks(ctx);
    }

    public List<Paperchase> Own(User user) {
        SQLiteDatabase database = null;
        Cursor paperchaseCursor = null;
        List<Paperchase> paperchases = new ArrayList<Paperchase>();

        try {
            database = getReadableDatabase();

            paperchaseCursor = database.rawQuery(
                    "SELECT PID, name FROM paperchase WHERE UID=?",
                    new String[]{user.getId() + ""});

            while (paperchaseCursor.moveToNext()) {
                Paperchase paperchase = new Paperchase(paperchaseCursor.getInt(0), user, paperchaseCursor.getString(1));

                paperchases.add(paperchase);
            }
        } catch (Exception e) {
            Log.w("Exception", e.toString());
        } finally {
            if (paperchaseCursor != null) {
                paperchaseCursor.close();
            }

            if (database != null) {
                database.close();
            }
        }

        return paperchases;
    }


    public List<Paperchase> Search(String text) {
        SQLiteDatabase database = null;
        Cursor paperchaseCursor = null;
        List<Paperchase> paperchases = new ArrayList<Paperchase>();
        Users users = null;

        try {
            database = getReadableDatabase();

            paperchaseCursor = database.rawQuery("SELECT * FROM paperchase WHERE name LIKE '%" + text.toString() + "%'", null);

            while (paperchaseCursor.moveToNext()) {
                Paperchase paperchase = new Paperchase(paperchaseCursor.getInt(0), null, paperchaseCursor.getString(2));

                paperchases.add(paperchase);
            }
        } catch (Exception e) {
            Log.w("Exception", e.toString());
        } finally {
            if (paperchaseCursor != null) {
                paperchaseCursor.close();
            }

            if (database != null) {
                database.close();
            }
        }

        return paperchases;
    }

    public Paperchase Create(Paperchase paperchase) {
        SQLiteDatabase database = null;

        try {
            database = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("uid", paperchase.getUser().getId());
            values.put("name", paperchase.getName());

            int id = (int) database.insert("paperchase", null, values);

            paperchase.setId(id);

            for (Mark mark : paperchase.getMarks()) {
                mark.setPaperchaseId(paperchase.getId());

                Mark newMark = marks.Create(mark);

                mark.setId(newMark.getId());
            }
        } catch (Exception e) {
            Log.w("Exception", e.toString());
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return paperchase;
    }
}