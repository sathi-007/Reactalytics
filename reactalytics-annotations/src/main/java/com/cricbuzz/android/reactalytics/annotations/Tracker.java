package com.cricbuzz.android.reactalytics.annotations;

public enum Tracker {
  ADJUST("adjust"),
  CRITTERCISM("crittercism"),
  FABRIC("fabric"),
  GOOGLE_ANALYTICS("ga"),
  MIXPANEL("mixpanel"),
  SNOWPLOW("snowplow");

  private final String value;

  Tracker(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
