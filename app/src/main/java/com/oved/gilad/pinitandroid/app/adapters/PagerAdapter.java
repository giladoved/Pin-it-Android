package com.oved.gilad.pinitandroid.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.oved.gilad.pinitandroid.app.tabs.AddTab;
import com.oved.gilad.pinitandroid.app.tabs.ListTab;
import com.oved.gilad.pinitandroid.app.tabs.MapTab;

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
                AddTab tab1 = new AddTab();
                return tab1;
            case 1:
                ListTab tab2 = new ListTab();
                return tab2;
            case 2:
                MapTab tab3 = new MapTab();
                return tab3;
            default:
                tab1 = new AddTab();
                return tab1;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
