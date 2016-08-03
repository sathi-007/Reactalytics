package reactalytics.cricbuzz.com.lib.trackers;

import java.util.Map;

public interface TrackingAdapter {

  void trackEvent(String title, Map<String, Object> values);

  void trackEvent(String screenName, String eventName);

  void start();

  void stop();
  
  String getTrackerType();
}
