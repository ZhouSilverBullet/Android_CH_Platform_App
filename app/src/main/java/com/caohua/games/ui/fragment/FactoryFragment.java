package com.caohua.games.ui.fragment;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouzhou on 2017/5/8.
 */

public class FactoryFragment {
    public static Map<Integer, Fragment> map = new HashMap<>();

    public static Fragment getFragment(int i, String bannerId) {
        Fragment fragment = map.get(i);
        if (fragment == null) {
            fragment = PrefectureTabFragment.newInstance(i);
            map.put(i, fragment);
        }
        return fragment;
    }

}
