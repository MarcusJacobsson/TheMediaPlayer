package dv106.lnu.themediaplayer.screenslide;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
	private ArrayList<Fragment> fragments;

	public ScreenSlidePagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}

    @Override
    public CharSequence getPageTitle(int position) {
        super.getPageTitle(position);
        String title = "";
        switch (position){
            case 0:
                title = "playlist";
            break;

            case 1:
                title = "music";
            break;

            case 2:
                title = "video";
            break;
        }
        return title;
    }
}
