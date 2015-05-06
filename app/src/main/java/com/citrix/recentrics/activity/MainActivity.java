package com.citrix.recentrics.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.citrix.recentrics.R;
import com.citrix.recentrics.database.DatabaseManager;
import com.citrix.recentrics.fragment.ContactsFragment;
import com.citrix.recentrics.fragment.MeetingsFragment;
import com.citrix.recentrics.fragment.TravelFragment;
import com.citrix.recentrics.library.SlidingTabLayout;

public class MainActivity extends AppCompatActivity implements ActionMenuView.OnMenuItemClickListener {

    private static final int TOTAL_TAB_COUNT = 1;

    private Toolbar toolbar;
    private ActionMenuView menuView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseManager.init(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        menuView = (ActionMenuView) findViewById(R.id.menu_view);
        menuView.setOnMenuItemClickListener(this);

        SlidingTabLayout tabLayout = (SlidingTabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        Fragment[] fragments = {ContactsFragment.newInstance(), MeetingsFragment.newInstance(), TravelFragment.newInstance()};
        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(tabsAdapter);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setViewPager(viewPager);
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Menu actionMenu = menuView.getMenu();
        getMenuInflater().inflate(R.menu.menu_main, actionMenu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }


    private class TabsAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments;

        public TabsAdapter(FragmentManager fragmentManager, Fragment[] fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return TOTAL_TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.contacts_fragment_name);

                case 1:
                    return getString(R.string.meetings_fragment_name);

                case 2:
                default:
                    return getString(R.string.travel_fragment_name);
            }
        }
    }
}
