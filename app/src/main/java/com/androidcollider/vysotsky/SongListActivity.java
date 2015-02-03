package com.androidcollider.vysotsky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.androidcollider.vysotsky.adapters.SongAdapter;
import com.androidcollider.vysotsky.adapters.SortTypeAdapter;
import com.androidcollider.vysotsky.database.DBupdater;
import com.androidcollider.vysotsky.database.DataSource;
import com.androidcollider.vysotsky.objects.Song;
import com.androidcollider.vysotsky.utils.AppController;
import com.androidcollider.vysotsky.utils.InternetHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SongListActivity extends ActionBarActivity {

    private final static String TAG = "Андроідний Коллайдер";
    private final static int UPDATE_DATA_TIME = 1000*3600;
    private ListView lv_songs_list, lv_sort_types;
    private EditText et_search_song;
    private ArrayList<Song> songList;
    private SongAdapter songAdapter;
    private DataSource dataSource;
    private ArrayList<String> sortTypeArrayList;
    private ImageView iv_search_sort;
    private String typeName;
    private boolean isShowingFavorite =false;
    private int sortType = 0;
    private Timer t;
    private DBupdater dBupdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        sendDataToAnalytics();
        registerReceiver(broadcastReceiver,new IntentFilter("update_songs_ui"));

        List<String> list = Arrays.asList(getResources().getStringArray(R.array.sort_array));
        sortTypeArrayList = new ArrayList<>();
        sortTypeArrayList.addAll(list);

        dataSource = new DataSource(this);
        Intent intent = getIntent();

        typeName = intent.getStringExtra("SongTypeName");
        initFields();
        initListeners();

        songList = dataSource.getSongMainInfo();

        songAdapter = new SongAdapter(this, songList);
        if (sortType==0){
            sortByName();
        } else {
            sortByRating();
        }

        lv_songs_list.setAdapter(songAdapter);
        SortTypeAdapter sortTypeAdapter = new SortTypeAdapter(this, sortTypeArrayList);
        lv_sort_types.setAdapter(sortTypeAdapter);

        dBupdater = new DBupdater(this, "timer");
        startTimerUpdating();
    }

    private void initFields(){
        lv_songs_list = (ListView)findViewById(R.id.lv_songs_list);
        et_search_song = (EditText)findViewById(R.id.et_search_song);
        et_search_song.setPadding(10,0,10,0);
        lv_sort_types = (ListView)findViewById(R.id.lv_sort_types);
        iv_search_sort = (ImageView)findViewById(R.id.iv_search_sort);
    }
    private void initListeners() {
        lv_songs_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idSong = songAdapter.getItem(position).getId();

                addOnePointToListRating(idSong);
                addOnePointToLocalDBRating(idSong);

                //songAdapter.halfUpdateData(songList);

                Intent intent = new Intent(SongListActivity.this,TextActivity.class);
                intent.putExtra("Song", songAdapter.getItem(position));
                startActivity(intent);
            }
        });

        lv_sort_types.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,songList.toString());
                switch (position){
                    case 0:
                        sortByName();
                        sortType=0;
                        showHideSortTypes(false);
                        Log.i(TAG,songList.toString());
                        break;

                    case 1:
                        sortByRating();
                        sortType=0;
                        showHideSortTypes(false);
                        Log.i(TAG,songList.toString());
                        break;
                }
            }
        });
//Привіт, Северине!
        et_search_song.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                songAdapter.searchNew(et_search_song.getText().toString(),isShowingFavorite);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_search_song.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();

                }
                return false;
            }
        });

        iv_search_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lv_sort_types.getVisibility()==View.VISIBLE){
                    showHideSortTypes(false);
                }else {
                    showHideSortTypes(true);
                }
            }
        });
    }

    private void addOnePointToListRating(int idSong){
        for (Song song : songList) {
            if (song.getId() == idSong) {
                song.setRating(song.getRating() + 1);
            }
        }
        long minRating = songList.get(0).getRating();
        long maxRating = songList.get(0).getRating();
        for (Song song : songList) {
            long songRating = song.getRating();
            if (songRating>maxRating){
                maxRating=songRating;
            }
            if (songRating<minRating){
                minRating=songRating;
            }
        }
        Song.current_max_rating = maxRating;
        Song.current_min_rating = minRating;
    }

    private void addOnePointToLocalDBRating(int idSong){
        dataSource.addPointToLocalRating(idSong);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT>10){
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_color));
        }

        MenuInflater menuInflater = getMenuInflater();
        if (!isShowingFavorite){
            menuInflater.inflate(R.menu.menu_off, menu);
        } else {
            menuInflater.inflate(R.menu.menu_on, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.show_favorite){
            songAdapter.showFavorite();
            isShowingFavorite = true;
            songAdapter.searchNew(et_search_song.getText().toString(), isShowingFavorite);
            if (Build.VERSION.SDK_INT > 10) {
                invalidateOptionsMenu();
            }
        } else if (item.getItemId()==R.id.show_all){
            songAdapter.showAll();
            isShowingFavorite = false;
            songAdapter.searchNew(et_search_song.getText().toString(), isShowingFavorite);
            if (Build.VERSION.SDK_INT > 10) {
                invalidateOptionsMenu();
            }
        }
        if (item.getItemId()==R.id.add_song){
            Intent intent = new Intent(this, SubmitActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

    private void sortByName (){
        //Sorting
        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {

                return song1.getName().compareTo(song2.getName());
            }
        });
        songAdapter.updateData(songList);
    }

    private void sortByRating (){
        //Sorting
        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {

                return new Long(song2.getRating()).compareTo(new Long(song1.getRating()));
            }
        });
        songAdapter.updateData(songList);
    }

    private void showHideSortTypes(boolean isShow){
        if(isShow){
            lv_sort_types.setVisibility(View.VISIBLE);
        } else {
            lv_sort_types.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        if (InternetHelper.isConnectionEnabled(this)){
            DBupdater dBupdater = new DBupdater(this,"finish");
            dBupdater.checkAndUpdateTables();
        } else {
            finish();
        }
    }

    private void startTimerUpdating() {
        //Declare the timer
        t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      dBupdater.checkAndUpdateTables();
                                  }

                              },
                //Set how long before to start calling the TimerTask (in milliseconds)
                UPDATE_DATA_TIME,
                //Set the amount of time between each execution (in milliseconds)
                UPDATE_DATA_TIME);
    }

    @Override
    protected void onDestroy() {
        t.cancel();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
    private void sendDataToAnalytics(){
        // Get tracker.
        Tracker t = ((AppController) getApplication()).getTracker(
                AppController.TrackerName.APP_TRACKER);

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("SongTypeActivity");

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void updateUI(){
        songList.clear();
        songList = dataSource.getSongMainInfo();
        songAdapter.updateData(songList);
        if (sortType==0){
            sortByName();
        } else {
            sortByRating();
        }
        songAdapter.searchNew(et_search_song.getText().toString(),isShowingFavorite);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("update_songs_ui")){
                Log.i(TAG,"update ui");
                updateUI();
            }

        }
    };
}
