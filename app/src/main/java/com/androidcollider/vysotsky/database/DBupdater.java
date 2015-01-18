package com.androidcollider.vysotsky.database;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidcollider.vysotsky.R;
import com.androidcollider.vysotsky.SongListActivity;
import com.androidcollider.vysotsky.SplashScreenActivity;
import com.androidcollider.vysotsky.objects.Comment;
import com.androidcollider.vysotsky.objects.SongForUpdateRating;
import com.androidcollider.vysotsky.utils.AppController;
import com.androidcollider.vysotsky.utils.NumberConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pseverin on 23.12.14.
 */
public class DBupdater {

    private final static int MY_SOCKET_TIMEOUT_MS = 30000;
    private final static String TAG = "Андроідний Коллайдер";
    private String mode = "";

    private HashMap<String, String> params;
    private DataSource dataSource;
    private Context context;
    //private ArrayList<Long> localUpdateDates, serverUpdateDates;

    private final static String[] tableNames = new String[]{"Song","Comment"};

    //private int needToUpdTables = 0;

    /*private int alreadyUpdRatings = 0;
    private int needToUpdRatings = 0;*/


    public DBupdater(Context context, String mode) {
        this.context = context;

        dataSource = new DataSource(context);
        params = new HashMap<>();
        this.mode = mode;
    }


    public void checkAndUpdateTables() {
        updateServerRatings();
    }

    public void updateServerRatings() {
        if (mode.equals("start")) {
            ((SplashScreenActivity) context).setLoadingStatus("Завантаження рейтингів");
        }
        ArrayList<SongForUpdateRating> songListForUpdateRatings = dataSource.getSongsForUpdateRating();

        if (songListForUpdateRatings.size()!=0){
            updateServerRatingsReq(songListForUpdateRatings);
        } else {
            updateServerComments();
        }
    }


    private void updateServerRatingsReq(final ArrayList<SongForUpdateRating> songListForUpdateRatings){
        String url = AppController.BASE_URL_KEY + "update_song_rating_mas.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE ratings", "     " + response);
                        /*  alreadyUpdRatings++;
                        if (alreadyUpdRatings == needToUpdRatings) {
                            alreadyUpdRatings = 0;
                            needToUpdRatings = 0;
                        }*/
                        updateServerComments();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG + " error", volleyError.toString());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                int arraySize = songListForUpdateRatings.size();
                String[] idArray = new String[arraySize];
                String[] localRatingArray = new String[arraySize];
                for (int i=0; i<arraySize; i++){
                    idArray[i] = String.valueOf(songListForUpdateRatings.get(i).getId());
                    localRatingArray[i] = String.valueOf(songListForUpdateRatings.get(i).getLocalRating());
                }
                HashMap<String, String> params = new HashMap<>();
                for (int i=0; i<arraySize; i++){
                    params.put("mas["+idArray[i]+"]",localRatingArray[i]);
                }
                return params;
            }
        };

        // Adding request to request queue
        /*strReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        AppController.getInstance().addToRequestQueue(strReq, "update_ratings");
    }

    private void updateServerComments() {
        if (mode.equals("start")) {
            ((SplashScreenActivity) context).setLoadingStatus("Загрузка коментарие");
        }
        ArrayList<Comment> commentListForServerUpdate = dataSource.getLocalComments();

        if (commentListForServerUpdate.size()!=0){
            for (Comment comment: commentListForServerUpdate){
                updateServerCommentReq(comment);
            }
        } else {
            getServerUpdateDatesReq();
        }
    }


    private void updateServerCommentReq(final Comment comment){

    }


    private void getServerUpdateDatesReq() {
        String url = AppController.BASE_URL_KEY + "get_last_updates.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("status").equals("True")) {
                        JSONObject result = res.getJSONObject("results");

                        ArrayList<Long> serverUpdateDates = new ArrayList<>();
                        serverUpdateDates.add(NumberConverter.dateToLongConverter(result.getString("Song_up")));
                        serverUpdateDates.add(NumberConverter.dateToLongConverter(result.getString("Comment_up")));

                        ArrayList<Long> localUpdateDates = dataSource.getLocalUpdates();

                        Log.i(TAG + " local updates", localUpdateDates.toString());
                        Log.i(TAG + " server updates", serverUpdateDates.toString());

                        //calculating needToUpdTables Tables
                        int needToUpdTables = 0;
                        for (int i = 0; i < tableNames.length; i++) {
                            if (serverUpdateDates.get(i) > localUpdateDates.get(i)) {
                                needToUpdTables++;
                            }
                        }
                        if (needToUpdTables == 0) {
                            setProgramChange();
                        } else {
                            if (mode.equals("start")) {
                                ((SplashScreenActivity) context).setLoadingStatus("Оновлення бази пісень");
                            }
                            /*for (int i = 0; i < tableNames.length; i++) {
                                if (serverUpdateDates.get(i) > localUpdateDates.get(i)) {
                                    getNewDataFromServerReq(i, localUpdateDates.get(i));
                                }
                            }*/
                            getNewDataFromServerReq(localUpdateDates);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG + " error", volleyError.toString());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("action", "get_last_updates");
                return params;
            }
        };
        // Adding request to request queue
        /*strReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        AppController.getInstance().addToRequestQueue(strReq, "last_updates");

    }

    private void getNewDataFromServerReq(final ArrayList<Long> localUpdateDates) {

        //Log.i(TAG, "оновлюємо локальну таблицю " + tableNames[tableIndex]);
        String url = AppController.BASE_URL_KEY + "get_updates_from_table_for_date.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE number", "     " + AppController.getInstance().getRequestQueue().getSequenceNumber() + " ");
                Log.d("RESPONSE tables", "     " + response);
                try {
                    JSONObject res = new JSONObject(response);
                    //if (res.getString("Status").equals("True")){

                        JSONArray resultSong = res.getJSONObject("Song").getJSONArray("results");
                        JSONArray resultComment = res.getJSONObject("Comment").getJSONArray("results");

                        for (int i = 0; i < resultComment.length(); i++) {
                            dataSource.putJsonObjectToLocalTable("Comment", resultComment.getJSONObject(i));
                        }

                        for (int i = 0; i < resultSong.length(); i++) {
                            dataSource.putJsonObjectToLocalTable("Song", resultSong.getJSONObject(i));
                        }
                        setProgramChange();

                    //}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG + " error", volleyError.toString());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("action", "get_updates_from_table_for_date");
                params.put("song_date", NumberConverter.longToDateConverter(localUpdateDates.get(0)));
                params.put("comment_date", NumberConverter.longToDateConverter(localUpdateDates.get(1)));
                return params;
            }
        };
        // Adding request to request queue
        /*strReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        AppController.getInstance().addToRequestQueue(strReq, "data update");
    }

    private void setProgramChange() {
        if (mode.equals("start")) {
            Intent intent = new Intent(DBupdater.this.context, SongListActivity.class);
            ((SplashScreenActivity) DBupdater.this.context).finish();
            DBupdater.this.context.startActivity(intent);
            ((SplashScreenActivity) DBupdater.this.context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        } else if (mode.equals("finish")) {
            ((SongListActivity) context).finish();
        }
    }

}
