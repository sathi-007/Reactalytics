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
    public void trackEvent(String screeName, String event, Map<String, Object> values) {
        Log.d(TAG, "TrackEvent Called");
    }

    @Override
    public void trackScreenTime(String screenName, long duration) {
        Log.d(TAG, "TrackScreenTime Called");
    }

    @Override
    public void trackScreenView(String screenName) {
        Log.d(TAG, "TrackScreenView Called");
    }

    @Override
    public void destroy() {

    }

    private final String TAG = CustomAnalyticsTrackAdapter.class.getSimpleName();
}

