package com.cricbuzz.android.reactalytics.annotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sathish-n on 9/6/16.
 */

public class BindClass {

    private final List<String> methodNames;
    private String screenName;
    private String className;
    private final ArrayList<String> trackerList;

    public BindClass(String screenName) {
        this.methodNames = new ArrayList<>();
        this.trackerList = new ArrayList<>();
        this.screenName = screenName;
    }

    public void setScreenName(String screenName) {
        if (screenName != null && screenName.length() > 0) {
            this.screenName = screenName;
        }
    }

    public String getScreenName(){
        return screenName;
    }

    public List<String> getMethodNames() {
        return methodNames;
    }

    public ArrayList<String> getTrackerList() {
        return trackerList;
    }

    public String getClassName() {

        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void addMethodName(String methodName) {
        this.methodNames.add(methodName);
    }

    public void addTrackerFilter(String trackerList) {
        this.trackerList.add(trackerList);
    }
}
