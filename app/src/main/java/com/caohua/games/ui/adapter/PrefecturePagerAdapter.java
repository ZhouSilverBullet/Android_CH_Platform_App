package com.caohua.games.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.caohua.games.biz.prefecture.PrefectureEntry;
import com.caohua.games.ui.fragment.FactoryFragment;
import com.caohua.games.ui.fragment.PrefectureTabFragment;
import com.culiu.mhvp.core.InnerScrollerContainer;
import com.culiu.mhvp.core.OuterPagerAdapter;
import com.culiu.mhvp.core.OuterScroller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/8.
 */

public class PrefecturePagerAdapter extends FragmentStatePagerAdapter implements OuterPagerAdapter {

    private List<String> tab_title;
    private PrefectureEntry.DataBean data;

    public PrefecturePagerAdapter(FragmentManager fm, List<String> tab_title) {
        super(fm);
        this.tab_title = tab_title;
    }

    public PrefecturePagerAdapter(FragmentManager fm, PrefectureEntry.DataBean data, ViewPager pager) {
        super(fm);
        this.data = data;
        if (tab_title == null) {
            tab_title = new ArrayList<>();
        }
        List<PrefectureEntry.DataBean.TabBannerBean> tab_banner = data.getTab_banner();
        for (int i = 0; i < tab_banner.size(); i++) {
            tab_title.add(tab_banner.get(i).getName());
        }
        pager.setOffscreenPageLimit(tab_banner.size());
        notifyDataSetChanged();
    }

    public void setTabData(PrefectureEntry.DataBean data, ViewPager pager) {
        this.data = data;
        int size = tab_title.size();
        List<String> tabs = new ArrayList<>();
        List<PrefectureEntry.DataBean.TabBannerBean> tab_banner = data.getTab_banner();
        int tabSize = tab_banner.size();
        for (int i = 0; i < tabSize; i++) {
            tabs.add(tab_banner.get(i).getName());
        }
        tab_title.clear();
        tab_title.addAll(tabs);
//        int totalSize = size - tabSize;
//        if (totalSize > 0) {
//            while (totalSize > 0) {
//                pager.removeViewAt(totalSize - 1);
//                totalSize--;
//            }
//        }
        pager.setOffscreenPageLimit(tab_banner.size());
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int position) {
        if (tab_title == null)
            return null;
        return FactoryFragment.getFragment(position, data.getTab_banner().get(position).getBanner_id());
    }

    @Override
    public int getCount() {
        return tab_title == null ? 0 : tab_title.size();
    }

    public void addAll(Collection<String> collection) {
        if (tab_title != null) {
            tab_title.clear();
            tab_title.addAll(collection);
            notifyDataSetChanged();
        }
    }

    /****
     * OuterPagerAdapter methods
     ****/
    private OuterScroller mOuterScroller;

    @Override
    public void setOuterScroller(OuterScroller outerScroller) {
        mOuterScroller = outerScroller;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO: Make sure to put codes below in your PagerAdapter's instantiateItem()
        // cuz Fragment has some weird life cycle.
        InnerScrollerContainer fragment =
                (InnerScrollerContainer) super.instantiateItem(container, position);
        ((PrefectureTabFragment) fragment).setKey(data.getTab_banner().get(position).getBanner_id());
        if (null != mOuterScroller) {
            fragment.setOuterScroller(mOuterScroller, position);
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_title.get(position);
    }
}
