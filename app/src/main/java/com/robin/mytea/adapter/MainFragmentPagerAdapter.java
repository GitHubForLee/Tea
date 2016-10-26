package com.robin.mytea.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.List;

/**
 * 主界面viewpager适配器
 * 
 *
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
	private final String TAG = "MainFragmentPagerAdapter";
	private List<Fragment> list = null;

	public MainFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.list = list;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public Object instantiateItem(View container, int position) {
		return super.instantiateItem(container, position);
	}

	@Override
	public int getCount() {
		return list.size();
	}

}
