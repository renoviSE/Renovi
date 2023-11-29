package com.example.renovi.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RenovationDatabaseHelper extends SQLiteOpenHelper {

    private static final  String LOG_TAG = RenovationDatabaseHelper.class.getSimpleName();
    public static final String DB_NAME = "renovi.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_METER = "mieter";

    public static final String COLUMN_MIETER_ID = "mieter_id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";

    public static final String SQL_CREATE_METER =
            "CREATE TABLE " + TABLE_METER +
                    "(" + COLUMN_MIETER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                    COLUMN_LAST_NAME + " TEXT NOT NULL);";


    public static final String TABLE_RENOVATION = "renovierungen";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_OBJECT = "object";
    public static final String COLUMN_ADVANTAGES = "advantages";
    public static final String COLUMN_DISADVANTAGES = "disadvantages";
    public static final String COLUMN_COST = "cost";
    public static final String COLUMN_PARAGRAPH = "paragraph";
    public static final String COLUMN_MIETER = "mieter";

    public static final String SQL_CREATE_RENOVATION =
            "CREATE TABLE " + TABLE_RENOVATION +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_OBJECT + " TEXT NOT NULL, " +
                    COLUMN_ADVANTAGES + " TEXT NOT NULL, " +
                    COLUMN_DISADVANTAGES + " TEXT NOT NULL, " +
                    COLUMN_COST + " INTEGER NOT NULL, " +
                    COLUMN_PARAGRAPH + " TEXT NOT NULL, " +
                    COLUMN_MIETER + " INTEGER REFERENCES " + TABLE_METER + "(" + COLUMN_MIETER_ID + "));";


    public RenovationDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: "+getDatabaseName()+" erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_RENOVATION + " angelegt.");
            sqLiteDatabase.execSQL(SQL_CREATE_METER);
            sqLiteDatabase.execSQL(SQL_CREATE_RENOVATION);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
