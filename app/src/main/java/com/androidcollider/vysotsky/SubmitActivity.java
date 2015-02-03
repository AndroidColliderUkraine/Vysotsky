package com.androidcollider.vysotsky;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.vysotsky.adapters.SortTypeAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SubmitActivity extends ActionBarActivity {

    private final static String TAG = "Андроідний Коллайдер";
    private String[] submitArray;
    private Spinner spinner_submit;
    private EditText et_submit;
    private Button b_submit;
    private final static String[] MAILS = new String[]{"android.collider@gmail.com"};
    private ListView lv_submit_types;
    private TextView tv_submit_type;

   /* private TextView tv_song_title, tv_song_text, tv_song_remarks, tv_song_source, tv_song_comments;
    private DataSource dataSource;
    private Song song;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        submitArray = getResources().getStringArray(R.array.submit_array);
        initFields();

        List<String> list = Arrays.asList(submitArray);
        ArrayList<String> submitList = new ArrayList<>();
        submitList.addAll(list);

        SortTypeAdapter adapter = new SortTypeAdapter(this, submitList);
        //ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,submitArray);
        lv_submit_types.setAdapter(adapter);


    }

    private void initFields(){
        //spinner_submit = (Spinner)findViewById(R.id.spinner_submit);
        et_submit = (EditText)findViewById(R.id.et_submit_field);
        et_submit.setPadding(10,10,10,10);
        b_submit = (Button)findViewById(R.id.b_submit);
        b_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        lv_submit_types = (ListView)findViewById(R.id.lv_submit_types);
        tv_submit_type = (TextView)findViewById(R.id.tv_submit_type);

        tv_submit_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lv_submit_types.getVisibility()==View.GONE){
                    lv_submit_types.setVisibility(View.VISIBLE);
                }else{
                    lv_submit_types.setVisibility(View.GONE);
                }
            }
        });


        lv_submit_types.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.i(TAG, songList.toString());
                tv_submit_type.setText(submitArray[position]);
                lv_submit_types.setVisibility(View.GONE);
                }
            }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_submit, menu);
        //getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.route_saver_actionbar_background));
        if (Build.VERSION.SDK_INT>10){
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_color));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return true;
    }

    private void sendEmail(){
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        // Кому
        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                MAILS);
        // Зачем
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                 "Высоцкий");
        // О чём
        emailIntent.putExtra(Intent.EXTRA_TEXT,tv_submit_type.getText().toString()+getString(R.string.double_enter)+
                et_submit.getText().toString());
        // С чем
        /*emailIntent.putExtra(
                android.content.Intent.EXTRA_STREAM,
                Uri.parse("file://"
                        + Environment.getExternalStorageDirectory()
                        + "/Клипы/SOTY_ATHD.mp4"));*/

        //emailIntent.setType("text/video");
        // Поехали!
        startActivity(Intent.createChooser(emailIntent,
                "Відправка листа..."));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

}
