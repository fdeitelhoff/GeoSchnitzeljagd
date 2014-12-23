package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import mobi.fhdo.geoschnitzeljagd.Model.PaperchaseReview;

public class PaperchaseReviews extends DataManager
{
    public PaperchaseReviews(Context ctx)
    {
        super(ctx);
    }

    public void Creaete(PaperchaseReview paperchaseReview)
    {
        SQLiteDatabase database = null;
        Cursor userCursor = null;

        try
        {
            database = getReadableDatabase();

            String sql = "Insert into " + _SCHNITZELJAGD_BEWERTUNG + " " +
                    "(" + _PID + "," + _UID + "," + _SCHWIERIGKEIT + "," + _SPANNUNG + "," + _UMGEBUNG + "," + _LAENGE + "," + _KOMMENTAR + ") " +
                    "values('" + paperchaseReview.getPaperchase().getId() + "','" + paperchaseReview.getUser().getId() + "','" + paperchaseReview.getDifficulty() + "','" + paperchaseReview.getExciting() + "','" + paperchaseReview.getEnvironment() + "','" + paperchaseReview.getLength() + "','" + paperchaseReview.getComment() + "')";
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
    }
}
