package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.UUID;

import mobi.fhdo.geoschnitzeljagd.Model.Mark;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;

public class Marks extends DataManager {
    public Marks(Context ctx) {
        super(ctx);
    }

    public Paperchase forPaperchase(Paperchase paperchase) {
        SQLiteDatabase database = null;
        Cursor markCursor = null;

        try {
            // This method will be called whenever the user wants to view a paperchase.
            // So we clear the marks beforehand.
            paperchase.getMarks().clear();

            database = getReadableDatabase();

            markCursor = database.rawQuery(
                    "SELECT MID, latitude, longitude, hint, sequence FROM Mark WHERE PID=?",
                    new String[]{paperchase.getId() + ""});

            while (markCursor.moveToNext()) {
                Mark mark = new Mark(UUID.fromString(markCursor.getString(0)), markCursor.getDouble(1),
                        markCursor.getDouble(2), markCursor.getString(3), markCursor.getInt(4));

                mark.setPaperchaseId(paperchase.getId());
                paperchase.getMarks().add(mark);
            }
        } catch (Exception e) {
            Log.w("Exception", e);
        } finally {
            if (markCursor != null) {
                markCursor.close();
            }

            if (database != null) {
                database.close();
            }
        }

        return paperchase;
    }

    public Mark create(Mark mark) {
        SQLiteDatabase database = null;

        try {
            database = getWritableDatabase();

            UUID newID = mark.getId() != null ? mark.getId() : UUID.randomUUID();

            ContentValues values = new ContentValues();
            values.put("mid", newID.toString());
            values.put("pid", mark.getPaperchaseId().toString());
            values.put("latitude", mark.getLatitude());
            values.put("longitude", mark.getLongitude());
            values.put("hint", mark.getHint());
            values.put("sequence", mark.getSequence());

            database.insert("mark", null, values);

            mark.setId(newID);
        } catch (Exception e) {
            Log.w("Exception", e.toString());
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return mark;
    }

    public void removeForPaperchase(Paperchase paperchase) {
        SQLiteDatabase database = null;

        try {
            database = getWritableDatabase();

            database.delete("mark", "pid = ?", new String[]{paperchase.getId() + ""});
        } catch (Exception e) {
            Log.w("Exception", e.toString());
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

    /*public Mark update(Mark mark) {
        SQLiteDatabase database = null;

        try {
            database = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("latitude", mark.getLatitude());
            values.put("longitude", mark.getLongitude());
            values.put("hint", mark.getHint());
            values.put("sequence", mark.getSequence());

            database.update("mark", values, "mid = ?", new String[]{mark.getId() + ""});
        } catch (Exception e) {
            Log.w("Exception", e.toString());
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return mark;
    }*/
}
