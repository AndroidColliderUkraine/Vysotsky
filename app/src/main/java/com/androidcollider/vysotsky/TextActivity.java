package com.androidcollider.vysotsky;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcollider.vysotsky.database.DataSource;
import com.androidcollider.vysotsky.database.SharedPref;
import com.androidcollider.vysotsky.objects.Comment;
import com.androidcollider.vysotsky.objects.Song;
import com.androidcollider.vysotsky.utils.AccordUtil;
import com.androidcollider.vysotsky.utils.AppController;
import com.androidcollider.vysotsky.utils.NumberConverter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Timer;
import java.util.TimerTask;


public class TextActivity extends ActionBarActivity {

    private final static String TAG = "Андроідний Коллайдер";
    private DrawerLayout mDrawerLayout;
    private TextView tv_user_name, tv_date_posted, tv_comment_text, tv_scroll_speed, tv_acc_number;
    private WebView tv_song_text;
    private LayoutInflater lInflater;
    private RelativeLayout help_container, help_dr_container;
    private EditText et_user_name, et_comment;
    private Button btn_comment;
    private DataSource dataSource;
    private Song song;
    private ImageView iv_minus, iv_plus, iv_down_acc, iv_up_acc, iv_down_scroll, iv_up_scroll;
    private int textSize;
    private SharedPreferences sharedPreferences;
    private final static String APP_PREFERENCES = "KoljadnikPref";
    private String text;
    private ScrollView text_scrollView;
    private int scrollingSpeed = 0;
    private int accordNumber = 0;
    private boolean isFavorite;
    private LinearLayout ll_song_comments;
    private View view;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPref sPref;
    private boolean isFullScreen;

    private Timer scrollTimer = null;
    private TimerTask scrollerSchedule;
    private int scrollPos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        showProgressDialog();
        sendDataToAnalytics();
        this.sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        textSize = sharedPreferences.getInt("fontSize", 14);

        sPref = new SharedPref(this);
        drawerInit();

        Intent intent = getIntent();
        song = intent.getParcelableExtra("Song");
        dataSource = new DataSource(this);
        song = dataSource.getSongAdvancedInfo(song);
        isFavorite = song.isFavorite();
        Log.i(TAG, song.toString());
        text = song.getText();

