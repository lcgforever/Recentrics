package com.citrix.recentrics.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.citrix.recentrics.R;
import com.citrix.recentrics.activity.BaseApplication;
import com.citrix.recentrics.activity.MainActivity;
import com.citrix.recentrics.adapter.ContactInfoCardAdapter;
import com.citrix.recentrics.adapter.ContactInfoListAdapter;
import com.citrix.recentrics.event.ContactInfoUpdatedEvent;
import com.citrix.recentrics.event.TimeOutEvent;
import com.citrix.recentrics.model.ContactModel;
import com.citrix.recentrics.network.GetContactInfoTask;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class ContactsFragment extends Fragment implements ContactInfoCardAdapter.ItemClickListener {

    private static final int DELAY_IN_MILLIS = 11000;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ContactInfoCardAdapter contactInfoCardAdapter;
    private ContactInfoListAdapter contactInfoListAdapter;
    private ContactModel contactModel;
    private Bus bus;
    private ActionMode actionMode;

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    public ContactsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactModel = ContactModel.getInstance();
        contactInfoCardAdapter = new ContactInfoCardAdapter(getActivity(), contactModel.getContactInfoList(), this);
        contactInfoListAdapter = new ContactInfoListAdapter(getActivity(), contactModel.getContactInfoList(), this);
        bus = BaseApplication.getBus();
        actionMode = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelActionMode();
        bus.unregister(this);
        SnackbarManager.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContacts();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.cyan, R.color.amber, R.color.red);
        if (contactModel.getContactInfoList().size() == 0) {
            refreshContacts();
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                swipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });

        recyclerView.setAdapter(isCardViewMode() ? contactInfoCardAdapter : contactInfoListAdapter);

        return view;
    }

    @Override
    public void onItemClicked(int position) {
        if (isCardViewMode()) {
            actionMode.setTitle(contactInfoCardAdapter.getTotalSelectedNum() + " selected");
        } else {
            actionMode.setTitle(contactInfoListAdapter.getTotalSelectedNum() + " selected");
        }
    }

    @Override
    public void onItemLongClicked(int position) {
        getActivity().startActionMode(new ActionModeCallback());
    }

    private boolean isCardViewMode() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(MainActivity.PREF_VIEW_IN_CARD, false);
    }

    private void refreshContacts() {
        showRefreshProgress();
        GetContactInfoTask.getInstance().getContactInfoListByKey(MainActivity.USER_KEY);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, DELAY_IN_MILLIS);
    }

    private void showRefreshProgress() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    public void changeToCardView() {
        contactInfoCardAdapter.updateContactInfoList(ContactModel.getInstance().getContactInfoList());
        recyclerView.setAdapter(contactInfoCardAdapter);
    }

    public void changeToListView() {
        contactInfoListAdapter.updateContactInfoList(ContactModel.getInstance().getContactInfoList());
        recyclerView.setAdapter(contactInfoListAdapter);
    }

    public void cancelActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Subscribe
    public void onContactInfoUpdatedEventReceived(ContactInfoUpdatedEvent event) {
        if (recyclerView.getAdapter() instanceof ContactInfoListAdapter) {
            contactInfoListAdapter.updateContactInfoList(contactModel.getContactInfoList());
        } else {
            contactInfoCardAdapter.updateContactInfoList(contactModel.getContactInfoList());
            if (actionMode != null) {
                actionMode.finish();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onTimeOutEventReceived(TimeOutEvent event) {
        if (actionMode != null) {
            actionMode.finish();
        }
        SnackbarManager.show(
                Snackbar.with(getActivity())
                        .text(getString(R.string.snackbar_message))
                        .textColor(Color.WHITE)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                        .actionLabel(getString(R.string.snackbar_action))
                        .actionColor(getActivity().getResources().getColor(R.color.accent))
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                SnackbarManager.dismiss();
                            }
                        }));
    }


    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getActivity().getMenuInflater().inflate(R.menu.action_menu, menu);
            actionMode = mode;
            actionMode.setTitle("1 selected");
            if (isCardViewMode()) {
                contactInfoCardAdapter.setInActionMode(true);
            } else {
                contactInfoListAdapter.setInActionMode(true);
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    if (isCardViewMode()) {
                        contactInfoCardAdapter.deleteSelectedItems();
                    } else {
                        contactInfoListAdapter.deleteSelectedItems();
                    }

                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (isCardViewMode()) {
                contactInfoCardAdapter.cancelAllSelection();
                contactInfoCardAdapter.setInActionMode(false);
            } else {
                contactInfoListAdapter.cancelAllSelection();
                contactInfoListAdapter.setInActionMode(false);
            }
            actionMode = null;
        }
    }
}
