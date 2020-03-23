package com.example.hasee.bluecalligrapher.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.hasee.bluecalligrapher.fragment.FragmentTracing1;
import com.example.hasee.bluecalligrapher.fragment.FragmentTracing2;
import com.example.hasee.bluecalligrapher.fragment.FragmentTracing3;
import com.example.hasee.bluecalligrapher.fragment.FragmentTracing4;
import com.example.hasee.bluecalligrapher.main.MainActivity;


/**
 * Created by hasee on 2018/3/30.
 */

public class MyFragmentPagerAdapterTracing extends FragmentPagerAdapter {
    private final int PAGER_COUNT=4;
    private FragmentTracing1 fragment1 = null;
    private FragmentTracing2 fragment2 = null;
    private FragmentTracing3 fragment3 = null;
    private FragmentTracing4 fragment4 = null;

    public MyFragmentPagerAdapterTracing(FragmentManager fm){
        super(fm);
        fragment1=new FragmentTracing1();
        fragment2=new FragmentTracing2();
        fragment3=new FragmentTracing3();
        fragment4=new FragmentTracing4();
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
            case MainActivity.PAGE_THREE:
                fragment = fragment3;
                break;
            case MainActivity.PAGE_FOUR:
                fragment = fragment4;
                break;
        }
        return fragment;
    }
}
