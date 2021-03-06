package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mobi.fhdo.geoschnitzeljagd.Model.Mark;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.Model.User;


public class Paperchases extends DataManager
{

    private Marks marks;
    private Context contextBuffer;

    public Paperchases(Context ctx)
    {
        super(ctx);

        contextBuffer = ctx;

        marks = new Marks(ctx);
    }

    public List<Paperchase> own(User user)
    {
        SQLiteDatabase database = null;
        Cursor paperchaseCursor = null;
        List<Paperchase> paperchases = new ArrayList<Paperchase>();

        try
        {
            database = getReadableDatabase();

            paperchaseCursor = database.rawQuery(
                    "SELECT PID, name, timestamp FROM paperchase WHERE UID=? ORDER BY name",
                    new String[]{user.getId() + ""});

            while (paperchaseCursor.moveToNext())
            {
                Paperchase paperchase = new Paperchase(UUID.fromString(paperchaseCursor.getString(0)), user, paperchaseCursor.getString(1), Timestamp.valueOf(paperchaseCursor.getString(2)));

                paperchases.add(paperchase);
            }
        }
        catch (Exception e)
        {
            Log.w("Exception", e.toString());
        }
        finally
        {
            if (paperchaseCursor != null)
            {
                paperchaseCursor.close();
            }

            if (database != null)
            {
                database.close();
            }
        }

        return paperchases;
    }

    public Paperchase Get(UUID paperchasesId)
    {
        if (paperchasesId == null)
        {
            throw new IllegalArgumentException("Die PaperchasesId darf nicht gleich Null sein!");
        }

        SQLiteDatabase database = null;
        Cursor paperchaseCursor = null;
        Paperchase paperchase = null;

        Users users = new Users(contextBuffer);
        users.getReadableDatabase();

        try
        {
            database = getReadableDatabase();

            paperchaseCursor = database.rawQuery(
                    "SELECT pid, uid, name, timestamp FROM paperchase WHERE PID=?",
                    new String[]{paperchasesId.toString()});

            if (paperchaseCursor.getCount() != 1)
            {
                throw new Exception("Paperchase mit der ID '" + paperchasesId + "' existiert nicht!");
            }

            while (paperchaseCursor.moveToNext())
            {
                paperchase = new Paperchase(UUID.fromString(paperchaseCursor.getString(0)),
                        users.Get(UUID.fromString(paperchaseCursor.getString(1))),
                        paperchaseCursor.getString(2),
                        Timestamp.valueOf(paperchaseCursor.getString(3)));
            }

            if(paperchase != null)
            {
                marks.forPaperchase(paperchase);
            }
        }
        catch (Exception e)
        {
            Log.d("Get Paperchase", e.getMessage());
        }
        finally
        {
            if (paperchaseCursor != null)
            {
                paperchaseCursor.close();
            }

            if (database != null)
            {
                database.close();
            }
        }

        return paperchase;
    }


    public List<Paperchase> search(String text)
    {
        SQLiteDatabase database = null;
        Cursor paperchaseCursor = null;
        List<Paperchase> paperchases = new ArrayList<Paperchase>();

        try
        {
            database = getReadableDatabase();

            paperchaseCursor = database.rawQuery("SELECT * FROM paperchase WHERE name LIKE '%" + text.toString() + "%'", null);

            while (paperchaseCursor.moveToNext())
            {
                Paperchase paperchase = new Paperchase(UUID.fromString(paperchaseCursor.getString(0)), null, paperchaseCursor.getString(2), Timestamp.valueOf(paperchaseCursor.getString(3)));

                paperchases.add(paperchase);
            }
        }
        catch (Exception e)
        {
            Log.w("Exception", e.toString());
        }
        finally
        {
            if (paperchaseCursor != null)
            {
                paperchaseCursor.close();
            }

            if (database != null)
            {
                database.close();
            }
        }

        return paperchases;
    }

    public List<Paperchase> All()
    {
        SQLiteDatabase database = null;
        Cursor paperchaseCursor = null;
        Cursor paperchaseMarksCursor = null;
        List<Paperchase> paperchases = new ArrayList<Paperchase>();
        Users users = null;

        try
        {
            database = getReadableDatabase();

            paperchaseCursor = database.rawQuery("SELECT * FROM paperchase", null);

            while (paperchaseCursor.moveToNext())
            {
                Paperchase paperchase = new Paperchase(UUID.fromString(paperchaseCursor.getString(0)), null, paperchaseCursor.getString(2), Timestamp.valueOf(paperchaseCursor.getString(3)));
                paperchaseMarksCursor = database.rawQuery("SELECT mid,pid,latitude, longitude, hint, sequence from mark where pid=? order by sequence",
                        new String[]{paperchase.getId() + ""});

                while (paperchaseMarksCursor.moveToNext())
                {
                    Mark mark = new Mark(UUID.fromString(paperchaseMarksCursor.getString(0)), UUID.fromString(paperchaseMarksCursor.getString(1)), paperchaseMarksCursor.getDouble(2), paperchaseMarksCursor.getDouble(3), paperchaseMarksCursor.getString(4), paperchaseMarksCursor.getInt(5));
                    paperchase.addMark(mark);
                }
                paperchases.add(paperchase);
            }
        }
        catch (Exception e)
        {
            Log.w("Exception", e.toString());
        }
        finally
        {
            if (paperchaseCursor != null)
            {
                paperchaseCursor.close();
            }
            if (paperchaseMarksCursor != null)
            {
                paperchaseMarksCursor.close();
            }

            if (database != null)
            {
                database.close();
            }
        }

        return paperchases;
    }


    public Paperchase CreateOrUpdate(Paperchase paperchase)
    {
        SQLiteDatabase database = null;
        Cursor paperchaseCursor = null;

        try
        {
            database = getWritableDatabase();


            paperchaseCursor = database.rawQuery(
                    "SELECT PID FROM paperchase WHERE PID=?",
                    new String[]{paperchase.getId().toString()});


            // Wenn es noch keine paperchase gibt
            if (paperchaseCursor.getCount() != 1)
            {
                ContentValues values = new ContentValues();
                values.put("pid", paperchase.getId().toString());
                values.put("uid", paperchase.getUser().getId().toString());
                values.put("name", paperchase.getName());

                database.insert("paperchase", null, values);

                for (Mark mark : paperchase.getMarks())
                {
                    marks.CreateOrUpdate(mark);
                }
            }
            else
            {
                String sql = "Update " + _SCHNITZELJAGD +
                        " set " + _UID + " = '" + paperchase.getUser().getId().toString() + "',"
                        + _NAME + " = '" + paperchase.getName() + "',"
                        + _TIMESTAMP + " = '" + paperchase.getTimestamp() + "'";
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

        return paperchase;
    }

    public Paperchase update(Paperchase paperchase)
    {
        SQLiteDatabase database = null;

        try
        {
            database = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", paperchase.getName());

            database.update("paperchase", values, "pid = ?", new String[]{paperchase.getId() + ""});

            marks.removeForPaperchase(paperchase);

            for (Mark mark : paperchase.getMarks())
            {
                marks.CreateOrUpdate(mark);
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

        return paperchase;
    }

    public void remove(Paperchase paperchase)
    {
        SQLiteDatabase database = null;

        try
        {
            database = getWritableDatabase();

            database.delete("paperchase", "pid = ?", new String[]{paperchase.getId() + ""});

            marks.removeForPaperchase(paperchase);
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

    public boolean DeleteAllPaperchases() throws Exception
    {
        SQLiteDatabase database = null;

        try
        {
            database = getWritableDatabase();

            String sql = "Delete From " + _SCHNITZELJAGD;
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