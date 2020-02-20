package com.example.itukontenjan;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int database_version = 1;
    private static final String database_name = "AlarmsAndNotifications";
    private static final String TABLE_NAME = "AlNot";
    private static final String id = "id";
    private static final String crn = "crn";
    private static final String lecture = "lecture";
    private static final String [] columns = {id, lecture, crn};
    private static final String CREATE = "create table if not exists "+TABLE_NAME+"(" + id + " integer primary key autoincrement,"+crn+" text not null,"+lecture+" text not null)";

    public SQLiteHelper(@Nullable Context context) {
        super(context, database_name, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        this.onCreate(db);
    }

    public void insert_row(lectureCRN lecC){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(crn, lecC.getCrn());
        values.put(lecture, lecC.getLecture());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<lectureCRN> get_row_list(){
        List<lectureCRN> lectureCRNList = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        lectureCRN lecC = null;
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(2) == null) break;
                lecC = new lectureCRN();
                lecC.setId(Integer.parseInt(cursor.getString(0)));
                lecC.setCrn(cursor.getString(1));
                lecC.setLecture(cursor.getString(2));
                lectureCRNList.add(lecC);
            }while (cursor.moveToNext());
        }
        return lectureCRNList;
    }

    public lectureCRN get_a_row(String lecCrn){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns, " crn = ?", new String[]{String.valueOf(lecCrn)}, null, null, null);
        if(cursor!= null){
            cursor.moveToFirst();
            lectureCRN lec = new lectureCRN();
            lec.setId(Integer.parseInt(cursor.getString(0)));
            lec.setCrn(cursor.getString(1));
            lec.setLecture(cursor.getString(2));
            return lec;
        }
        return null;
    }

    public void delete(lectureCRN lecC){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "crn = ?", new String[]{String.valueOf(lecC.getCrn())});
        db.close();
    }
}
