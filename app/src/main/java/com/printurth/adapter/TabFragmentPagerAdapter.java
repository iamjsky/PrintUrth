package com.printurth.adapter;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.printurth.ui.fragment.Menu1Fragment;
import com.printurth.ui.fragment.Menu2Fragment;
import com.printurth.ui.fragment.Menu3Fragment;
import com.printurth.ui.fragment.Menu4Fragment;




/**
 * Created by JSky on 2019-05-17.
 */
public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:

                return new Menu1Fragment();
            case 1:

                return  new Menu2Fragment();
            case 2:

                return new Menu3Fragment();
            case 3:
                return new Menu4Fragment();
            default:
                return null;
        }
    }
    @Override // ViewPager의 Page 수
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }
}


