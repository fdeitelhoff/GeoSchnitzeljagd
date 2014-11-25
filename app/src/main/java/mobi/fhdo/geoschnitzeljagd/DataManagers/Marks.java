package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
                Mark mark = new Mark();
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
}
