package reactalytics.cricbuzz.com.sample.trackers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import reactalytics.cricbuzz.com.lib.trackers.TrackingAdapter;
import reactalytics.cricbuzz.com.sample.R;
import reactalytics.cricbuzz.com.sample.utils.Constants;

public class GoogleAnalyticsTrackingAdapter implements TrackingAdapter {

    Tracker gaTracker;
    Context context;
    public   GoogleAnalyticsTrackingAdapter(Context context){
        this.context = context;
        System.out.println("GATrackingAdapter Track Initialization ");
        gaTracker = GoogleAnalytics.getInstance(context).newTracker(R.xml.app_tracker);
    }

    @Override
    public void trackEvent(String screeName,String event, Map<String, Object> values) {
        System.out.println("GATrackingAdapter Track Event "+screeName);
        for (Map.Entry<String, Object> map : values.entrySet()) {
            gaTracker.set(map.getKey(),map.getValue().toString());
            System.out.println("GATRackingAdapter key "+map.getKey()+" value "+map.getValue().toString());
        }
        gaTracker.send(new HitBuilders.EventBuilder().setAction(event).build());
        GoogleAnalytics.getInstance(context).dispatchLocalHits();
    }


    @Override
    public void trackScreenView(String screenName) {
        Log.e(TAG, "Send ScreenName " + screenName );
        gaTracker.setScreenName(screenName);
        gaTracker.send(new HitBuilders.ScreenViewBuilder().build());
//        dataLayer.push(screenName, eventName);
    }

    @Override
    public void trackScreenTime(String screenName, long duration) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String toString() {
        return "GoogleAnalytics";
    }

    private final String TAG = GoogleAnalyticsTrackingAdapter.class.getSimpleName();

}
