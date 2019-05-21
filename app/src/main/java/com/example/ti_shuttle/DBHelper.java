package com.example.ti_shuttle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TI_shuttle.db";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table mapping (AID text primary key, CID text)");
        db.execSQL("create table pass_data (ID integer primary key autoincrement, AID text, date text, Location_in text, Time_in text, Location_out text, Time_out text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS mapping");
        db.execSQL("DROP TABLE IF EXISTS pass_data");
        onCreate(db);
    }

    String checkAID(String CID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("mapping", new String[]{"AID"}, "CID=" + CID, null, null, null, null);
        String AID;
        if (cursor.moveToNext()) {
            AID = cursor.getString(1);
        } else {
            AID = null;
        }
        cursor.close();
        return AID;
    }

    String insertAID(String CID, String AID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("AID", AID);
        values.put("CID", CID);
        db.insert("mapping", null, values);
        return AID;
    }

    boolean insertData(String AID, String location, String date, String time) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query("pass_data", new String[]{"ID", "Location_out"}, "AID=? and date=?", new String[]{AID, date}, null, null, null);
            ContentValues values = new ContentValues();
            int no_rows = cursor.getCount();
            switch (no_rows) {
                case 0:
                    values.put("AID", AID);
                    values.put("date", date);
                    values.put("Location_in", location);
                    values.put("Time_in", time);
                    values.put("Location_out", (String) null);
                    values.put("Time_out", (String) null);
                    db.insert("pass_data", null, values);
                    break;
                case 1:
                    cursor.moveToFirst();
                    String ID = String.valueOf(cursor.getInt(1));
                    String location_out = cursor.getString(2);
                    if (location_out != null && !location_out.isEmpty()) {
                        // 3rd time in a day, insert second row
                        values.put("AID", AID);
                        values.put("date", date);
                        values.put("Location_in", location);
                        values.put("Time_in", time);
                        values.put("Location_out", (String) null);
                        values.put("Time_out", (String) null);
                        db.insert("pass_data", null, values);
                    } else {
                        // 2nd time in a day, update location_out in first row
                        values.put("AID", AID);
                        values.put("date", date);
                        values.put("Location_out", location);
                        values.put("Time_out", time);
                        db.update("pass_data", values, "ID=?", new String[]{ID});
                    }
                    break;
                case 2:
                    cursor.moveToLast();
                    ID = String.valueOf(cursor.getInt(1));
                    values.put("AID", AID);
                    values.put("date", date);
                    values.put("Location_out", location);
                    values.put("Time_out", time);
                    db.update("pass_data", values, "ID=?", new String[]{ID});
                    break;
                default:
                    cursor.close();
                    return false;
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
