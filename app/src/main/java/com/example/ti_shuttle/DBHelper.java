package com.example.ti_shuttle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class pass_data {
    String AID;
    String date;
    String Location_in;
    String Time_in;
    String Location_out;
    String Time_out;
}
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TI_shuttle.db";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table mapping (AID text primary key, CID text)");
        db.execSQL("create table pass_data (_id integer primary key autoincrement, AID text, date text, Location_in text, Time_in text, Location_out text, Time_out text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS mapping");
        db.execSQL("DROP TABLE IF EXISTS pass_data");
        onCreate(db);
    }

    String checkAID(String CID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("mapping", new String[]{"AID"}, "CID=?", new String[]{CID}, null, null, null);
        String AID;
        if (cursor.moveToNext()) {
            AID = cursor.getString(0);
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
            Cursor cursor = db.query("pass_data", new String[]{"_id", "Location_out"}, "AID=? and date=?", new String[]{AID, date}, null, null, "Time_in");
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
                    String ID = String.valueOf(cursor.getInt(0));
                    String location_out = cursor.getString(1);
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
                        db.update("pass_data", values, "_id=?", new String[]{ID});
                    }
                    break;
                case 2:
                    cursor.moveToLast();
                    ID = String.valueOf(cursor.getInt(0));
                    values.put("AID", AID);
                    values.put("date", date);
                    values.put("Location_out", location);
                    values.put("Time_out", time);
                    db.update("pass_data", values, "_id=?", new String[]{ID});
                    break;
                default:
                    cursor.close();
                    System.out.println(no_rows);
                    return false;
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    ArrayList<pass_data> getAllEntries() {
        ArrayList<pass_data> array_list = new ArrayList<>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query("pass_data", new String[]{"_id", "AID", "date", "Location_in", "Time_in", "Location_out", "Time_out"}, null, null, null, null, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            pass_data data = new pass_data();
            data.AID = res.getString(0);
            data.date = res.getString(1);
            data.Location_in = res.getString(2);
            data.Time_in = res.getString(3);
            data.Location_out = res.getString(4);
            data.Time_out = res.getString(5);
            array_list.add(data);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    Cursor getCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("pass_data", new String[]{"_id", "AID", "date", "Location_in", "Time_in", "Location_out", "Time_out"}, null, null, null, null, null);
    }
}
