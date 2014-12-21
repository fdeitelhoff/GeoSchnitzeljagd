package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.UUID;

import mobi.fhdo.geoschnitzeljagd.Model.Mark;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;

public class Marks extends DataManager
{
    public Marks(Context ctx)
    {
        super(ctx);
    }

    public Paperchase ForPaperchase(Paperchase paperchase)
    {
        SQLiteDatabase database = null;
        Cursor markCursor = null;

        try
        {
            database = getReadableDatabase();

            markCursor = database.rawQuery(
                    "SELECT MID, latitude, longitude, hint, sequence FROM Mark WHERE PID=?",
                    new String[]{paperchase.getId() + ""});

            while (markCursor.moveToNext())
            {
                Mark mark = new Mark(UUID.fromString(markCursor.getString(0)), markCursor.getDouble(1),
                        markCursor.getDouble(2), markCursor.getString(3), markCursor.getInt(4));

                paperchase.getMarks().add(mark);
            }
        }
        catch (Exception e)
        {
            Log.w("Exception", e);
        }
        finally
        {
            if (markCursor != null)
            {
                markCursor.close();
            }

            if (database != null)
            {
                database.close();
            }
        }

        return paperchase;
    }

    public Mark Create(Mark mark)
    {
        SQLiteDatabase database = null;

        try
        {
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
        }
        catch (Exception e)
        {
            Log.w("Exception", e.toString());
        }
        finally
        {
            if (database != null)
            {
                database.close();
            }
        }

        return mark;
    }
}