        checkViewButtons();
        initFields();
    }

    private void initFields() {
        //tv_song_text = (TextView) findViewById(R.id.tv_song_text);
        tv_song_text = (WebView) findViewById(R.id.tv_song_text);
        //tv_song_text.loadData("<style>  body background-color: #f0f0f0;}pre{white-space:pre-wrap;font-family: 'Open Sans', sans-serif; </style>"+text,"text/html", "en_US");
        tv_song_text.loadDataWithBaseURL(null, "<style> body { background-color: #f0f0f0; } pre{ white-space:pre-wrap; font-family: 'Open Sans', sans-serif; font-size: " + textSize + "pt;} </style>" + text, "text/html", "UTF-8", null);
        //tv_song_text.setText(Html.fromHtml(text));

        //tv_song_text.setTextSize(textSize);
        ((TextView) findViewById(R.id.tv_commentari)).setTextSize(15);

        iv_minus = (ImageView) findViewById(R.id.iv_minus);
        iv_plus = (ImageView) findViewById(R.id.iv_plus);

        iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontPlus();
            }
        });

        iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontMinus();
            }
        });

        //tv_song_comments = (TextView) findViewById(R.id.tv_song_comments);

        et_user_name = (EditText) findViewById(R.id.et_username);
        et_user_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();

                }
                return false;
            }
        });

        et_user_name.setPadding(6, 0, 0, 0);

        et_comment = (EditText) findViewById(R.id.et_comment_field);
        et_comment.setPadding(8, 8, 8, 8);
        btn_comment = (Button) findViewById(R.id.btn_comment);
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_user_name.getText().toString().isEmpty() && !et_comment.getText().toString().isEmpty()) {
                    Comment newComment = new Comment(0, song.getId(), et_user_name.getText().toString(),
                            et_comment.getText().toString(), NumberConverter.longToDateConverter(System.currentTimeMillis()));
                    addOneCommentToCommentView(newComment);
                    dataSource.addCommentToLocal(newComment);
                    Toast.makeText(getApplicationContext(), getString(R.string.succesfull_comm_add_message), Toast.LENGTH_SHORT).show();
                    et_user_name.setText("");
                    et_comment.setText("");
                }

            }
        });

        iv_down_acc = (ImageView) findViewById(R.id.iv_down_acc);
        iv_up_acc = (ImageView) findViewById(R.id.iv_up_acc);

        tv_acc_number = (TextView) findViewById(R.id.tv_acc_number);
        iv_down_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accDown();
            }
        });

        iv_up_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accUp();
            }
        });


        text_scrollView = (ScrollView) findViewById(R.id.text_scrollView);

        iv_down_scroll = (ImageView) findViewById(R.id.iv_down_scroll);
        iv_up_scroll = (ImageView) findViewById(R.id.iv_up_scroll);

        tv_scroll_speed = (TextView) findViewById(R.id.tv_scroll_speed);
        iv_down_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollDown();
            }
        });

        iv_up_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUp();
            }
        });

        findViewById(R.id.iv_full_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFullScreen) {
                    fullScreen(true);
                } else {
                    fullScreen(false);
                }
            }
        });

        lInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ll_song_comments = (LinearLayout) findViewById(R.id.ll_song_comments);
        //ll_song_comments.setDividerPadding(20);
        CheckBox cb_help = (CheckBox)findViewById(R.id.cb_help);
        help_container = (RelativeLayout)findViewById(R.id.help_container);
        help_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                help_container.setVisibility(View.GONE);
            }
        });
        cb_help.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sPref.setShowHelp(false);
                    help_container.setVisibility(View.GONE);
                } else {
                    sPref.setShowHelp(true);
                }
            }
        });


        addAllComments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (!isFavorite) {
            menuInflater.inflate(R.menu.menu_off, menu);
        } else {
            menuInflater.inflate(R.menu.menu_on, menu);
        }
        if (Build.VERSION.SDK_INT > 10) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(song.getName());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_color));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_favorite) {
            dataSource.makeSongFavorite(song.getId(), true);
            isFavorite = true;
            if (Build.VERSION.SDK_INT > 10) {
                invalidateOptionsMenu();
            }
            Intent intent = new Intent();
            intent.setAction("update_songs_ui");
            sendBroadcast(intent);
        } else if (item.getItemId() == R.id.show_all) {
            dataSource.makeSongFavorite(song.getId(), false);
            isFavorite = false;
            if (Build.VERSION.SDK_INT > 10) {
                invalidateOptionsMenu();
            }
        } else if (item.getItemId() == R.id.add_song) {
            Intent intent = new Intent(this, SubmitActivity.class);
            startActivity(intent);
        }
        /*if (item.getItemId() == R.id.sms_song) {
            sendSms();
        }*/
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    /*private void shareSong() {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("plain/text");

        // Зачем
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                "Колядка - " + song.getName());
        // О чём
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                song.getText() + getString(R.string.slava_ukraini));
        // С чем
        *//*emailIntent.putExtra(
                android.content.Intent.EXTRA_STREAM,
                Uri.parse("file://"
                        + Environment.getExternalStorageDirectory()
                        + "/Клипы/SOTY_ATHD.mp4"));*//*

        //emailIntent.setType("text/video");
        // Поехали!
        startActivity(Intent.createChooser(emailIntent,
                "Відправка пісні..."));
    }

    private void sendSms() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
        intent.putExtra("sms_body", song.getText() + getString(R.string.slava_ukraini));
        startActivity(intent);
    }*/

    private void addAllComments() {

        for (Comment comment : song.getComments()) {
            view = lInflater.inflate(R.layout.item_comment, null);
            tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            tv_date_posted = (TextView) view.findViewById(R.id.tv_date_posted);
            tv_comment_text = (TextView) view.findViewById(R.id.tv_comment_text);

            tv_comment_text.setText(comment.getText());
            tv_user_name.setText(comment.getUserName());
            tv_date_posted.setText(comment.getDatePosted().substring(0, comment.getDatePosted().length() - 3));
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lParams.setMargins(0,0,0,15);
            ll_song_comments.addView(view,lParams);
        }
        //tv_song_comments.setText(commentSB);
    }

    private void addOneCommentToCommentView(Comment comment) {
        view = lInflater.inflate(R.layout.item_comment, null);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        tv_date_posted = (TextView) view.findViewById(R.id.tv_date_posted);
        tv_comment_text = (TextView) view.findViewById(R.id.tv_comment_text);

        tv_comment_text.setText(comment.getText());
        tv_user_name.setText(comment.getUserName());
        tv_date_posted.setText(comment.getDatePosted().substring(0, comment.getDatePosted().length() - 3));
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParams.setMargins(0,0,0,15);
        ll_song_comments.addView(view,lParams);
    }


    public void startAutoScrolling() {
        if (scrollTimer == null) {
            scrollTimer = new Timer();
            final Runnable Timer_Tick = new Runnable() {
                public void run() {
                    moveScrollView();
                }
            };

            if (scrollerSchedule != null) {
                scrollerSchedule.cancel();
                scrollerSchedule = null;
            }
            scrollerSchedule = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(Timer_Tick);
                }
            };

            scrollTimer.schedule(scrollerSchedule, 0, Math.abs(200 / scrollingSpeed));
        }
    }

    public void moveScrollView() {
        if (scrollingSpeed > 0) {
            scrollPos = (int) (text_scrollView.getScrollY() + 1.0);
        } else {
            scrollPos = (int) (text_scrollView.getScrollY() - 1.0);
        }
        text_scrollView.scrollTo(0, scrollPos);

    }


    public void stopAutoScrolling() {
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer = null;
        }
    }


    private void drawerInit() {
        final ImageView iv_1_font = (ImageView) findViewById(R.id.iv_1_font);
        ImageView iv_2_font = (ImageView) findViewById(R.id.iv_2_font);
        ImageView iv_3_font = (ImageView) findViewById(R.id.iv_3_font);

        final ImageView iv_1_acc = (ImageView) findViewById(R.id.iv_1_acc);
        ImageView iv_2_acc = (ImageView) findViewById(R.id.iv_2_acc);
        final TextView tv_2_acc = (TextView) findViewById(R.id.tv_2_acc);
        ImageView iv_3_acc = (ImageView) findViewById(R.id.iv_3_acc);

        final ImageView iv_1_scroll = (ImageView) findViewById(R.id.iv_1_scroll);
        ImageView iv_2_scroll = (ImageView) findViewById(R.id.iv_2_scroll);
        final TextView tv_2_scroll = (TextView) findViewById(R.id.tv_2_scroll);
        ImageView iv_3_scroll = (ImageView) findViewById(R.id.iv_3_scroll);

        final ImageView iv_1_screen = (ImageView) findViewById(R.id.iv_1_screen);
        ImageView iv_2_screen = (ImageView) findViewById(R.id.iv_2_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        iv_1_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sPref.getShowFonts()) {
                    sPref.setShowFonts(false);
                    iv_1_font.setImageDrawable(getResources().getDrawable(R.drawable.check_gray));
                    findViewById(R.id.font_container).setVisibility(View.GONE);
                } else {
                    sPref.setShowFonts(true);
                    iv_1_font.setImageDrawable(getResources().getDrawable(R.drawable.check_green));
                    findViewById(R.id.font_container).setVisibility(View.VISIBLE);
                }
            }
        });

        iv_1_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sPref.getShowAcc()) {
                    sPref.setShowAcc(false);
                    iv_1_acc.setImageDrawable(getResources().getDrawable(R.drawable.check_gray));
                    findViewById(R.id.acc_container).setVisibility(View.GONE);
                } else {
                    sPref.setShowAcc(true);
                    iv_1_acc.setImageDrawable(getResources().getDrawable(R.drawable.check_green));
                    findViewById(R.id.acc_container).setVisibility(View.VISIBLE);
                }
            }
        });

        iv_1_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sPref.getShowScroll()) {
                    sPref.setShowScroll(false);
                    iv_1_scroll.setImageDrawable(getResources().getDrawable(R.drawable.check_gray));
                    findViewById(R.id.scroll_container).setVisibility(View.GONE);
                } else {
                    sPref.setShowScroll(true);
                    iv_1_scroll.setImageDrawable(getResources().getDrawable(R.drawable.check_green));
                    findViewById(R.id.scroll_container).setVisibility(View.VISIBLE);
                }
            }
        });

        iv_1_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sPref.getShowScreen()) {
                    sPref.setShowScreen(false);
                    iv_1_screen.setImageDrawable(getResources().getDrawable(R.drawable.check_gray));
                    findViewById(R.id.iv_full_screen).setVisibility(View.GONE);
                } else {
                    sPref.setShowScreen(true);
                    iv_1_screen.setImageDrawable(getResources().getDrawable(R.drawable.check_green));
                    findViewById(R.id.iv_full_screen).setVisibility(View.VISIBLE);
                }
            }
        });


        iv_2_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontMinus();
            }
        });

        iv_3_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontPlus();
            }
        });


        iv_2_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accDown();
                tv_2_acc.setText(String.valueOf(Math.abs(scrollingSpeed)));
                if (accordNumber > 0) {
                    tv_2_acc.setText("+" + String.valueOf(accordNumber));
                } else {
                    tv_2_acc.setText(String.valueOf(accordNumber));
                }
            }
        });

        iv_3_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accUp();
                tv_2_scroll.setText(String.valueOf(Math.abs(scrollingSpeed)));
                if (accordNumber > 0) {
                    tv_2_acc.setText("+" + String.valueOf(accordNumber));
                } else {
                    tv_2_acc.setText(String.valueOf(accordNumber));
                }
            }
        });


        iv_2_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollDown();
                String scrl = null;
                if (scrollingSpeed>0){
                    scrl = "+"+String.valueOf(scrollingSpeed);
                } else {
                    scrl = String.valueOf(scrollingSpeed);
                }
                tv_2_scroll.setText(scrl);
            }
        });

        iv_3_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUp();
                String scrl = null;
                if (scrollingSpeed>0){
                    scrl = "+"+String.valueOf(scrollingSpeed);
                } else {
                    scrl = String.valueOf(scrollingSpeed);
                }
                tv_2_scroll.setText(scrl);
            }
        });

        iv_2_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFullScreen) {
                    fullScreen(true);
                } else {
                    fullScreen(false);
                }
            }
        });


        CheckBox cb_dr_help = (CheckBox)findViewById(R.id.cb_dr_help);
        help_dr_container = (RelativeLayout)findViewById(R.id.help_dr_container);
        help_dr_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                help_dr_container.setVisibility(View.GONE);
            }
        });

        if (!sPref.getShowHelpDr()) {
            findViewById(R.id.help_dr_container).setVisibility(View.GONE);
        }

        cb_dr_help.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sPref.setShowHelpDr(false);
                    help_dr_container.setVisibility(View.GONE);
                } else {
                    sPref.setShowHelpDr(true);
                }
            }
        });


        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        );
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (sPref.getShowFonts()) {
                    iv_1_font.setImageDrawable(getResources().getDrawable(R.drawable.check_green));
                } else {
                    iv_1_font.setImageDrawable(getResources().getDrawable(R.drawable.check_gray));
                }

                if (sPref.getShowAcc()) {
                    iv_1_acc.setImageDrawable(getResources().getDrawable(R.drawable.check_green));
                } else {
                    iv_1_acc.setImageDrawable(getResources().getDrawable(R.drawable.check_gray));
                }

                if (sPref.getShowScroll()) {
                    iv_1_scroll.setImageDrawable(getResources().getDrawable(R.drawable.check_green));
                } else {
                    iv_1_scroll.setImageDrawable(getResources().getDrawable(R.drawable.check_gray));
                }

                if (sPref.getShowScreen()) {
                    iv_1_screen.setImageDrawable(getResources().getDrawable(R.drawable.check_green));
                } else {
                    iv_1_screen.setImageDrawable(getResources().getDrawable(R.drawable.check_gray));
                }

                tv_2_scroll.setText(String.valueOf(Math.abs(scrollingSpeed)));
                if (accordNumber > 0) {
                    tv_2_acc.setText("+" + String.valueOf(accordNumber));
                } else {
                    tv_2_acc.setText(String.valueOf(accordNumber));
                }
                help_container.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void checkViewButtons() {
        if (!sPref.getShowFonts()) {
            findViewById(R.id.font_container).setVisibility(View.GONE);
        }

        if (!sPref.getShowAcc()) {
            findViewById(R.id.acc_container).setVisibility(View.GONE);
        }

        if (!sPref.getShowScroll()) {
            findViewById(R.id.scroll_container).setVisibility(View.GONE);
        }

        if (!sPref.getShowScreen()) {
            findViewById(R.id.iv_full_screen).setVisibility(View.GONE);
        }

        if (!sPref.getShowHelp()) {
            findViewById(R.id.help_container).setVisibility(View.GONE);
        }
    }

    private void fontPlus() {
        if (textSize < 35) {
            textSize++;
            sharedPreferences.edit().putInt("fontSize", textSize).commit();
            tv_song_text.loadDataWithBaseURL(null, "<style> body { background-color: #f0f0f0; } pre{ white-space:pre-wrap; font-family: 'Open Sans', sans-serif; font-size: " + textSize + "pt;} </style>" + text, "text/html", "UTF-8", null);

        }
    }

    private void fontMinus() {
        if (textSize > 5) {
            textSize--;
            sharedPreferences.edit().putInt("fontSize", textSize).commit();
            tv_song_text.loadDataWithBaseURL(null, "<style> body { background-color: #f0f0f0; } pre{ white-space:pre-wrap; font-family: 'Open Sans', sans-serif; font-size: " + textSize + "pt;} </style>" + text, "text/html", "UTF-8", null);
        }
    }


    private void scrollUp() {
            scrollingSpeed--;
            String scrl = null;
            if (scrollingSpeed>0){
                scrl = "+"+String.valueOf(scrollingSpeed);
            } else {
                scrl = String.valueOf(scrollingSpeed);
            }
            tv_scroll_speed.setText(scrl);
            if (scrollingSpeed == 0) {
                stopAutoScrolling();
            } else {
                stopAutoScrolling();
                startAutoScrolling();
            }
    }

    private void scrollDown() {
            scrollingSpeed++;
            String scrl = null;
            if (scrollingSpeed>0){
                scrl = "+"+String.valueOf(scrollingSpeed);
            } else {
                scrl = String.valueOf(scrollingSpeed);
            }
            tv_scroll_speed.setText(scrl);
            if (scrollingSpeed == 0) {
                stopAutoScrolling();
            } else {
                stopAutoScrolling();
                startAutoScrolling();
            }
    }


    private void accDown() {
        text = AccordUtil.downAccord(text);
        accordNumber--;
        if (accordNumber > 0) {
            tv_acc_number.setText("+" + String.valueOf(accordNumber));
        } else {
            tv_acc_number.setText(String.valueOf(accordNumber));
        }
        tv_song_text.loadDataWithBaseURL(null, "<style> body { background-color: #f0f0f0; } pre{ white-space:pre-wrap; font-family: 'Open Sans', sans-serif; font-size: " + textSize + "pt;} </style>" + text, "text/html", "UTF-8", null);
    }

    private void accUp() {
        text = AccordUtil.upAccord(text);
        accordNumber++;
        if (accordNumber > 0) {
            tv_acc_number.setText("+" + String.valueOf(accordNumber));
        } else {
            tv_acc_number.setText(String.valueOf(accordNumber));
        }
        tv_song_text.loadDataWithBaseURL(null, "<style> body { background-color: #f0f0f0; } pre{ white-space:pre-wrap; font-family: 'Open Sans', sans-serif; font-size: " + textSize + "pt;} </style>" + text, "text/html", "UTF-8", null);

    }

    private void fullScreen(boolean fs) {
        if (fs) {
            isFullScreen = true;
            //getSupportActionBar().hide();
            findViewById(R.id.tv_commentari).setVisibility(View.GONE);
            findViewById(R.id.ll_song_comments).setVisibility(View.GONE);
            findViewById(R.id.new_comment_container).setVisibility(View.GONE);
            findViewById(R.id.textView).setVisibility(View.GONE);

            if (Build.VERSION.SDK_INT < 16) {
                getSupportActionBar().hide();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                // Remember that you should never show the action bar if the
                // status bar is hidden, so hide that too if necessary.
                ActionBar actionBar = getSupportActionBar();
                actionBar.hide();
            }

        } else {
            isFullScreen = false;
            //getSupportActionBar().hide();
            findViewById(R.id.tv_commentari).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_song_comments).setVisibility(View.VISIBLE);
            findViewById(R.id.new_comment_container).setVisibility(View.VISIBLE);
            findViewById(R.id.textView).setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT < 16) {
                getSupportActionBar().show();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
                // Remember that you should never show the action bar if the
                // status bar is hidden, so hide that too if necessary.
                ActionBar actionBar = getSupportActionBar();
                actionBar.show();
            }
        }

    }

    private void sendDataToAnalytics(){
        // Get tracker.
        Tracker t = ((AppController) getApplication()).getTracker(
                AppController.TrackerName.APP_TRACKER);

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("TextACtivity");

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void showProgressDialog(){
        final ProgressDialog pd = new ProgressDialog(TextActivity.this);
        pd.setMessage(getString(R.string.wait_for_download_text));
        pd.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pd.dismiss();
                findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
            }
        }, 1000);
    }


    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
