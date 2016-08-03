package reactalytics.cricbuzz.com.sample.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import reactalytics.cricbuzz.com.sample.view.fragment.NewsDummyFragment;

/**
 * Created by sathish-n on 8/6/16.
 */

public class HomeNewsTabAdapter extends FragmentStatePagerAdapter {

    public HomeNewsTabAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                return NewsDummyFragment.newInstance();
//            case 1:
//                return NewsDummyFragment.newInstance();
//            case 2:
//                return NewsDummyFragment.newInstance();
//            case 3:
//                return NewsDummyFragment.newInstance();
//        }

        return NewsDummyFragment.newInstance();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "New Tab";
    }

    private final static String TAG = HomeNewsTabAdapter.class.getSimpleName();
}
