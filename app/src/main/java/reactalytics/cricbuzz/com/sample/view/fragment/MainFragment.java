package reactalytics.cricbuzz.com.sample.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;


import com.cricbuzz.android.reactalytics.annotations.Reactalytics;
import com.cricbuzz.android.reactalytics.annotations.TrackEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import reactalytics.cricbuzz.com.lib.BaseFragment;
import reactalytics.cricbuzz.com.sample.R;
import reactalytics.cricbuzz.com.sample.utils.Constants;
import reactalytics.cricbuzz.com.sample.utils.ToolbarSetupListener;
import reactalytics.cricbuzz.com.sample.view.HomeNewsTabAdapter;

/**
 * Created by sathish-n on 17/5/16.
 */

@Reactalytics(value = {Constants.AnalyticsTrackers.GOOGLE_ANALYTICS}, screenName = "MainFragment")
public class MainFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.content_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @TrackEvent
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach ");
        if (getActivity() instanceof ToolbarSetupListener) {
            toolbarSetupListener = (ToolbarSetupListener) getActivity();
        } else {
            //throw new ClassCastException();
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        init();
        setupViewPager();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @TrackEvent
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ");
    }

    public void init() {
        toolbar.setTitle("News");
        toolbarSetupListener.setUpToolbar(toolbar);
    }

    private void setupViewPager() {
        adapter = new HomeNewsTabAdapter(getActivity().getSupportFragmentManager(), getContext());
        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                appBarLayout.setExpanded(true);
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.addOnPageChangeListener(pageChangeListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause ");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart ");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        unbinder.unbind();
    }

    @Override
    public Class<? extends BaseFragment> getName() {
        return MainFragment.class;
    }

    @BindView(R.id.cl_content)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.vp_content)
    ViewPager viewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    private ToolbarSetupListener toolbarSetupListener;
    private Unbinder unbinder;
    private ViewPager.OnPageChangeListener pageChangeListener;
    private final String TAG = MainFragment.class.getSimpleName();
    private HomeNewsTabAdapter adapter;
}
