package com.example.collegelove;

import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                chatfreg cf=new chatfreg();
            return cf;
            case 1:
                crushfreg crf=new crushfreg();
                return crf;
            case 2:
                requestfreg rf=new requestfreg();
                return rf;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position){

        switch (position){
            case 0:
                return "CHAT";
            case 1:
                return "CRUSH";
            case 2:
                return "REQUEST";
            default:
                return null;
        }
    }
}
