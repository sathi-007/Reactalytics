package reactalytics.cricbuzz.com.lib;

import android.app.Application;
import android.util.Log;

import com.cricbuzz.android.reactalytics.annotations.BindClass;
import com.cricbuzz.android.reactalytics.annotations.MainBinderClass;
import com.cricbuzz.android.reactalytics.annotations.TrackEndTime;
import com.cricbuzz.android.reactalytics.annotations.TrackScreenView;
import com.cricbuzz.android.reactalytics.annotations.TrackStartTime;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import reactalytics.cricbuzz.com.lib.Rx.RxBus;
import reactalytics.cricbuzz.com.lib.trackers.TrackingAdapter;
import reactalytics.cricbuzz.com.lib.util.TrackingAdaptersListSingleton;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by sathish-n on 12/5/16.
 */
public class Reactalytics {

    Reactalytics(Application applictaion, int delay, List<TrackingAdapter> trackingAdapters, HashMap<String, TrackingAdapter> trackingAdapterHashMap) {
        this.application = applictaion;
        this.delay = delay;
        this.trackingAdapters = trackingAdapters;
        this.trackingAdaptersListSingleton = TrackingAdaptersListSingleton.getInstance();
        this.trackingAdapterHashMap = trackingAdapterHashMap;
        bindRxReceiver();
        addTrackingAdapters();
        readJsonFile();
        application.registerActivityLifecycleCallbacks(new ReactLifecycleCallbacks(rxBus));
        this.isInitialized = true;
    }

