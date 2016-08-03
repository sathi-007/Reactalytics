package reactalytics.cricbuzz.com.lib.internal;

import java.util.List;

/**
 * Created by sathish-n on 18/5/16.
 */
public interface BaseVerify<T> {

    List<T> getFilterList();

    boolean isMethodExists(String methodName);

    String getScreenName();
}
