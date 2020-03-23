package com.example.hasee.bluecalligrapher.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.hasee.bluecalligrapher.fragment.FragmentUserInfo1;
import com.example.hasee.bluecalligrapher.fragment.FragmentUserInfo2;
import com.example.hasee.bluecalligrapher.main.MainActivity;


/**
 * Created by hasee on 2018/6/15.
 */

public class MyFragmentAdapterUserInfo extends FragmentPagerAdapter{
    private final int PAGER_COUNT=2;
    private FragmentUserInfo1 fragment1 = null;
    private FragmentUserInfo2 fragment2 = null;

    public MyFragmentAdapterUserInfo(FragmentManager fm){
        super(fm);
        fragment1=new FragmentUserInfo1();
        fragment2=new FragmentUserInfo2();
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_ONE:
                fragment = fragment1;
                break;
            case MainActivity.PAGE_TWO:
                fragment = fragment2;
                break;
        }
        return fragment;
    }
}
