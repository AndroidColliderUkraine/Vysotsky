package com.androidcollider.vysotsky.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.androidcollider.vysotsky.database.local_db.DBhelperLocalDB;
import com.androidcollider.vysotsky.objects.Song;
import com.androidcollider.vysotsky.objects.SongForUpdateRating;
import com.androidcollider.vysotsky.utils.NumberConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pseverin on 22.12.14.
 */
public class DataSource {


    private final static String TAG = "Андроідний Коллайдер";

    private DBupdater dBupdater;
    private DBhelperLocalDB dbHelperLocal;
    private SQLiteDatabase dbLocal;
    private Context context;
    private SharedPreferences sharedPreferences;
    private final static String APP_PREFERENCES = "VysotskyPref";

    private final static String[] tableNames = new String[]{"Song","Comment"};


    public DataSource(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        dbHelperLocal = new DBhelperLocalDB(context);
    }

    //Open database
    public void openLocal() throws SQLException {
        dbLocal = dbHelperLocal.getWritableDatabase();
    }

    //Close database
    public void closeLocal() {
        dbLocal.close();
    }

    public void setLocalUpdateDates(String table, long time) {
        sharedPreferences.edit().putLong(table, time).apply();
    }

    public ArrayList<Long> getLocalUpdates() {
        ArrayList<Long> localUpdates = new ArrayList<>();
        for (int i = 0; i < tableNames.length; i++) {
            localUpdates.add(sharedPreferences.getLong(tableNames[i], 0));
        }
        return localUpdates;
    }


