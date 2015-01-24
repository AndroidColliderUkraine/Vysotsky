package com.androidcollider.vysotsky;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidcollider.vysotsky.database.DBupdater;
import com.androidcollider.vysotsky.database.DataSource;
import com.androidcollider.vysotsky.database.SharedPref;
import com.androidcollider.vysotsky.utils.InternetHelper;


public class SplashScreenActivity extends Activity {

    private ImageView iv_splash_title_main, iv_splash_title_hat;
    private Animation slideUpMain, slideDownHat, fadeInAC;
    private TextView tv_ac, tv_loading_status;
    private Context context;
    private ProgressBar pb_loading_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        context = this;

        tv_loading_status = (TextView) findViewById(R.id.tv_loading_status);

        pb_loading_data = (ProgressBar) findViewById(R.id.pb_loading_new_data);

        slideUpMain = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_hat);
        slideDownHat = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        fadeInAC = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);

        iv_splash_title_main = (ImageView) findViewById(R.id.iv_splash_title_main);
        //iv_splash_title_hat = (ImageView) findViewById(R.id.iv_splash_title_hat);
        tv_ac = (TextView) findViewById(R.id.tv_ac);


        iv_splash_title_main.setAnimation(fadeInAC);
        //iv_splash_title_hat.setAnimation(slideDownHat);
        tv_ac.setAnimation(fadeInAC);


        fadeInAC.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                DataSource dataSource = new DataSource(context);
                SharedPref sPref = new SharedPref(getApplicationContext());
                if (sPref.isNotFirstStart()) {
                    if (InternetHelper.isConnectionEnabled(context)) {
                        DBupdater dBupdater = new DBupdater(context, "start");
                        dBupdater.checkAndUpdateTables();
                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, SongListActivity.class);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                } else {
                    if (InternetHelper.isConnectionEnabled(context)) {
                        sPref.savePref("wasStarted", true);
                        DBupdater dBupdater = new DBupdater(context, "start");
                        dBupdater.checkAndUpdateTables();
                    } else {
                        showSettingsAlert();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setLoadingStatus(String status) {
        tv_loading_status.setText(status);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Base_Theme_AppCompat));
        alertDialog.setTitle("Налаштування інтернету");
        alertDialog.setMessage("При першому запуску Колядника Вам необхідно завантажити базу пісень. Будь ласка, ввімкніть мережу натиснувши кнопку Налаштування");

        alertDialog.setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        alertDialog.setPositiveButton("Налаштування", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                SplashScreenActivity.this.startActivity(intent);
                finish();
            }
        });

        alertDialog.show();
    }

    public void setProgressLoading(int progress) {
        pb_loading_data.setProgress(progress);
    }

    public void setProgressMax(int max) {
        pb_loading_data.setMax(max);
    }

    public void setProgressVisible(int visible) {
        pb_loading_data.setVisibility(visible);
    }


}
