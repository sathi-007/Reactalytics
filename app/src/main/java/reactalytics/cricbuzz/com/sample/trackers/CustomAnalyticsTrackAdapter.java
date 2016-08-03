package reactalytics.cricbuzz.com.sample.trackers;

import android.util.Log;

import java.util.Map;

import reactalytics.cricbuzz.com.lib.trackers.TrackingAdapter;
import reactalytics.cricbuzz.com.sample.utils.Constants;

/**
 * Created by sathish-n on 18/5/16.
 */
public class CustomAnalyticsTrackAdapter implements TrackingAdapter {

    @Override
    public void trackEvent(String screenName, String eventName) {
        Log.d(TAG, "TrackEvent "+screenName);
    }

    @Override
    public void trackEvent(String title, Map<String, Object> values) {
        Log.d(TAG, "TrackEvent "+title);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getTrackerType() {
        return Constants.AnalyticsTrackers.CUSTOM_TRACKER;
    }

    private final String TAG = CustomAnalyticsTrackAdapter.class.getSimpleName();
}

