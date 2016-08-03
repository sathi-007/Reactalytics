package reactalytics.cricbuzz.com.sample.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import reactalytics.cricbuzz.com.sample.R;

/**
 * Created by kiran.kumar on 12/4/15.
 */
public class NewsDummyFragment extends Fragment {
    public NewsDummyFragment() {
        super();
    }

    public static NewsDummyFragment newInstance() {
        return new NewsDummyFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View fragmentView = inflater.inflate(R.layout.fragment_news_all, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.initialize();
    }

    private void initialize() {
//            toolbar.setTitle("Matches");
        //toolbarSetupListener.setUpToolbar(toolbar);
        textView.setText("Dummy Screen");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public Context getContext() {
        return this.getActivity();
    }

//    @Override
//    public CoordinatorLayout getCoordinatorLayout() {
//        return coordinatorLayout;
//    }

    @BindView(R.id.txt_header)
    TextView textView;
//
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;

    @BindView(R.id.cl_content)
    CoordinatorLayout coordinatorLayout;

    private Unbinder unbinder;
    private final static String TAG = NewsDummyFragment.class.getSimpleName();

}
