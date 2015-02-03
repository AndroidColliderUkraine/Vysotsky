package com.androidcollider.vysotsky.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.androidcollider.vysotsky.database.local_db.DBhelperLocalDB;
import com.androidcollider.vysotsky.objects.Comment;
import com.androidcollider.vysotsky.objects.Song;
import com.androidcollider.vysotsky.objects.SongForUpdateRating;
import com.androidcollider.vysotsky.utils.NumberConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by pseverin on 22.12.14.
 */
public class DataSource {


    private final static String TAG = "Андроідний Коллайдер";

    private DBupdater dBupdater;
    private DBhelperLocalDB dbHelperLocal;
    private SQLiteDatabase dbLocal;
    private Context context;
    private SharedPref sPref;

    private final static String[] tableNames = new String[]{"Song", "Comment"};


    public DataSource(Context context) {
        this.context = context;
        dbHelperLocal = new DBhelperLocalDB(context);
        sPref = new SharedPref(context);
    }

    //Open database
    public void openLocal() throws SQLException {

        if (dbLocal == null || !dbLocal.isOpen()) {
            dbLocal = dbHelperLocal.getWritableDatabase();
        }
    }

    //Close database
    public void closeLocal() {
        if (dbLocal != null && dbLocal.isOpen()) {
            dbLocal.close();
        }
    }


