package com.example.second_exercise;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;


// ViewPager的适配器是PagerAdapter，它是基类提供适配器来填充页面ViewPager内部
// 设置pageAdapter
public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager mfragmentManager;
    private List<Fragment> mlist;

    public TabFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mlist = list;
    }

    @Override
    public Fragment getItem(int arg0) {
        return mlist.get(arg0);//显示第几个页面
    }

    @Override
    public int getCount() {
        return mlist.size();//有几个页面
    }

}
