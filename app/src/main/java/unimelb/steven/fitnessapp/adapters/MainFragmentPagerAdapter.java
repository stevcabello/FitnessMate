package unimelb.steven.fitnessapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import unimelb.steven.fitnessapp.fragments.ActivityFragment;
import unimelb.steven.fitnessapp.fragments.CaloriesFragment;

/**
 * Adapter to handle the actions when moving through "tabs" (i.e. Fragments).
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    //private String tabTitles[] = new String[] { "Activity", "Map","Calories"};
    private String tabTitles[] = new String[] { "Activity", "Calories"};
    private Context context;
    private long baseId = 0;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
             return ActivityFragment.newInstance("","");
//        else if (position == 1)
//            return MapFragment.newInstance("", "");
        else
            return CaloriesFragment.newInstance("","");

    }

    //this is called when notifyDataSetChanged() is called
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        // give an ID different from position when position has been changed
        return baseId + position;
    }

    /**
     * Notify that the position of a fragment has been changed.
     * Create a new ID for each position to force recreation of the fragment
     * @param n number of items which have been changed
     */
    public void notifyChangeInPosition(int n) {
        // shift the ID returned by getItemId outside the range of all previous fragments
        baseId += getCount() + n;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
          return tabTitles[position];
    }




}
