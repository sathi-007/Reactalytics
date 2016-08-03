package reactalytics.cricbuzz.com.lib.service;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.util.List;

import reactalytics.cricbuzz.com.lib.trackers.TrackingAdapter;
import reactalytics.cricbuzz.com.lib.util.Constant;
import reactalytics.cricbuzz.com.lib.util.TrackingAdaptersListSingleton;

/**
 * Created by sathish-n on 18/5/16.
 */
public class AnalyticsService extends GcmTaskService {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d(TAG, "onRunTask");

        TrackingAdaptersListSingleton trackingAdaptersListSingleton = TrackingAdaptersListSingleton.getInstance();

        final Bundle extras = taskParams.getExtras();
        String filterList = extras.getString(Constant.FILTER_LIST);
        String[] trackers = null;
        String screenName = extras.getString(Constant.SCREEN_NAME);
        String eventName = extras.getString(Constant.EVENT_NAME);
        if (filterList != null && filterList.length() > 0) {
            trackers = filterList.split("\\$.\\$");
            if (trackers != null && trackers.length > 0) {

                for (int i = 0; i < trackers.length; i++) {
                    Log.d(TAG, " " + trackers.length + " tracker " + trackers[i]);
                    TrackingAdapter trackingAdapter = trackingAdaptersListSingleton.getTrackingAdapter(trackers[i]);
                    trackingAdapter.trackEvent(screenName, eventName);
                }
                return GcmNetworkManager.RESULT_SUCCESS;
            }
        } else {
            List<TrackingAdapter> trackingAdapterList = trackingAdaptersListSingleton.getTrackingAdapterList();
            for (TrackingAdapter trackingAdapter : trackingAdapterList) {
                trackingAdapter.trackEvent(screenName, eventName);
            }
            return GcmNetworkManager.RESULT_SUCCESS;
        }

        return GcmNetworkManager.RESULT_FAILURE;
    }

    private final String TAG = AnalyticsService.class.getSimpleName();
}