    public void putJsonObjectToLocalTable(String tableName, JSONObject jsonObject) {

        long updateTime = 0;
        if (tableName.equals("Song")) {
            openLocal();
            try {
                int idSongServer = jsonObject.getInt("id");
                updateTime = NumberConverter.dateToLongConverter(jsonObject.getString("Date"));
                int showTo = jsonObject.getInt("ShowTo");;
                if (showTo==1){
                    //Add data to table Song
                    ContentValues cv = new ContentValues();
                    cv.put("Date", updateTime);
                    cv.put("Name", jsonObject.getString("Name"));
                    cv.put("Text", jsonObject.getString("Text"));
                    cv.put("Chord", jsonObject.getString("Chord"));
                    cv.put("Year", jsonObject.getInt("Year"));
                    cv.put("About", jsonObject.getString("About"));
                    cv.put("VideoLink", jsonObject.getString("VideoLink"));
                    cv.put("Rating", jsonObject.getLong("Rating"));
                    cv.put("LocalRating", 0);
                    cv.put("IsFavorite", 0);
                    int updateCount = dbLocal.update(tableName, cv, "id_song = ?", new String[]{String.valueOf(idSongServer)});
                    if (updateCount == 0) {
                        cv.put("id_song", idSongServer);
                        long insertCount = dbLocal.insert("Song", null, cv);
                    }
                    cv.clear();
                } else {
                    int delCount = dbLocal.delete(tableName, "id_song = ?", new String[]{String.valueOf(idSongServer)});
                }
                setLocalUpdateDates(tableName, updateTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (tableName.equals("Comment")) {
            openLocal();
            //Add data to table Comment
            try {
                int idCommentServer = jsonObject.getInt("id");
                updateTime = NumberConverter.dateToLongConverter(jsonObject.getString("Date"));
                int showTo = jsonObject.getInt("ShowTo");;
                if (showTo==1){
                    ContentValues cv = new ContentValues();
                    cv.put("Date", updateTime);
                    cv.put("id_song", jsonObject.getInt("id_Song"));
                    cv.put("Text", jsonObject.getString("Text"));
                    cv.put("DatePosted", jsonObject.getLong("DatePosted"));
                    int updateCount = dbLocal.update(tableName, cv, "id_comment = ?", new String[]{String.valueOf(idCommentServer)});
                    if (updateCount == 0) {
                        cv.put("id_comment", idCommentServer);
                        long insertCount = dbLocal.insert(tableName, null, cv);
                    }
                    cv.clear();
                } else {
                    int delCount = dbLocal.delete(tableName, "id_comment = ?", new String[]{String.valueOf(idCommentServer)});
                }
                setLocalUpdateDates(tableName, updateTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        closeLocal();
    }

    public ArrayList<Song> getSongMainInfo(int idType) {
        openLocal();
        Cursor cursor = dbLocal.query("Song", null, null, null, null, null, null);
        ArrayList<Song> songsList = new ArrayList<>();
        Log.i(TAG, " кількість типу id=" + idType + "     " + cursor.getCount());
        if (cursor.moveToFirst()) {
            int nameColIndex = cursor.getColumnIndex("Name");
            int ratingColIndex = cursor.getColumnIndex("Rating");
            int localRatingColIndex = cursor.getColumnIndex("LocalRating");
            int yearColIndex = cursor.getColumnIndex("Year");
            int isFavoriteColIndex = cursor.getColumnIndex("IsFavorite");
            int idColIndex = cursor.getColumnIndex("id_song");

            long minRating = cursor.getLong(ratingColIndex);
            long maxRating = cursor.getLong(ratingColIndex);

            for (int i = 0; i < cursor.getCount(); i++) {
                String songName = cursor.getString(nameColIndex);
                int songId = cursor.getInt(idColIndex);
                int songYear = cursor.getInt(yearColIndex);
                boolean isFavorite = cursor.getInt(isFavoriteColIndex)!=0;
                long songRating = cursor.getLong(ratingColIndex) + cursor.getLong(localRatingColIndex);
                songsList.add(new Song(songId,songName, songRating, songYear, isFavorite));

                if (songRating > maxRating) {
                    maxRating = songRating;
                }
                if (songRating < minRating) {
                    minRating = songRating;
                }
                cursor.moveToNext();
            }
            Song.current_max_rating = maxRating;
            Song.current_min_rating = minRating;
        }
        cursor.close();
        closeLocal();
        return songsList;
    }





    public void addPointToLocalRating(int idSong) {
        openLocal();
        Cursor cursor = dbLocal.query("Song", null, "id_song = ?", new String[]{String.valueOf(idSong)}, null, null, null);
        long myLocalRating = 0;
        if (cursor.moveToFirst()) {
            int localRatingColIndex = cursor.getColumnIndex("my_local_rating");
            myLocalRating = cursor.getLong(localRatingColIndex);
        }

        ContentValues cv = new ContentValues();
        cv.put("my_local_rating", myLocalRating + 1);
        dbLocal.update("Song", cv, "id_song = ?", new String[]{String.valueOf(idSong)});
        closeLocal();
    }


    public ArrayList<SongForUpdateRating> getSongsForUpdateRating() {
        openLocal();
        Cursor cursor = dbLocal.query("Song", null, "LocalRating > 0", null, null, null, null);
        ArrayList<SongForUpdateRating> songsForUpdateRating = new ArrayList<>();

        if (cursor.moveToFirst()) {
            int localRatingColIndex = cursor.getColumnIndex("LocalRating");
            int idColIndex = cursor.getColumnIndex("id_song");
            for (int i = 0; i < cursor.getCount(); i++) {
                long localRating = cursor.getLong(localRatingColIndex);
                int songId = cursor.getInt(idColIndex);
                songsForUpdateRating.add(new SongForUpdateRating(songId, localRating));
                cursor.moveToNext();
            }
            ContentValues cv = new ContentValues();
            cv.put("LocalRating", 0);
            dbLocal.update("Song", cv, null, null);
        }
        cursor.close();
        closeLocal();

        return songsForUpdateRating;
    }


    public Song getSongAdvancedInfo(Song song) {
        openLocal();
        Cursor cursor = dbLocal.query("Song", null, "id_song = ?", new String[]{String.valueOf(song.getId())}, null, null, null);
        //ArrayList<Song> songsList = new ArrayList<>();
        //Log.i(TAG, " кількість типу id=" + idType + "     " + cursor.getCount());
        if (cursor.moveToFirst()) {
            int dataColIndex = cursor.getColumnIndex("data");
            //int remarksColIndex = cursor.getColumnIndex("remarks");
            int sourceColIndex = cursor.getColumnIndex("source");
            int idSongColumnIndex = cursor.getColumnIndex("id_song");

            int idSong = cursor.getInt(idSongColumnIndex);

            String text = cursor.getString(dataColIndex);
            //String remarks = cursor.getString(remarksColIndex);
            String source = cursor.getString(sourceColIndex);
            song.setText(text);
            //song.setRemarks(remarks);
            //song.setSource(source);
        }
        cursor.close();
        /*cursor = dbLocal.query("CarolComment", null, "id_text = ?", new String[]{String.valueOf(song.getId())}, null, null, null);

        ArrayList<String> commentList = new ArrayList<>();
        //Log.i(TAG, " кількість типу id=" + idType + "     " + cursor.getCount());
        if (cursor.moveToFirst()) {
            int commentDataColIndex = cursor.getColumnIndex("data");

            for (int i = 0; i < cursor.getCount(); i++) {
                commentList.add(cursor.getString(commentDataColIndex));
            }
            song.setComments(commentList);
        }
        cursor.close();
        closeLocal();*/
        return song;
    }

    public boolean isTextContainsChars(int songId, String text) {
        openLocal();
        Cursor cursor = dbLocal.query("Song", null, "id_song = ?", new String[]{String.valueOf(songId)}, null, null, null);
        //ArrayList<Song> songsList = new ArrayList<>();
        //Log.i(TAG, " кількість типу id=" + idType + "     " + cursor.getCount());
        if (cursor.moveToFirst()) {
            int dataColIndex = cursor.getColumnIndex("data");
            String data = cursor.getString(dataColIndex);
            data = data.toLowerCase();
            if (data.contains(text)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void savePref(String sPrefType, boolean wasStarted) {
        if (sPrefType.equals("wasStarted")) {
            sharedPreferences.edit().
                    putBoolean(sPrefType, wasStarted)
                    .apply();
        }

    }

    public boolean isNotFirstStart() {
        return sharedPreferences.getBoolean("wasStarted", false);
    }


    public Cursor getUpdatebleRowsFromLocal(String tableName, long updateFrom) {
        openLocal();
        Cursor cursor = dbLocal.query(tableName, null, "update_time > ?", new String[]{String.valueOf(updateFrom)}, null, null, null);

        return cursor;
    }


}
