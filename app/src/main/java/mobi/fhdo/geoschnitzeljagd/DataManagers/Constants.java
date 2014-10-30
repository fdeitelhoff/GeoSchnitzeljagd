package mobi.fhdo.geoschnitzeljagd.DataManagers;

import android.provider.BaseColumns;

public interface Constants
{
    // Tabelle
    public static final String _USER = "user";
    // Spalten
    public static final String _UID = "UID";
    public static final String _USERNAME = "username";
    public static final String _PASSWORD = "password";


    // Tabelle
    public static final String _MARKIERUNG = "markierung";
    // Spalten
    public static final String _MID = "MID";
    public static final String _HINWEIS = "Hinweis";
    public static final String _REIHENFOLGE = "reihenfolge";


    // Tabelle
    public static final String _SCHNITZELJAGD = "schnitzeljagd";
    // Spalten
    public static final String _SID = "SID";
    public static final String _NAME = "Name";


    // Tabelle
    public static final String _SCHNITZELJAGD_ABSOLVIERT = "schnitzeljagd_absolviert";
    // Spalten
    public static final String _STARTZEIT = "startzeit";
    public static final String _ENDZEIT = "endzeit";


    // Tabelle
    public static final String _SCHNITZELJAGD_BEWERTUNG = "schnitzeljagd_bewertung";
    // Spalten
    public static final String _SBID = "SBID";
    public static final String _SCHWIERIGKEIT = "Schwierigkeit";
    public static final String _SPANNUNG = "Spannung";
    public static final String _UMGEBUNG = "Umgebung";
    public static final String _LAENGE = "Laenge";
    public static final String _KOMMENTAR = "Kommentar";
}