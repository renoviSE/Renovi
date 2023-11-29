package com.example.renovi.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RenovationDataSource {
    private static final String LOG_TAG = RenovationDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private RenovationDatabaseHelper dbHelper;

    public RenovationDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new RenovationDatabaseHelper(context);
    }

    public void open(){
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefordert.");
    }
    public void close(){
        dbHelper.close();
    }

    // Methode zum Hinzufügen eines Mieters
    public long addMieter(String firstName, String lastName) {
        ContentValues values = new ContentValues();
        values.put(RenovationDatabaseHelper.COLUMN_FIRST_NAME, firstName);
        values.put(RenovationDatabaseHelper.COLUMN_LAST_NAME, lastName);

        return database.insert(RenovationDatabaseHelper.TABLE_METER, null, values);
    }

    // Methode zum Hinzufügen einer Renovierung mit Bezug zum Mieter
    public long addRenovation(String object, String advantages, String disadvantages,
                              int cost, String paragraph, long mieterId) {
        ContentValues values = new ContentValues();
        values.put(RenovationDatabaseHelper.COLUMN_OBJECT, object);
        values.put(RenovationDatabaseHelper.COLUMN_ADVANTAGES, advantages);
        values.put(RenovationDatabaseHelper.COLUMN_DISADVANTAGES, disadvantages);
        values.put(RenovationDatabaseHelper.COLUMN_COST, cost);
        values.put(RenovationDatabaseHelper.COLUMN_PARAGRAPH, paragraph);
        values.put(RenovationDatabaseHelper.COLUMN_MIETER, mieterId);

        return database.insert(RenovationDatabaseHelper.TABLE_RENOVATION, null, values);
    }
}
