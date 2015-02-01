package com.androidcollider.vysotsky.database.local_db;

/**
 * Created by pseverin on 22.12.14.
 */
public class SQLQueriesLocalDB {
    //make a string SQL request for Song table
    public static final String create_song_table = "CREATE TABLE Song (" +
            "id_song          INTEGER PRIMARY KEY NOT NULL," +
            "Name             TEXT NOT NULL," +
            "Text             TEXT NOT NULL," +
            "Chord            TEXT NOT NULL," +
            "Year             INTEGER NOT NULL," +
            "About            TEXT NOT NULL," +
            "VideoLink        TEXT NOT NULL," +
            "Date             INTEGER NOT NULL," +
            "Rating           INTEGER NOT NULL," +
            "LocalRating      INTEGER NOT NULL," +
            "IsFavorite       INTEGER NOT NULL" +
            ");";
    //make a string SQL request for Song table
    public static final String create_lower_song_table = "CREATE TABLE LowerSong (" +
            "id_song          INTEGER PRIMARY KEY NOT NULL," +
            "NameLower        TEXT NOT NULL," +
            "TextLower        TEXT NOT NULL" +
            ");";
    //make a string SQL request for Comment table
    public static final String create_comment_table = "CREATE TABLE Comment (" +
            "id_comment       INTEGER PRIMARY KEY NOT NULL," +
            "id_song          INTEGER NOT NULL," +
            "Text             TEXT NOT NULL," +
            "UserName         TEXT NOT NULL," +
            "DatePosted       TEXT NOT NULL," +
            "Date             INTEGER NOT NULL" +
            ");";
    //make a string SQL request for LocalComment table
    public static final String create_local_comment_table = "CREATE TABLE LocalComment (" +
            "id               INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_song          INTEGER NOT NULL," +
            "Text             TEXT NOT NULL," +
            "UserName         TEXT NOT NULL," +
            "DatePosted       INTEGER NOT NULL" +
            ");";

}
