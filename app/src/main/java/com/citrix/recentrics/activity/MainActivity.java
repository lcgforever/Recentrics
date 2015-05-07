package com.citrix.recentrics.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.citrix.recentrics.R;
import com.citrix.recentrics.database.DatabaseManager;
import com.citrix.recentrics.fragment.ContactsFragment;
import com.citrix.recentrics.fragment.MeetingsFragment;
import com.citrix.recentrics.fragment.TravelFragment;
import com.citrix.recentrics.library.SlidingTabLayout;

public class MainActivity extends AppCompatActivity implements ActionMenuView.OnMenuItemClickListener,
        MeetingsFragment.StartActivityListener {

    public static final String PREF_VIEW_IN_CARD = "PREF_VIEW_IN_CARD";
    public static final int USER_KEY = 2;
    private static final int TOTAL_TAB_COUNT = 3;

    private Toolbar toolbar;
    private ActionMenuView menuView;
    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private MenuItem changeViewItem;
    private SharedPreferences preferences;

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
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(tabsAdapter);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setViewPager(viewPager);
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        tabLayout.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (changeViewItem != null) {
                    changeViewItem.setVisible(position == 0);
                }
            }
        });

        preferences = getPreferences(MODE_PRIVATE);
        if (!preferences.contains(PREF_VIEW_IN_CARD)) {
            preferences.edit().putBoolean(PREF_VIEW_IN_CARD, false).apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Menu actionMenu = menuView.getMenu();
        getMenuInflater().inflate(R.menu.menu_main, actionMenu);
        changeViewItem = actionMenu.findItem(R.id.action_change_view);
        boolean viewInCard = preferences.getBoolean(PREF_VIEW_IN_CARD, false);
        if (viewInCard) {
            changeViewItem.setTitle(getString(R.string.action_list_view));
        } else {
            changeViewItem.setTitle(getString(R.string.action_card_view));
        }
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_view:
                boolean viewInCard = preferences.getBoolean(PREF_VIEW_IN_CARD, false);
                if (!viewInCard) {
                    changeViewItem.setTitle(getString(R.string.action_list_view));
                    ((ContactsFragment) tabsAdapter.getItem(0)).changeToCardView();
                    preferences.edit().putBoolean(PREF_VIEW_IN_CARD, true).apply();
                } else {
                    changeViewItem.setTitle(getString(R.string.action_card_view));
                    ((ContactsFragment) tabsAdapter.getItem(0)).changeToListView();
                    preferences.edit().putBoolean(PREF_VIEW_IN_CARD, false).apply();
                }
                break;

            case R.id.action_settings:
                break;
        }
        return true;
    }

    @Override
    public void onStartActivity(String json, View view, String transition) {
        Intent intent = new Intent(this, ViewContactActivity.class);
        if (transition.equals(getString(R.string.transition_more))) {
            intent.putExtra(ViewContactActivity.LIST_CONTACT_INFO, json);
        } else {
            intent.putExtra(ViewContactActivity.DATA_CONTACT_INFO, json);
        }
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, new Pair<>(view, transition),
                new Pair<View, String>(toolbar, getString(R.string.transition_toolbar)));
        startActivity(intent, optionsCompat.toBundle());
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
