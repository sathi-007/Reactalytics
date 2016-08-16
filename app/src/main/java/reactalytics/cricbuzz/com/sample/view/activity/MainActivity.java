package reactalytics.cricbuzz.com.sample.view.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.cricbuzz.android.reactalytics.annotations.Reactalytics;
import com.cricbuzz.android.reactalytics.annotations.TrackEndTime;
import com.cricbuzz.android.reactalytics.annotations.TrackScreenView;
import com.cricbuzz.android.reactalytics.annotations.TrackStartTime;

import java.util.List;

import reactalytics.cricbuzz.com.lib.internal.AnnotatedInfo;
import reactalytics.cricbuzz.com.sample.R;
import reactalytics.cricbuzz.com.sample.SampleApplication;
import reactalytics.cricbuzz.com.sample.utils.Constants;
import reactalytics.cricbuzz.com.sample.utils.ToolbarSetupListener;
import reactalytics.cricbuzz.com.sample.view.fragment.MainFragment;


@Reactalytics(value = {Constants.AnalyticsTrackers.GOOGLE_ANALYTICS, Constants.AnalyticsTrackers.CUSTOM_TRACKER}, screenName = "MainActivity")
public class MainActivity extends AppCompatActivity implements ToolbarSetupListener {



    @TrackScreenView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new MainFragment())
                .commit();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                readReactlyticsFile();
                onButtonClick();
            }
        });
    }


    @TrackStartTime(id=1)
    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void setUpToolbar(Toolbar toolbar) {
        Log.i("MainActivity", "setupToolbar");
        if (toolbar == null)
            return;
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @TrackEndTime(id=1)
    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onButtonClick() {
        SampleApplication sampleApplication = (SampleApplication) getApplication();
        sampleApplication.getReactalytics().trackScreenView("MainActivity");

    }

    private final String TAG = MainActivity.class.getSimpleName();
}
