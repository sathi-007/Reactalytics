package reactalytics.cricbuzz.com.lib;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.cricbuzz.android.reactalytics.annotations.BindClass;
import com.cricbuzz.android.reactalytics.annotations.MainBinderClass;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reactalytics.cricbuzz.com.lib.Rx.RxBus;
import reactalytics.cricbuzz.com.lib.internal.AnnotatedInfo;
import reactalytics.cricbuzz.com.lib.internal.BaseVerify;
import reactalytics.cricbuzz.com.lib.service.AnalyticsService;
import reactalytics.cricbuzz.com.lib.trackers.TrackingAdapter;
import reactalytics.cricbuzz.com.lib.util.Constant;
import reactalytics.cricbuzz.com.lib.util.TrackingAdaptersListSingleton;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by sathish-n on 12/5/16.
 */
public class Reactalytics {
    private static RxBus rxBus = new RxBus();
    Application application;
    int delay;
    private final List<TrackingAdapter> trackingAdapters;
    TrackingAdaptersListSingleton trackingAdaptersListSingleton;
    HashMap<String, BaseVerify> baseVerifyHashMap = new HashMap<>();

    Reactalytics(Application applictaion, int delay, List<TrackingAdapter> trackingAdapters) {
        this.application = applictaion;
        this.delay = delay;
        this.trackingAdapters = trackingAdapters;
        this.trackingAdaptersListSingleton = TrackingAdaptersListSingleton.getInstance();
        bindRxReceiver();
        addTrackingAdapters();
        readJsonFile();
        application.registerActivityLifecycleCallbacks(new ReactLifecycleCallbacks(rxBus));
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

            System.out.println("$$$$$$$$$$ Found Reactalytics File #######"+buf.toString());
            readJsonToBindingClass(buf.toString());
        } catch (Exception e) {
            System.out.println("$$$$$$$$$$ Not Found Reactalytics File #######");
        }
    }

    private void readJsonToBindingClass(String json){
        Gson gson = new Gson();
        MainBinderClass mainBinderClass = gson.fromJson(json, MainBinderClass.class);
        System.out.println("$$$$$$$$$$ Found MainBinderClass File #######"+mainBinderClass.getBindClassList().size());
        if(mainBinderClass!=null&&mainBinderClass.getBindClassList()!=null&&mainBinderClass.getBindClassList().size()>0){
            for(BindClass bindClass:mainBinderClass.getBindClassList()){
                bindClassMap.put(bindClass.getClassName(),bindClass);
            }
        }
    }


    private void addTrackingAdapters() {
        if (trackingAdapters != null && trackingAdapters.size() > 0) {
            for (TrackingAdapter trackingAdapter : trackingAdapters) {
                trackingAdaptersListSingleton.addTrackingAdapter(trackingAdapter.getTrackerType(), trackingAdapter);
            }
        }
    }

    private void bindRxReceiver() {
        rxBus.toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d(TAG, "Exception occured " + throwable.getMessage());
                    }
                }).subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event instanceof Event) {
                    Event event1 = (Event) event;
                    String activityName = event1.getScreenName();
                    String methodName = event1.getEventName();
                    String packageName = event1.getPacakageName();

                    Log.d(TAG, "Find view binder class " + packageName + "." + activityName);
                    String className = packageName + "." + activityName;
                    if(bindClassMap.containsKey(className)){
                        BindClass bindClass = bindClassMap.get(className);
                        startAnalyticCall(bindClass.getTrackerList(), bindClass.getScreenName(), methodName);
                    }
//                    BaseVerify baseVerify = getBaseVerify(className);
//                    if (baseVerify != null) {
//                        ArrayList<String> filterList = (ArrayList) baseVerify.getFilterList();
//                        Log.d(TAG, "filterList size " + filterList.size());
//                        if (baseVerify.isMethodExists(methodName)) {
//                            Log.d(TAG, "HIT. Loaded view binder class " + activityName + "_$$_Binder");
//                            startAnalyticCall(filterList, baseVerify.getScreenName(), methodName);
//                        }
//                    }

                }
            }
        });
    }

    public void sendEvent(String screenName, String eventName, ArrayList<String> filters) {
        startAnalyticCall(filters, screenName, eventName);
    }

    public void sendEvent(String screenName, String eventName) {
        startAnalyticCall(trackingAdaptersListSingleton.getTrackingAdapterFilterList(), screenName, eventName);
    }

    private void startAnalyticCall(ArrayList<String> analyticsFilterList, String screenName, String eventName) {
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
            for (String filter:analyticsFilterList) {
                Log.d(TAG, " " + analyticsFilterList.size() + " tracker " + filter);
                TrackingAdapter trackingAdapter = trackingAdaptersListSingleton.getTrackingAdapter(filter);
                trackingAdapter.trackEvent(screenName, eventName);
            }
        }else {
            List<TrackingAdapter> trackingAdapterList = trackingAdaptersListSingleton.getTrackingAdapterList();
            for (TrackingAdapter trackingAdapter : trackingAdapterList) {
                trackingAdapter.trackEvent(screenName, eventName);
            }
        }
    }

    protected static RxBus getRxBus() {
        return rxBus;
    }

    public static void destroy() {
        rxBus = null;
    }

    public static class Builder {
        private final Application app;
        private int delay;
        private List<TrackingAdapter> trackingAdapters = new ArrayList<>();

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

        public Builder trackerList(List<TrackingAdapter> trackingAdapters) {
            if (trackingAdapters == null || trackingAdapters.size() == 0) {
                throw new IllegalArgumentException("TrackingAdapters list should be >0");
            }
            this.trackingAdapters = trackingAdapters;
            return this;
        }

        public Reactalytics build() {
            return new Reactalytics(app, delay, trackingAdapters);
        }
    }

    private final String TAG = Reactalytics.class.getSimpleName();
    private HashMap<String ,BindClass> bindClassMap = new HashMap<>();
}
