package com.sobot.demo.tab;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

public class SobotViewPagerAdapter extends SobotTabFragmentPagerAdapter {
    private String[] tabs;
    private List<Fragment> pagers;
    private Context context;

    public SobotViewPagerAdapter(Context context, FragmentManager fm, String[] tabs,
                                 List<Fragment> pagers) {
        super(fm);
        this.tabs = tabs;
        this.pagers = pagers;
        this.context = context;
    }

    /**
     * 返回每一页需要的fragment
     */
    @Override
    public Fragment getItem(int position) {
        return pagers.get(position);
    }

    @Override
    public int getCount() {
        return pagers.size();
    }

    /**
     * 返回每一页对应的title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if (tabs != null && position < tabs.length) {
            return tabs[position];
        }
        return "";
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
    }
}