package com.androidcollider.vysotsky.database.local_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pseverin on 22.12.14.
 */
public class DBhelperLocalDB  extends SQLiteOpenHelper {

    Context context;
    private static final String DATABASE_NAME = "Vysotsky.db";
    private static final int DATABASE_VERSION = 2;

    public DBhelperLocalDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLQueriesLocalDB.create_song_table);
        db.execSQL(SQLQueriesLocalDB.create_comment_table);
        db.execSQL(SQLQueriesLocalDB.create_local_comment_table);
        db.execSQL(SQLQueriesLocalDB.create_lower_song_table);
    }
    // Method for update database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion==2){
            db.execSQL(SQLQueriesLocalDB.create_lower_song_table);
        }
    }
}
