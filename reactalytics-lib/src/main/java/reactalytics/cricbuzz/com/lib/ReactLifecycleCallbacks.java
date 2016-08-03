package reactalytics.cricbuzz.com.lib;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import reactalytics.cricbuzz.com.lib.Rx.RxBus;

/**
 * Created by sathish-n on 12/5/16.
 */
final class ReactLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    RxBus rxBus;

    public ReactLifecycleCallbacks(RxBus rxBus) {
        this.rxBus = rxBus;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

        Log.e("ReactLifecycleCallbacks", "onActivityCreated:" + getActivityName(activity));
        rxBus.send(getEvent(activity, "onCreate"));
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.e("ReactLifecycleCallbacks", "onActivityDestroyed:" + getActivityName(activity));
        rxBus.send(getEvent(activity, "onDestroy"));
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("ReactLifecycleCallbacks", "onActivityPaused:" + getActivityName(activity));
        rxBus.send(getEvent(activity, "onPause"));
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.e("ReactLifecycleCallbacks", "onActivityResumed:" + getActivityName(activity));
        rxBus.send(getEvent(activity, "onResume"));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity,
                                            Bundle outState) {
        Log.e("ReactLifecycleCallbacks", "onActivitySaveInstanceState:" + getActivityName(activity));
        rxBus.send(getEvent(activity, "onSaveInstanceState"));
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.e("ReactLifecycleCallbacks", "onActivityStarted:" + getActivityName(activity));
        rxBus.send(getEvent(activity, "onStart"));
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("ReactLifecycleCallbacks", "onActivityStopped:" + getActivityName(activity));
        rxBus.send(getEvent(activity, "onStop"));
    }

    private Event getEvent(Activity activity, String methodName) {
        Event event = new Event();
        event.setScreenName(activity.getLocalClassName());
        event.setEventName(methodName);
        event.setPacakageName(activity.getPackageName());
        return event;
    }

    private String getActivityName(Activity activity) {
        String className =activity.getLocalClassName();
        Log.e("ReactLifecycleCallbacks", "Activity Package:" + activity.getPackageName());
        int pos = className.lastIndexOf('.') + 1;
        String onlyClass = className.substring(pos);
        return onlyClass;
    }
}
