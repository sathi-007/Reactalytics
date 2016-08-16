package reactalytics.cricbuzz.com.lib.trackers;

import java.util.Map;

public interface TrackingAdapter {

  void trackEvent(String screeName,String event,Map<String, Object> values);

  void trackScreenView(String screenName);

  void trackScreenTime(String screenName, long duration);

  void destroy();
}
