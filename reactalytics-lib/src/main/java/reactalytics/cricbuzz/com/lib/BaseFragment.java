package reactalytics.cricbuzz.com.lib;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import reactalytics.cricbuzz.com.lib.Rx.RxBus;

/**
 * Created by sathish-n on 17/5/16.
 */
public abstract class BaseFragment extends Fragment {

    String className;
    String packageName;
    RxBus rxBus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxBus = Reactalytics.getRxBus();
        Log.e("BaseFragment","onCreate: " + className+" pacakageName "+packageName);
        sendEvent("onCreate");
    }

    public void initialize() {
        String classWithPackage = this.getClass().getName();
        int pos = classWithPackage.lastIndexOf('.') + 1;
        className = classWithPackage.substring(pos);
        packageName = classWithPackage.substring(0,pos-1);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("BaseFragment","onActivityCreated:" + this.getClass().getName());
        sendEvent("onActivityCreated");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("BaseFragment","onCreateView:" + this.getClass().getName());

        sendEvent("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onStart() {
        Log.e("BaseFragment","onStart:" + this.getClass().getName());
        super.onStart();
        sendEvent("onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        sendEvent("onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        sendEvent("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        sendEvent("onPause");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sendEvent("onAttach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sendEvent("onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        sendEvent("onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendEvent("onDestroy");
    }

    public abstract Class<? extends BaseFragment> getName();

    private void sendEvent(String methodName) {
        if(isEmpty(className)||isEmpty(packageName)){
            initialize();
        }

        if(rxBus==null){
            rxBus = Reactalytics.getRxBus();
        }

        Event event = new Event();
        event.setScreenName(className);
        event.setEventName(methodName);
        event.setPacakageName(packageName);
        rxBus.send(event);
    }

    public boolean isEmpty(String str){
        if(str==null||str.length()==0){
            return true;
        }
        return false;
    }

}
