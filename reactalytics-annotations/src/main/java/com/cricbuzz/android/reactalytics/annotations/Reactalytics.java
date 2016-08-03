package com.cricbuzz.android.reactalytics.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by sathish-n on 13/5/16.
 */

@Retention(RUNTIME) @Target(value=TYPE)
public @interface Reactalytics {

    String[] value() default {};

    String screenName() default "";
}
