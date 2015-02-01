package com.androidcollider.vysotsky.utils;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.util.HashMap;

/**
 * Created by pseverin on 05.11.14.
 */
@ReportsCrashes(
        formKey = "",
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri ="http://560671.acolider.web.hosting-test.net/MAB-LAB-master/MAB-LAB-master/report/report.php"
        )

public class AppController extends Application {

    private static final String PROPERTY_ID = "UA-58437769-2";
    public static final String TAG = "Андроідний Коллайдер";
    public static final String BASE_URL_KEY = "http://560671.acolider.web.hosting-test.net/VV/API/";

    private RequestQueue mRequestQueue;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        mInstance = this;

    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    public boolean isRequestQoeueFinished(){
        if (mRequestQueue != null) {
            return false;
        } else {
            return true;
        }
    }

     public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = null;
            if (trackerId == TrackerName.APP_TRACKER){
                t = analytics.newTracker(PROPERTY_ID);
            }

            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
