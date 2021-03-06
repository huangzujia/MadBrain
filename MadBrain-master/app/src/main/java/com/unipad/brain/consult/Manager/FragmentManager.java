package com.unipad.brain.consult.manager;


import com.unipad.brain.consult.dao.ConsultFragmentFactory;
import com.unipad.brain.consult.entity.ConsultTab;
import com.unipad.common.BaseFragment;
import android.support.v4.app.Fragment;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by 63 on 2016/6/20.
 */
public class FragmentManager {
    public static HashMap<String, SoftReference<Fragment>> maps;
    private static boolean needCache = true;

    public static BaseFragment getFragment(ConsultTab consultTab){
        if(!needCache){
            return ConsultFragmentFactory.getInstance().getFragment(consultTab);
        }
        if(maps == null){
            maps = new HashMap<>();
        }
       SoftReference<Fragment> softReference = maps.get(consultTab.getType());
        if(softReference == null){
            BaseFragment fragment = creatNewFragment(consultTab);
            return  (BaseFragment)fragment;
        }else{
            Fragment fragment = softReference.get();
            if(fragment == null){
                return creatNewFragment(consultTab);
            }else{
                return (BaseFragment)fragment;
            }
        }
    }

    private static BaseFragment creatNewFragment(ConsultTab consultTab) {
        Fragment fragment;
        SoftReference<Fragment> softReference;
        fragment = ConsultFragmentFactory.getInstance().getFragment(consultTab);
        softReference = new SoftReference<Fragment>(fragment);
        maps.put(consultTab.getType(), softReference);
        return  (BaseFragment)fragment;
    }
}
