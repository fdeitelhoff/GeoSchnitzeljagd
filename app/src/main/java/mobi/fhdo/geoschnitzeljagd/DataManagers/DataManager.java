package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataManager extends SQLiteOpenHelper implements Constants {
    private static final String DATABASE_NAME = "mobi.fhdo.geoschnitzeljagd.sqlite.db";
    private static final int DATABASE_VERSION = 11;

    public DataManager(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.w("onCreate", "Creating a new database");

            db.execSQL("CREATE TABLE \"" + _MARKIERUNG + "\" (" +
                    "\"" + _MID + "\" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "\"" + _PID + "\" INTEGER REFERENCES \"" + _SCHNITZELJAGD + "\" (\"" + _PID + "\"), " +
                    "\"" + _LATITUDE + "\" DOUBLE NOT NULL, " +
                    "\"" + _LONGITUDE + "\" DOUBLE NOT NULL, " +
                    "\"" + _HINWEIS + "\" TEXT, " +
                    "\"" + _REIHENFOLGE + "\" SMALLINT NOT NULL);\n");

            db.execSQL("CREATE TABLE \"" + _SCHNITZELJAGD + "\" (" +
                    "\"" + _PID + "\" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "\"" + _UID + "\" INTEGER REFERENCES \"" + _USER + "\" (\"" + _UID + "\"), " +
                    "\"" + _NAME + "\" TEXT NOT NULL);\n");

            db.execSQL("CREATE TABLE \"" + _SCHNITZELJAGD_ABSOLVIERT + "\" (" +
                    "\"" + _UID + "\" INTEGER REFERENCES \"" + _USER + "\" (\"" + _UID + "\"), " +
                    "\"" + _PID + "\" INTEGER REFERENCES \"" + _SCHNITZELJAGD + "\" (\"" + _PID + "\"), " +
                    "\"" + _STARTZEIT + "\" TIMESTAMP NOT NULL, " +
                    "\"" + _ENDZEIT + "\" TIMESTAMP);\n");

            db.execSQL("CREATE TABLE \"" + _SCHNITZELJAGD_BEWERTUNG + "\" (" +
                    "\"" + _SBID + "\" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "\"" + _PID + "\" INTEGER REFERENCES \"" + _SCHNITZELJAGD + "\" (\"" + _PID + "\"), " +
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

            // Einen Beispielbenutzer.
            db.execSQL("INSERT INTO \"User\" ( \"Username\",\"Password\" ) VALUES ( 'Fabian','test' );");

            // Beispiel Schnitzeljagden.
            db.execSQL("INSERT INTO \"paperchase\" ( \"UID\",\"name\" ) VALUES ( 1, 'Beispiel Schnitzeljagd 1' );");
            db.execSQL("INSERT INTO \"paperchase\" ( \"UID\",\"name\" ) VALUES ( 1, 'Beispiel Schnitzeljagd 2' );");

            // Beispiel Markierungen.
            db.execSQL("INSERT INTO \"mark\" ( \"PID\",\"latitude\",\"longitude\",\"hint\",\"sequence\" ) VALUES ( 1, 1, 1,'Hinweis 1', 1 );");
            db.execSQL("INSERT INTO \"mark\" ( \"PID\",\"latitude\",\"longitude\",\"hint\",\"sequence\" ) VALUES ( 1, 1, 1,'Hinweis 2', 2 );");
            db.execSQL("INSERT INTO \"mark\" ( \"PID\",\"latitude\",\"longitude\",\"hint\",\"sequence\" ) VALUES ( 2, 1, 1,'Hinweis A', 1 );");
            db.execSQL("INSERT INTO \"mark\" ( \"PID\",\"latitude\",\"longitude\",\"hint\",\"sequence\" ) VALUES ( 2, 1, 1,'Hinweis B', 2 );");
        } catch (Exception e) {
            Log.w("onCreate", e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        try {
            Log.w("onUpgrade", "Upgrading database from version " + oldVersion + " to version " + newVersion + ".");

            db.execSQL("DROP TABLE IF EXISTS " + _MARKIERUNG);
            db.execSQL("DROP TABLE IF EXISTS " + _SCHNITZELJAGD);
            db.execSQL("DROP TABLE IF EXISTS " + _SCHNITZELJAGD_ABSOLVIERT);
            db.execSQL("DROP TABLE IF EXISTS " + _SCHNITZELJAGD_BEWERTUNG);
            db.execSQL("DROP TABLE IF EXISTS " + _USER);

            onCreate(db);
        } catch (Exception e) {
            Log.w("onUpgrade", e.toString());
        }
    }
}
