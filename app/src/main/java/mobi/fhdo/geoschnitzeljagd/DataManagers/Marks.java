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

    public Paperchase forPaperchase(Paperchase paperchase)
    {
        SQLiteDatabase database = null;
        Cursor markCursor = null;

        try
        {
            // This method will be called whenever the user wants to view a paperchase.
            // So we clear the marks beforehand.
            paperchase.getMarks().clear();

            database = getReadableDatabase();

            markCursor = database.rawQuery(
                    "SELECT MID, latitude, longitude, hint, sequence FROM Mark WHERE PID=?",
                    new String[]{paperchase.getId() + ""});

            while (markCursor.moveToNext())
            {
                Mark mark = new Mark(UUID.fromString(markCursor.getString(0)), markCursor.getDouble(1),
                        markCursor.getDouble(2), markCursor.getString(3), markCursor.getInt(4));

                mark.setPaperchaseId(paperchase.getId());
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

    public Mark CreateOrUpdate(Mark mark)
    {
        SQLiteDatabase database = null;
        Cursor markCursor = null;

        try
        {
            database = getWritableDatabase();

            markCursor = database.rawQuery(
                    "SELECT MID FROM mark WHERE MID=?",
                    new String[]{mark.getId().toString()});

            // Wenn es noch keine paperchase gibt
            if (markCursor.getCount() != 1)
            {
                ContentValues values = new ContentValues();
                values.put("mid", mark.getId().toString());
                values.put("pid", mark.getPaperchaseId().toString());
                values.put("latitude", mark.getLatitude());
                values.put("longitude", mark.getLongitude());
                values.put("hint", mark.getHint());
                values.put("sequence", mark.getSequence());

                database.insert("mark", null, values);
            }
            else
            {
                String sql = "Update " + _MARKIERUNG +
                        " set " + _PID + " = '" + mark.getPaperchaseId().toString() + "',"
                        + _LATITUDE + " = '" + mark.getLatitude() + "',"
                        + _LONGITUDE + " = '" + mark.getLongitude() + "',"
                        + _HINWEIS + " = '" + mark.getHint() + "',"
                        + _REIHENFOLGE + " = '" + mark.getSequence() + "'";

                database.execSQL(sql);
            }
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

    public void removeForPaperchase(Paperchase paperchase)
    {
        SQLiteDatabase database = null;

        try
        {
            database = getWritableDatabase();

            database.delete("mark", "pid = ?", new String[]{paperchase.getId() + ""});
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
    }
    public boolean DeleteAllMarks() throws Exception
    {
        SQLiteDatabase database = null;

        try
        {
            database = getWritableDatabase();

            String sql = "Delete From " + _MARKIERUNG;
            database.execSQL(sql);
        }
        finally
        {
            if (database != null)
            {
                database.close();
            }
        }

        return true;
    }
}
