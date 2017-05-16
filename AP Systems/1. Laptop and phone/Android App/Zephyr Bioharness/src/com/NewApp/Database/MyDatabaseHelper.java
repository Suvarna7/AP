package com.NewApp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Cat on 10/18/2016.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 2;
    // Database creation sql statement
    //private static final String DATABASE_CREATE = "create table MyEmployees ( _id integer primary key,name text not null);";

    public MyDatabaseHelper(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
        System.out.println("wHERE IS THE DATABASE?? " + name);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        //database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old, int up) {
        Log.w(MyDatabaseHelper.class.getName(),   "Upgrading database from version " + old + " to "
                        + up + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS MyEmployees");
        onCreate(sqLiteDatabase);
    }
}
