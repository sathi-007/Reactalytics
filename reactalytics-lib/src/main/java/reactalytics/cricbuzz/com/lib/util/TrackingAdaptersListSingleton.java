package reactalytics.cricbuzz.com.lib.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import reactalytics.cricbuzz.com.lib.trackers.TrackingAdapter;

/**
 * Created by sathish-n on 18/5/16.
 */
public class TrackingAdaptersListSingleton {

    public static TrackingAdaptersListSingleton trackingAdaptersListSingleton;

    private HashMap<String,TrackingAdapter> trackingAdapterHashMap = new HashMap<>();

    public static TrackingAdaptersListSingleton getInstance(){
        if(trackingAdaptersListSingleton==null){
            trackingAdaptersListSingleton = new TrackingAdaptersListSingleton();
        }
        return trackingAdaptersListSingleton;
    }

    public void addTrackingAdapter(String key, TrackingAdapter trackingAdapter){
        Log.d(TAG, "TrackerAdapter name "+key.toLowerCase());
        trackingAdapterHashMap.put(key.toLowerCase(),trackingAdapter);
    }

    public TrackingAdapter getTrackingAdapter(String key){
        Log.d(TAG, "TrackerAdapter get adapter "+key.toLowerCase());
        if(trackingAdapterHashMap.containsKey(key.toLowerCase())){
            return trackingAdapterHashMap.get(key);
        }
        return null;
    }

    public ArrayList<TrackingAdapter> getTrackingAdapterList(){
        Iterator it = trackingAdapterHashMap.entrySet().iterator();
        ArrayList<TrackingAdapter> trackingAdapterList = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
            trackingAdapterList.add((TrackingAdapter) pair.getValue());
        }
        return trackingAdapterList;
    }

    public ArrayList<String> getTrackingAdapterFilterList(){
        Iterator it = trackingAdapterHashMap.entrySet().iterator();
        ArrayList<String> trackingAdapterList = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            trackingAdapterList.add((String) pair.getKey());
        }
        return trackingAdapterList;
    }

    private final String TAG = TrackingAdaptersListSingleton.class.getSimpleName();
}
