package reactalytics.cricbuzz.com.sample;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import reactalytics.cricbuzz.com.lib.Reactalytics;
import reactalytics.cricbuzz.com.lib.internal.AnnotatedInfo;
import reactalytics.cricbuzz.com.lib.trackers.TrackingAdapter;
import reactalytics.cricbuzz.com.sample.trackers.CustomAnalyticsTrackAdapter;
import reactalytics.cricbuzz.com.sample.trackers.GoogleAnalyticsTrackingAdapter;

/**
 * Created by sathish-n on 13/5/16.
 */
public class SampleApplication extends Application {

    Reactalytics reactalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        reactalytics = new Reactalytics.Builder(this)
                .addAnalyticsTracker(new GoogleAnalyticsTrackingAdapter(getApplicationContext()))
                .addAnalyticsTracker(new CustomAnalyticsTrackAdapter())
                .delay(5)
                .build();
    }

    public Reactalytics getReactalytics() {
        return reactalytics;
    }


   private final String TAG = SampleApplication.class.getSimpleName();
}
