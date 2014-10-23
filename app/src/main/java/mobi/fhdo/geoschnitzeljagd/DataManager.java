package mobi.fhdo.geoschnitzeljagd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Console;

public class DataManager extends SQLiteOpenHelper implements Constants
{
    private static final String DATABASE_NAME = "sqlite.db";
    private static final int DATABASE_VERSION = 1;

    public DataManager(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);

        //Log.v(_ENDZEIT,"rsgwergweg");
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE \"" + _MARKIERUNG + "\" (" +
                "\"" + _MID + "\" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "\"" + _SID + "\" INTEGER REFERENCES \"" + _SCHNITZELJAGD + "\" (\"" + _SID + "\"), " +
                "\"" + _HINWEIS + "\" TEXT, " +
                "\"" + _REIHENFOLGE + "\" SMALLINT NOT NULL);\n");

        db.execSQL("CREATE TABLE \"" + _SCHNITZELJAGD + "\" (" +
                "\"" + _SID + "\" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "\"" + _UID + "\" INTEGER REFERENCES \"" + _USER + "\" (\"" + _UID + "\"), " +
                "\"" + _NAME + "\" TEXT NOT NULL);\n");

        db.execSQL("CREATE TABLE \"" + _SCHNITZELJAGD_ABSOLVIERT + "\" (" +
                "\"" + _UID + "\" INTEGER REFERENCES \"" + _USER + "\" (\"" + _UID + "\"), " +
                "\"" + _SID + "\" INTEGER REFERENCES \"" + _SCHNITZELJAGD + "\" (\"" + _SID + "\"), " +
                "\"" + _STARTZEIT + "\" TIMESTAMP NOT NULL, " +
                "\"" + _ENDZEIT + "\" TIMESTAMP);\n");

        db.execSQL("CREATE TABLE \"" + _SCHNITZELJAGD_BEWERTUNG + "\" (" +
                "\"" + _SBID + "\" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "\"" + _SID + "\" INTEGER REFERENCES \"" + _SCHNITZELJAGD + "\" (\"" + _SID + "\"), " +
                "\"" + _UID + "\" INTEGER REFERENCES \"" + _USER + "\" (\"" + _UID + "\"), " +
                "\"" + _SCHWIERIGKEIT + "\" SMALLINT NOT NULL, " +
                "\"" + _SPANNUNG + "\" SMALLINT NOT NULL, " +
                "\"" + _UMGEBUNG + "\" SMALLINT NOT NULL, " +
                "\"" + _LAENGE + "\" SMALLINT NOT NULL, " +
                "\"" + _KOMMENTAR + "\" TEXT NOT NULL);\n");

        db.execSQL("CREATE TABLE \"" + _USER + "\" (\"" +
                _UID + "\" INTEGER PRIMARY KEY AUTOINCREMENT, \"" +
                _USERNAME + "\" TEXT NOT NULL UNIQUE, \"" +
                _PASSWORD + "\" TEXT NOT NULL);\n");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion)
    {
        onCreate(db);
    }
}