    private void readJsonFile() {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = application.getAssets().open("reactviewlist.json");
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            readJsonToBindingClass(buf.toString());
        } catch (Exception e) {
            System.out.println("Not Found Reactalytics File");
        }
    }

    private void readJsonToBindingClass(String json) {
        Gson gson = new Gson();
        MainBinderClass mainBinderClass = gson.fromJson(json, MainBinderClass.class);
        if (mainBinderClass != null && mainBinderClass.getBindClassList() != null && mainBinderClass.getBindClassList().size() > 0) {
            for (BindClass bindClass : mainBinderClass.getBindClassList()) {
                bindClassMap.put(bindClass.getClassName(), bindClass);
            }
        }
    }


    private void addTrackingAdapters() {
        Iterator it = trackingAdapterHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
            trackingAdaptersListSingleton.addTrackingAdapter((String) pair.getKey(), (TrackingAdapter) pair.getValue());
        }

    }


    private void bindRxReceiver() {

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = rxBus.toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())

                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Exception occured " + e.getMessage());
                    }

                    @Override
                    public void onNext(Object event) {
                        if (event instanceof Event) {
                            if (!isInitialized) {
                                throw new IllegalStateException("Reactalytics Should be initialized before using it");
                            }

                            Event event1 = (Event) event;
                            String activityName = event1.getScreenName();
                            String methodName = event1.getEventName();

                            if (bindClassMap.containsKey(activityName)) {
                                BindClass bindClass = bindClassMap.get(activityName);

                                if (bindClass.getMethodMappedEvents(methodName) != null) {

                                    String methodEvents = bindClass.getMethodMappedEvents(methodName);
                                    String[] events = methodEvents.split("%\\$\\$%");
                                    Log.d(TAG, "BindClass Found for Event " + activityName + " method name " + methodName);
                                    for (String eventN : events) {
                                        pushAnalytics(eventN, bindClass, methodName);
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "OnComplete Called on RxBus");
                    }
                });
    }

    private void pushAnalytics(String event, BindClass bindClass, String methodName) {
        String trackEndTime = TrackEndTime.class.getSimpleName();
        String trackStartTime = TrackStartTime.class.getSimpleName();
        String trackScreenView = TrackScreenView.class.getSimpleName();
        if (event.contentEquals(trackScreenView)) {
            publishScreenViewAnalyticCall(bindClass.getTrackerList(), bindClass.getScreenName());
        } else if (event.contentEquals(trackStartTime)) {
            Integer id = bindClass.getScreeTimeTrackId(methodName);
            if (id != null) {
                bindClass.putScreenTimeValue(id, (new Date()).getTime());
            }
        } else if (event.contentEquals(trackEndTime)) {
            Integer id = bindClass.getScreeTimeTrackId(methodName);
            if (id != null) {
                Long startTime = bindClass.getScreenTimeValue(id);
                if (startTime > 0) {
                    Long timeDiff = ((new Date()).getTime()) - startTime;
                    Log.d(TAG, "timediff in milliseconds "+timeDiff);
                    if (timeDiff > 0) {
                        publishScreenTimeAnalyticCall(bindClass.getTrackerList(), bindClass.getScreenName(), timeDiff);
                        bindClass.putScreenTimeValue(id, 0L);
                    }
                }
            }
        }
    }

    public void trackScreenView(String screenName) {
        publishScreenViewAnalyticCall(trackingAdaptersListSingleton.getTrackingAdapterFilterList(), screenName);
    }

    public void startTrackTime(Integer id) {
        if (id != null) {
            trackTimeMap.put(id, new Date().getTime());
        }
    }

    public void endTrackTime(Class<?> clazz, Integer id) {
        if (id != null) {
            Long startTime = trackTimeMap.get(id);
            Long timeDiff = new Date().getTime() - startTime;
            if (timeDiff > 0) {
                BindClass bindClass = bindClassMap.get(clazz.getCanonicalName().toString());
                if (bindClass != null) {
                    publishScreenTimeAnalyticCall(bindClass.getTrackerList(), bindClass.getScreenName(), timeDiff);
                } else {
                    publishScreenTimeAnalyticCall(trackingAdaptersListSingleton.getTrackingAdapterFilterList(), clazz.getSimpleName(), timeDiff);
                }
            }
        }
    }

    private void publishScreenViewAnalyticCall(ArrayList<String> analyticsFilterList, String screenName) {
//        Task.Builder taskBuilder = new OneoffTask.Builder()
//                .setExecutionWindow(0, delay);
//        Bundle b = new Bundle();
//        StringBuilder stringBuilder = null;
//        if (analyticsFilterList != null && analyticsFilterList.size() > 0) {
//            stringBuilder = new StringBuilder(analyticsFilterList.get(0));
//            for (int i = 1; i < analyticsFilterList.size(); i++) {
//                stringBuilder.append("$.$" + analyticsFilterList.get(i));
//            }
//        }
//        Log.d(TAG, "startAnalytics calls " + stringBuilder.toString());
//        if (stringBuilder != null) {
//            b.putString(Constant.FILTER_LIST, stringBuilder.toString());
//        }
//        b.putString(Constant.SCREEN_NAME, screenName);
//        b.putString(Constant.EVENT_NAME, event);
//        Task task = taskBuilder.setService(AnalyticsService.class)
//                .setTag(screenName + event)
//                .setExtras(b)
//                .setUpdateCurrent(false)
//                .setRequiredNetwork(1)
//                .setPersisted(false)
//                .build();
//
//        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(application);
//        mGcmNetworkManager.schedule(task);

        if (analyticsFilterList != null && analyticsFilterList.size() > 0) {
            for (String filter : analyticsFilterList) {
                Log.d(TAG, " " + analyticsFilterList.size() + " tracker " + filter);
                TrackingAdapter trackingAdapter = trackingAdaptersListSingleton.getTrackingAdapter(filter);
                trackingAdapter.trackScreenView(screenName);
            }
        } else {
            List<TrackingAdapter> trackingAdapterList = trackingAdaptersListSingleton.getTrackingAdapterList();
            if (trackingAdapterList != null && trackingAdapterList.size() > 0) {
                Log.d(TAG, "TrackingAdapter Found for Event " + trackingAdapterList.size());
                for (TrackingAdapter trackingAdapter : trackingAdapterList) {
                    trackingAdapter.trackScreenView(screenName);
                }
            }
        }
    }

    private void publishScreenTimeAnalyticCall(ArrayList<String> analyticsFilterList, String screenName, Long timeDuration) {
        long diffSeconds = timeDuration / 1000;
        if (analyticsFilterList != null && analyticsFilterList.size() > 0) {
            for (String filter : analyticsFilterList) {
                Log.d(TAG, " " + analyticsFilterList.size() + " tracker " + filter);
                TrackingAdapter trackingAdapter = trackingAdaptersListSingleton.getTrackingAdapter(filter);
                trackingAdapter.trackScreenTime(screenName, diffSeconds);
            }
        } else {
            List<TrackingAdapter> trackingAdapterList = trackingAdaptersListSingleton.getTrackingAdapterList();
            if (trackingAdapterList != null && trackingAdapterList.size() > 0) {
                Log.d(TAG, "TrackingAdapter Found for Event " + trackingAdapterList.size());
                for (TrackingAdapter trackingAdapter : trackingAdapterList) {
                    trackingAdapter.trackScreenTime(screenName, diffSeconds);
                }
            }
        }
    }

    public TrackingAdapter getTrackingAdapter(Class<?> trackingClass) {
        return trackingAdapterHashMap.get(trackingClass.getSimpleName());
    }

    protected static RxBus getRxBus() {
        return rxBus;
    }

    public void destroy() {
        rxBus = null;
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public static class Builder {
        private final Application app;
        private int delay;
        private List<TrackingAdapter> trackingAdapters = new ArrayList<>();
        private HashMap<String, TrackingAdapter> trackingAdapterHashMap = new HashMap<>();

        public Builder(Application app) {
            if (app == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.app = app;
        }

        public Builder delay(int delay) {
            if (delay < 0) {
                throw new IllegalArgumentException("Delay Should be >= 0");
            }
            this.delay = delay;
            return this;
        }

        public Builder addAnalyticsTracker(TrackingAdapter trackingAdapter) {
            if (trackingAdapter == null) {
                throw new IllegalArgumentException("TrackingAdapter list should be null");
            }
            this.trackingAdapters.add(trackingAdapter);
            this.trackingAdapterHashMap.put(trackingAdapter.getClass().getSimpleName(), trackingAdapter);
            return this;
        }

        public Reactalytics build() {
            return new Reactalytics(app, delay, trackingAdapters, trackingAdapterHashMap);
        }
    }

    private boolean isInitialized = false;
    private final String TAG = Reactalytics.class.getSimpleName();
    private final HashMap<String, BindClass> bindClassMap = new HashMap<>();
    private HashMap<String, TrackingAdapter> trackingAdapterHashMap = new HashMap<>();
    private static RxBus rxBus = new RxBus();
    Application application;
    int delay;
    private HashMap<Integer, Long> trackTimeMap = new HashMap<>();
    private final List<TrackingAdapter> trackingAdapters;
    TrackingAdaptersListSingleton trackingAdaptersListSingleton;
    Subscription subscription;
}
