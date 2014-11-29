package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mobi.fhdo.geoschnitzeljagd.Model.Mark;

public class Marks extends DataManager {
    public Marks(Context ctx) {
        super(ctx);
    }

    public List<Mark> ForPaperchaseId(int id) {
        SQLiteDatabase database = null;
        Cursor markCursor = null;
        List<Mark> marks = new ArrayList<Mark>();

        try {
            database = getReadableDatabase();

            markCursor = database.rawQuery(
                    "SELECT MID, gpsdata, hint, sequence FROM Mark WHERE PID=?",
                    new String[]{id + ""});

            while (markCursor.moveToNext()) {
                //Mark mark = new Mark();
            }
        } finally {
            if (markCursor != null) {
                markCursor.close();
            }

            if (database != null) {
                database.close();
            }
        }

        return marks;
    }

    public Mark Create(Mark mark) {
        SQLiteDatabase database = null;

        try {
            database = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("latitude", mark.getLatitude());
            values.put("longitude", mark.getLongitude());
            values.put("hint", mark.getHint());
            values.put("sequence", mark.getSequence());

            int id = (int) database.insert("mark", null, values);

            mark.setId(id);
        } catch (Exception e) {
            Log.w("Exception", e.toString());
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return mark;
    }
}
