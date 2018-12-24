package com.minhnhut.library.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.minhnhut.library.Fragments.FragmentBook;
import com.minhnhut.library.Fragments.FragmentCategory;

public class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new FragmentBook();
            case 1:
                return new FragmentCategory();
        }
        return new FragmentBook();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Sách";
            case 1:
                return "Danh mục";
        }
        return super.getPageTitle(position);
    }
}
