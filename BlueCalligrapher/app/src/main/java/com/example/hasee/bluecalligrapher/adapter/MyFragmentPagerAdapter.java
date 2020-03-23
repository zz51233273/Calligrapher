package com.example.hasee.bluecalligrapher.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.hasee.bluecalligrapher.fragment.Fragment1;
import com.example.hasee.bluecalligrapher.fragment.Fragment2;
import com.example.hasee.bluecalligrapher.fragment.Fragment3;
import com.example.hasee.bluecalligrapher.fragment.Fragment4;
import com.example.hasee.bluecalligrapher.main.MainActivity;

/**
 * Created by hasee on 2018/3/30.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
    private final int PAGER_COUNT=4;
    private Fragment1 fragment1 = null;
    private Fragment2 fragment2 = null;
    private Fragment3 fragment3 = null;
    private Fragment4 fragment4 = null;

    public MyFragmentPagerAdapter(FragmentManager fm){
        super(fm);
        fragment1=new Fragment1();
        fragment2=new Fragment2();
        fragment3=new Fragment3();
        fragment4=new Fragment4();
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
