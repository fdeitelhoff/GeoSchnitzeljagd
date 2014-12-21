package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.util.UUID;

public class DataManager extends SQLiteOpenHelper implements Constants
{
    private static final String DATABASE_NAME = "mobi.fhdo.geoschnitzeljagd.sqlite.db";
    private static final int DATABASE_VERSION = 22;

    public DataManager(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            Log.w("onCreate", "Creating a new database");


            //uniqueidentifier

            db.execSQL("CREATE TABLE \"" + _MARKIERUNG + "\" (" +
                    "\"" + _MID + "\" TEXT PRIMARY KEY, " +
                    "\"" + _PID + "\" TEXT REFERENCES \"" + _SCHNITZELJAGD + "\" (\"" + _PID + "\"), " +
                    "\"" + _LATITUDE + "\" DOUBLE NOT NULL, " +
                    "\"" + _LONGITUDE + "\" DOUBLE NOT NULL, " +
                    "\"" + _HINWEIS + "\" TEXT, " +
                    "\"" + _REIHENFOLGE + "\" SMALLINT NOT NULL);\n");

            db.execSQL("CREATE TABLE \"" + _SCHNITZELJAGD + "\" (" +
                    "\"" + _PID + "\" TEXT PRIMARY KEY, " +
                    "\"" + _UID + "\" TEXT REFERENCES \"" + _USER + "\" (\"" + _UID + "\"), " +
                    "\"" + _NAME + "\" TEXT NOT NULL, \"" +
                    _TIMESTAMP + "\" DATETIME DEFAULT CURRENT_TIMESTAMP\n);\n");

            db.execSQL("CREATE TABLE \"" + _SCHNITZELJAGD_ABSOLVIERT + "\" (" +
                    "\"" + _UID + "\" TEXT REFERENCES \"" + _USER + "\" (\"" + _UID + "\"), " +
                    "\"" + _PID + "\" TEXT REFERENCES \"" + _SCHNITZELJAGD + "\" (\"" + _PID + "\"), " +
                    "\"" + _STARTZEIT + "\" TIMESTAMP NOT NULL, " +
                    "\"" + _ENDZEIT + "\" TIMESTAMP);\n");

            db.execSQL("CREATE TABLE \"" + _SCHNITZELJAGD_BEWERTUNG + "\" (" +
                    "\"" + _SBID + "\" TEXT PRIMARY KEY, " +
                    "\"" + _PID + "\" TEXT REFERENCES \"" + _SCHNITZELJAGD + "\" (\"" + _PID + "\"), " +
                    "\"" + _UID + "\" TEXT REFERENCES \"" + _USER + "\" (\"" + _UID + "\"), " +
                    "\"" + _SCHWIERIGKEIT + "\" SMALLINT NOT NULL, " +
                    "\"" + _SPANNUNG + "\" SMALLINT NOT NULL, " +
                    "\"" + _UMGEBUNG + "\" SMALLINT NOT NULL, " +
                    "\"" + _LAENGE + "\" SMALLINT NOT NULL, " +
                    "\"" + _KOMMENTAR + "\" TEXT NOT NULL, \"" +
                    _TIMESTAMP + "\" DATETIME DEFAULT CURRENT_TIMESTAMP\n);\n");

            db.execSQL("CREATE TABLE \"" + _USER + "\" (\"" +
                    _UID + "\" TEXT PRIMARY KEY, \"" +
                    _USERNAME + "\" TEXT NOT NULL UNIQUE, \"" +
                    _PASSWORD + "\" TEXT NOT NULL, \"" +
                    _TIMESTAMP + "\" DATETIME DEFAULT CURRENT_TIMESTAMP\n);\n");
/*
            java.util.Date date = new java.util.Date();

            UUID fabianID= UUID.randomUUID();

            // Einen Beispielbenutzer.
            db.execSQL("INSERT INTO \"User\" ( \"UID\",\"Username\",\"Password\",\"Timestamp\" ) VALUES ('" + fabianID.toString() + "', 'Fabian','test', '" + new Timestamp(date.getTime()) + "');");

            // Beispiel Schnitzeljagden.
            db.execSQL("INSERT INTO \"paperchase\" ( \"PID\",\"UID\",\"name\" ) VALUES ('" + UUID.randomUUID().toString() + "', '" + UUID.randomUUID().toString() + "', 'Beispiel Schnitzeljagd 1' );");
            db.execSQL("INSERT INTO \"paperchase\" ( \"PID\",\"UID\",\"name\" ) VALUES ('" + UUID.randomUUID().toString() + "', '" + UUID.randomUUID().toString() + "', 'Beispiel Schnitzeljagd 2' );");

            // Beispiel Markierungen.
            db.execSQL("INSERT INTO \"mark\" ( \"MID\",\"PID\",\"latitude\",\"longitude\",\"hint\",\"sequence\" ) VALUES ('" + UUID.randomUUID().toString() + "', '" + fabianID.toString() + "', 1, 1,'Hinweis 1', 1 );");
            db.execSQL("INSERT INTO \"mark\" ( \"MID\",\"PID\",\"latitude\",\"longitude\",\"hint\",\"sequence\" ) VALUES ('" + UUID.randomUUID().toString() + "', '" + fabianID.toString() + "', 1, 1,'Hinweis 2', 2 );");
            db.execSQL("INSERT INTO \"mark\" ( \"MID\",\"PID\",\"latitude\",\"longitude\",\"hint\",\"sequence\" ) VALUES ('" + UUID.randomUUID().toString() + "', '" + UUID.randomUUID().toString() + "', 1, 1,'Hinweis A', 1 );");
            db.execSQL("INSERT INTO \"mark\" ( \"MID\",\"PID\",\"latitude\",\"longitude\",\"hint\",\"sequence\" ) VALUES ('" + UUID.randomUUID().toString() + "', '" + UUID.randomUUID().toString() + "', 1, 1,'Hinweis B', 2 );");
        */
        }
        catch (Exception e)
        {
            Log.w("onCreate", e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion)
    {
        try
        {
            Log.w("onUpgrade", "Upgrading database from version " + oldVersion + " to version " + newVersion + ".");

            db.execSQL("DROP TABLE IF EXISTS " + _MARKIERUNG);
            db.execSQL("DROP TABLE IF EXISTS " + _SCHNITZELJAGD);
            db.execSQL("DROP TABLE IF EXISTS " + _SCHNITZELJAGD_ABSOLVIERT);
            db.execSQL("DROP TABLE IF EXISTS " + _SCHNITZELJAGD_BEWERTUNG);
            db.execSQL("DROP TABLE IF EXISTS " + _USER);

            onCreate(db);
        }
        catch (Exception e)
        {
            Log.w("onUpgrade", e.toString());
        }
    }
}
