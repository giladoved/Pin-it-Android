package com.oved.gilad.pinitandroid.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.oved.gilad.pinitandroid.app.tabs.AddPin;
import com.oved.gilad.pinitandroid.app.tabs.ListView;
import com.oved.gilad.pinitandroid.app.tabs.MapView;

/**
 * Created by gilad on 7/23/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AddPin tab1 = new AddPin();
                return tab1;
            case 1:
                MapView tab2 = new MapView();
                return tab2;
            case 2:
                ListView tab3 = new ListView();
                return tab3;
            default:
                tab1 = new AddPin();
                return tab1;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