    public void putJsonObjectToLocalTable(String tableName, JSONObject jsonObject) {
        openLocal();
        long updateTime = 0;
        if (tableName.equals("Song")) {

            try {
                int idSongServer = jsonObject.getInt("id");
                String name = jsonObject.getString("Name");
                String text = jsonObject.getString("Text");
                updateTime = NumberConverter.dateToLongConverter(jsonObject.getString("Date"));
                int showTo = jsonObject.getInt("ShowTo");
                long count = 0;
                if (showTo == 1) {
                    //Add data to table Song
                    ContentValues cv = new ContentValues();
                    cv.put("Date", updateTime);
                    cv.put("Name", name);
                    cv.put("Text", text);
                    cv.put("Chord", jsonObject.getString("Chord"));
                    String year = jsonObject.getString("Year");
                    if (year.isEmpty()){
                        cv.put("Year", 0);
                    } else {
                        cv.put("Year", Integer.parseInt(year));
                    }

                    cv.put("About", jsonObject.getString("About"));
                    cv.put("VideoLink", jsonObject.getString("VideoLink"));
                    cv.put("Rating", jsonObject.getLong("Rating"));
                    count = dbLocal.update(tableName, cv, "id_song = ?", new String[]{String.valueOf(idSongServer)});

                    ContentValues cv2 = new ContentValues();
                    cv2.put("NameLower", name.toLowerCase());
                    cv2.put("TextLower", text.toLowerCase());
                    dbLocal.update("LowerSong", cv2, "id_song = ?", new String[]{String.valueOf(idSongServer)});
                    if (count == 0) {
                        cv.put("id_song", idSongServer);
                        cv2.put("id_song", idSongServer);
                        cv.put("LocalRating", 0);
                        cv.put("IsFavorite", 0);
                        count = dbLocal.insert("Song", null, cv);
                        dbLocal.insert("LowerSong", null, cv2);
                    }
                    cv.clear();
                } else {
                    count = dbLocal.delete(tableName, "id_song = ?", new String[]{String.valueOf(idSongServer)});
                    dbLocal.delete("LowerSong", "id_song = ?", new String[]{String.valueOf(idSongServer)});
                }
                if (count > 0) {
                    sPref.setLocalUpdateDates(tableName, updateTime);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (tableName.equals("Comment")) {

            //Add data to table Comment
            try {
                int idCommentServer = jsonObject.getInt("id");
                updateTime = NumberConverter.dateToLongConverter(jsonObject.getString("Date"));
                int showTo = jsonObject.getInt("ShowTo");
                long count = 0;
                if (showTo == 1) {
                    ContentValues cv = new ContentValues();
                    cv.put("Date", updateTime);
                    cv.put("id_song", jsonObject.getInt("id_Song"));
                    cv.put("Text", jsonObject.getString("Text"));
                    cv.put("UserName", jsonObject.getString("UserName"));
                    cv.put("DatePosted", jsonObject.getString("DatePosted"));
                    count = dbLocal.update(tableName, cv, "id_comment = ?", new String[]{String.valueOf(idCommentServer)});
                    if (count == 0) {
                        cv.put("id_comment", idCommentServer);
                        count = dbLocal.insert(tableName, null, cv);
                    }
                    cv.clear();
                } else {
                    count = dbLocal.delete(tableName, "id_comment = ?", new String[]{String.valueOf(idCommentServer)});
                }
                if (count > 0) {
                    sPref.setLocalUpdateDates(tableName, updateTime);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        closeLocal();
    }

    public ArrayList<Song> getSongMainInfo() {
        openLocal();
        Cursor cursor = dbLocal.query("Song", null, null, null, null, null, null);
        ArrayList<Song> songsList = new ArrayList<>();
        Log.i(TAG, " кількість типу "+cursor.getCount());
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
                boolean isFavorite = cursor.getInt(isFavoriteColIndex) != 0;
                long songRating = cursor.getLong(ratingColIndex) + cursor.getLong(localRatingColIndex);
                songsList.add(new Song(songId, songName, songRating, songYear, isFavorite));

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
            int localRatingColIndex = cursor.getColumnIndex("LocalRating");
            myLocalRating = cursor.getLong(localRatingColIndex);
        }

        ContentValues cv = new ContentValues();
        cv.put("LocalRating", myLocalRating + 1);
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
        if (cursor.moveToFirst()) {
            int textColIndex = cursor.getColumnIndex("Text");
            int aboutColIndex = cursor.getColumnIndex("About");
            int chordColIndex = cursor.getColumnIndex("Chord");
            int videoLinkColIndex = cursor.getColumnIndex("VideoLink");
            int favoriteColIndex = cursor.getColumnIndex("IsFavorite");

            String text = cursor.getString(textColIndex);
            String chord = cursor.getString(chordColIndex);
            String about = cursor.getString(aboutColIndex);
            String videoLink = cursor.getString(videoLinkColIndex);
            boolean isFavorite = false;
            if (cursor.getInt(favoriteColIndex)==1){
                isFavorite = true;
            }
            song.setText(text);
            song.setChord(chord);
            song.setAbout(about);
            song.setVideoLink(videoLink);
            song.setFavorite(isFavorite);
        }
        cursor.close();

        ArrayList<Comment> commentList = new ArrayList<>();
        cursor = dbLocal.query("Comment", null, "id_song = ?", new String[]{String.valueOf(song.getId())}, null, null, null);
        if (cursor.moveToFirst()) {
            int textColIndex = cursor.getColumnIndex("Text");
            int userNameColIndex = cursor.getColumnIndex("UserName");
            int datePostedColIndex = cursor.getColumnIndex("DatePosted");

            for (int i = 0; i < cursor.getCount(); i++) {
                commentList.add(new Comment(0, song.getId(), cursor.getString(userNameColIndex),
                        cursor.getString(textColIndex), cursor.getString(datePostedColIndex)));
                cursor.moveToNext();
            }

        }
        cursor.close();

        cursor = dbLocal.query("LocalComment", null, "id_song = ?", new String[]{String.valueOf(song.getId())}, null, null, null);
        if (cursor.moveToFirst()) {
            int textColIndex = cursor.getColumnIndex("Text");
            int userNameColIndex = cursor.getColumnIndex("UserName");
            int datePostedColIndex = cursor.getColumnIndex("DatePosted");

            for (int i = 0; i < cursor.getCount(); i++) {
                commentList.add(new Comment(0, song.getId(), cursor.getString(userNameColIndex), cursor.getString(textColIndex),
                        cursor.getString(datePostedColIndex)));
                cursor.moveToNext();
            }

        }
        cursor.close();

        Collections.sort(commentList, new Comparator<Comment>() {
            @Override
            public int compare(Comment comment1, Comment comment2) {

                return NumberConverter.dateToLongConverter(comment1.getDatePosted()).compareTo
                        (NumberConverter.dateToLongConverter(comment2.getDatePosted()));
            }
        });

        song.setComments(commentList);
        closeLocal();
        return song;
    }

    /*public boolean isTextContainsChars(int songId, String text) {
        Cursor cursor = dbLocal.query("Song", null, "id_song = ?", new String[]{String.valueOf(songId)}, null, null, null);
        //ArrayList<Song> songsList = new ArrayList<>();
        //Log.i(TAG, " кількість типу id=" + idType + "     " + cursor.getCount());
        if (cursor.moveToFirst()) {
            int dataColIndex = cursor.getColumnIndex("Text");
            String data = cursor.getString(dataColIndex);
            data = data.toLowerCase();
            cursor.close();
            if (data.contains(text)) {
                return true;
            } else {
                return false;
            }
        } else {
            cursor.close();
            return false;
        }
    }*/


    public void addCommentToLocal(Comment comment) {
        openLocal();
        //Add data to table Comment

        ContentValues cv = new ContentValues();
        cv.put("id_song", comment.getIdSong());
        cv.put("Text", comment.getText());
        cv.put("UserName", comment.getUserName());
        cv.put("DatePosted", comment.getDatePosted());
        dbLocal.insert("LocalComment", null, cv);
        cv.clear();

        closeLocal();
    }

    public ArrayList<Comment> getLocalComments(){
        ArrayList<Comment> comments = new ArrayList<>();
        openLocal();
        Cursor cursor = dbLocal.query("LocalComment", null, null, null, null, null, null);
        //ArrayList<Song> songsList = new ArrayList<>();
        //Log.i(TAG, " кількість типу id=" + idType + "     " + cursor.getCount());
        if (cursor.moveToFirst()) {
            int textColIndex = cursor.getColumnIndex("Text");
            int userNameColIndex = cursor.getColumnIndex("UserName");
            int datePostedColIndex = cursor.getColumnIndex("DatePosted");
            int songIdColIndex = cursor.getColumnIndex("id_song");
            int idCommentColIndex = cursor.getColumnIndex("id");

            for (int i = 0; i < cursor.getCount(); i++) {
                comments.add(new Comment(cursor.getInt(idCommentColIndex), cursor.getInt(songIdColIndex),
                        cursor.getString(userNameColIndex), cursor.getString(textColIndex), cursor.getString(datePostedColIndex)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        openLocal();
        return comments;
    }

    public void deleteCommentFromLocalComments(int idComment){
        openLocal();
        dbLocal.delete("LocalComment", "id = ?", new String[]{String.valueOf(idComment)});
        closeLocal();
    }

    public void makeSongFavorite(int id, boolean isFavorite){
        openLocal();
        //Add data to table Comment

        ContentValues cv = new ContentValues();
        if (isFavorite){
            cv.put("IsFavorite", 1);
        } else {
            cv.put("IsFavorite", 0);
        }
        dbLocal.update("Song",cv,"id_song=?",new String[]{String.valueOf(id)});
        cv.clear();

        closeLocal();
    }

    public ArrayList<Integer> getSerachRawQuaryArrayList(String str){
        ArrayList<Integer> ids = new ArrayList<>();
        openLocal();
        Cursor cursor = dbLocal.rawQuery("SELECT id_song FROM LowerSong WHERE (TextLower LIKE '%"+str+"%' OR NameLower LIKE '%"+str+"%')",null);
        if (cursor.moveToFirst()) {
            int songIdColIndex = cursor.getColumnIndex("id_song");
            do{
                ids.add(cursor.getInt(songIdColIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeLocal();
        return ids;
    }

    public void copyDataToLowerCaseTable(){
        openLocal();
        Cursor cursor1 = dbLocal.query("Song", null, null, null, null, null, null);
        Cursor cursor2 = dbLocal.query("LowerSong", null, null, null, null, null, null);

        if (cursor1.getCount()>cursor2.getCount()){
            dbLocal.delete("LowerSong", null, null);
            if (cursor1.moveToFirst()){
                int idIndex = cursor1.getColumnIndex("id_song");
                int nameIndex = cursor1.getColumnIndex("Name");
                int textIndex = cursor1.getColumnIndex("Text");
                do{
                    ContentValues cv = new ContentValues();
                    cv.put("id_song",cursor1.getInt(idIndex));
                    cv.put("NameLower",cursor1.getString(nameIndex).toLowerCase());
                    cv.put("TextLower",cursor1.getString(textIndex).toLowerCase());
                    dbLocal.insert("LowerSong", null, cv);
                }while (cursor1.moveToNext());
            }
        }
    }
}
