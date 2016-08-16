package com.cricbuzz.android.reactalytics.annotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by sathish-n on 9/6/16.
 */

public class BindClass {

    private final List<String> methodNames;
    private String screenName;
    private String className;
    private final ArrayList<String> trackerList;
    private HashMap<Integer, Long> trackTimeMap = new HashMap<>();
    private HashMap<String, String> trackMethodEventMap = new HashMap<>();
    private HashMap<String, Integer> trackMethodIdMap = new HashMap<>();

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

    public String getScreenName() {
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

    public void addMethodName(String methodName, String annotation) {
        String annotationClass = this.trackMethodEventMap.get(methodName);
        StringBuilder evenTNames;
        if (annotationClass != null && annotationClass.length() > 0) {
            evenTNames = new StringBuilder(annotationClass);
            evenTNames.append("%$$%" + annotation);
        } else {
            evenTNames = new StringBuilder();
            evenTNames.append(annotation);
        }

        this.trackMethodEventMap.put(methodName, evenTNames.toString());
        this.methodNames.add(methodName);
    }

    public void addTrackTimeMethodId(String methodName, Integer id) {
        this.trackMethodIdMap.put(methodName, id);
    }

    public String getMethodMappedEvents(String methodName){
        return this.trackMethodEventMap.get(methodName);
    }

    public Integer getScreeTimeTrackId(String methodName){
        return this.trackMethodIdMap.get(methodName);
    }

    public void putScreenTimeValue(Integer id,Long startTime){
        this.trackTimeMap.put(id,startTime);
    }

    public Long getScreenTimeValue(Integer id){
        return this.trackTimeMap.get(id);
    }

    public void addTrackerFilter(String trackerList) {
        this.trackerList.add(trackerList);
    }

}
