package com.androidcollider.vysotsky;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcollider.vysotsky.database.DataSource;
import com.androidcollider.vysotsky.objects.Comment;
import com.androidcollider.vysotsky.objects.Song;
import com.androidcollider.vysotsky.utils.NumberConverter;


public class TextActivity extends Activity {

    private final static String TAG = "Андроідний Коллайдер";

    private TextView tv_song_title, tv_song_text, tv_song_remarks, tv_song_source, tv_song_comments;
    private EditText et_user_name, et_comment;
    private Button btn_comment;
    private DataSource dataSource;
    private Song song;
    private ImageView iv_minus, iv_plus;
    private int textSize;
    private SharedPreferences sharedPreferences;
    private StringBuffer commentSB;
    private final static String APP_PREFERENCES = "KoljadnikPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        this.sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        textSize = sharedPreferences.getInt("fontSize", 14);

        Intent intent = getIntent();
        song = intent.getParcelableExtra("Song");
        dataSource = new DataSource(this);
        song = dataSource.getSongAdvancedInfo(song);
        Log.i(TAG,song.toString());
        initFields();
    }

    private void initFields(){
        tv_song_text = (TextView)findViewById(R.id.tv_song_text);
        tv_song_text.setText(song.getText());

        tv_song_text.setTextSize(textSize);
        ((TextView)findViewById(R.id.tv_commentari)).setTextSize(textSize+1);

        iv_minus = (ImageView)findViewById(R.id.iv_minus);
        iv_plus = (ImageView)findViewById(R.id.iv_plus);

        iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textSize<25){
                    textSize++;
                    sharedPreferences.edit().putInt("fontSize",textSize).commit();
                    tv_song_text.setTextSize(textSize);
                    ((TextView)findViewById(R.id.tv_commentari)).setTextSize(textSize+1);
                }
            }
        });

        iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textSize>10){
                    textSize--;
                    sharedPreferences.edit().putInt("fontSize",textSize).commit();

                    tv_song_text.setTextSize(textSize);
                    ((TextView)findViewById(R.id.tv_commentari)).setTextSize(textSize+1);
                }
            }
        });

        tv_song_comments = (TextView)findViewById(R.id.tv_song_comments);

        et_user_name = (EditText)findViewById(R.id.et_username);
        et_comment = (EditText)findViewById(R.id.et_comment_field);
        btn_comment = (Button)findViewById(R.id.btn_comment);
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_user_name.getText().toString().isEmpty()&&!et_comment.getText().toString().isEmpty()){
                    Comment newComment = new Comment(0,song.getId(),et_user_name.getText().toString(),
                            et_comment.getText().toString(),NumberConverter.longToDateConverter(System.currentTimeMillis()));
                    addOneCommentToCommentView(newComment);
                    dataSource.addCommentToLocal(newComment);
                    Toast.makeText(getApplicationContext(),getString(R.string.succesfull_comm_add_message),Toast.LENGTH_SHORT).show();
                }

            }
        });


        addAllComments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_text, menu);
        if (Build.VERSION.SDK_INT>10){
            if (getActionBar()!=null){
                getActionBar().setTitle(song.getName());
                getActionBar().setDisplayHomeAsUpEnabled(true);
            }
            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_color));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.share_song){
            shareSong();
        }
        if (item.getItemId()==R.id.sms_song){
            sendSms();
        }
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }
    private void shareSong(){
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("plain/text");

        // Зачем
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                "Колядка - "+song.getName());
        // О чём
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                song.getText()+getString(R.string.slava_ukraini));
        // С чем
        /*emailIntent.putExtra(
                android.content.Intent.EXTRA_STREAM,
                Uri.parse("file://"
                        + Environment.getExternalStorageDirectory()
                        + "/Клипы/SOTY_ATHD.mp4"));*/

        //emailIntent.setType("text/video");
        // Поехали!
        startActivity(Intent.createChooser(emailIntent,
                "Відправка пісні..."));
    }
    private void sendSms(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
        intent.putExtra( "sms_body", song.getText()+getString(R.string.slava_ukraini));
        startActivity(intent);
    }

    private void addAllComments(){
        commentSB = new StringBuffer();

        for (Comment comment: song.getComments()){
            commentSB.append("  "+comment.getUserName()+"       "+ comment.getDatePosted()+"\n");
            commentSB.append(comment.getText()+"\n\n");
        }
        tv_song_comments.setText(commentSB);
    }

    private void addOneCommentToCommentView(Comment comment){
        commentSB.append("  "+comment.getUserName()+"       "+ comment.getDatePosted()+"\n");
        commentSB.append(comment.getText()+"\n\n");
        tv_song_comments.setText(commentSB);
    }

}
