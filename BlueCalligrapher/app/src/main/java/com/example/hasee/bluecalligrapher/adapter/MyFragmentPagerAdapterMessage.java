package com.example.hasee.bluecalligrapher.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.hasee.bluecalligrapher.fragment.FragmentSetting1;
import com.example.hasee.bluecalligrapher.fragment.FragmentSetting2;
import com.example.hasee.bluecalligrapher.fragment.FragmentSetting3;


/**
 * Created by hasee on 2018/5/29.
 */

public class MyFragmentPagerAdapterMessage extends FragmentPagerAdapter{
    private final int PAGER_COUNT=3;
    private FragmentSetting1 fragment1 = null;
    private FragmentSetting2 fragment2 = null;
    private FragmentSetting3 fragment3 = null;
    private final int PAGE_ONE = 0;
    private final int PAGE_TWO = 1;
    private final int PAGE_THREE = 2;
    public MyFragmentPagerAdapterMessage(FragmentManager fm){
        super(fm);
        fragment1=new FragmentSetting1();
        fragment2=new FragmentSetting2();
        fragment3=new FragmentSetting3();
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
        Fragment current_fragment=null;
        switch (position) {
            case PAGE_ONE:
                current_fragment = fragment1;
                break;
            case PAGE_TWO:
                current_fragment = fragment2;
                break;
            case PAGE_THREE:
                current_fragment = fragment3;
                break;
        }
        return current_fragment;
    }
}
