package com.sobot.chat.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.sobot.chat.fragment.SobotBaseFragment;

import java.util.List;

public class StViewPagerAdapter extends SmartFragmentStatePagerAdapter {
    private String[] tabs;
    private List<SobotBaseFragment> pagers;
    private Context context;

    public StViewPagerAdapter(Context context, FragmentManager fm, String[] tabs,
                              List<SobotBaseFragment> pagers) {
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

    /*@Override
    public void finishUpdate(ViewGroup container) {
        try {
            super.finishUpdate(container);
        } catch (NullPointerException nullPointerException) {
            Log.d(StViewPagerAdapter.class.getSimpleName(), "Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
        }
    }*/

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}