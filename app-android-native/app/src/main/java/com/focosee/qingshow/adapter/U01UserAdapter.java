package com.focosee.qingshow.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.focosee.qingshow.activity.fragment.U01FavoriteFragment;
import com.focosee.qingshow.activity.fragment.U01RecommendFragment;

/**
 * Created by Administrator on 2015/4/29.
 */
public class U01UserAdapter extends FragmentPagerAdapter {

    public U01UserAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new U01RecommendFragment();

            case 1:
                return new U01FavoriteFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
