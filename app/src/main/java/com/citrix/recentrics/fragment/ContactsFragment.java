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
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.citrix.recentrics.R;
import com.citrix.recentrics.activity.BaseApplication;
import com.citrix.recentrics.activity.MainActivity;
import com.citrix.recentrics.adapter.ContactInfoCardAdapter;
import com.citrix.recentrics.adapter.ContactInfoListAdapter;
import com.citrix.recentrics.event.DataUpdatedEvent;
import com.citrix.recentrics.event.TimeOutEvent;
import com.citrix.recentrics.model.ContactModel;
import com.citrix.recentrics.network.GetContactInfoTask;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class ContactsFragment extends Fragment implements ActionMode.Callback{

    private static final int DELAY_IN_MILLIS = 6000;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ContactInfoCardAdapter contactInfoCardAdapter;
    private ContactInfoListAdapter contactInfoListAdapter;
    private ContactModel contactModel;
    private Bus bus;
    private ContactInfoCardAdapter.ContactInfoCardLongClickListener cardLongClickListener;
    private ContactInfoListAdapter.ContactInfoListEventListener listEventListener;
    protected ActionMode mActionMode;


    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    public ContactsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactModel = ContactModel.getInstance();
        cardLongClickListener = new ContactInfoCardAdapter.ContactInfoCardLongClickListener() {
            @Override
            public void onCardLongClicked(View v, int position) {

                contactInfoCardAdapter.setSelectedPosition(position);

                if (mActionMode == null) {
                    getActivity().startActionMode(ContactsFragment.this);
                }
                mActionMode.setTitle(String.valueOf(contactInfoCardAdapter.getSelectedNum()) + " selected");
            }

            @Override
            public void onCardClicked(View v, int position) {

                if (mActionMode != null) {
                    if (contactInfoCardAdapter.isSelected(position)) {
                        contactInfoCardAdapter.cancelSelectedPosition(position);
                    } else {
                        contactInfoCardAdapter.setSelectedPosition(position);
                    }

                    mActionMode.setTitle(String.valueOf(contactInfoCardAdapter.getSelectedNum()) + " selected");
                }
            }
        };

        listEventListener = new ContactInfoListAdapter.ContactInfoListEventListener() {
            @Override
            public void onListItemLongClicked(View v, int position) {

                contactInfoListAdapter.setSelectedPosition(position);

                if (mActionMode == null) {
                    getActivity().startActionMode(ContactsFragment.this);
                }
                mActionMode.setTitle(String.valueOf(contactInfoListAdapter.getSelectedNum()) + " selected");
            }

            @Override
            public void onListItemClicked(View v, int position) {

                if (mActionMode != null) {
                    if (contactInfoListAdapter.isSelected(position)) {
                        contactInfoListAdapter.cancelSelectedPosition(position);
                    } else {
                        contactInfoListAdapter.setSelectedPosition(position);
                    }

                    mActionMode.setTitle(String.valueOf(contactInfoListAdapter.getSelectedNum()) + " selected");
                }
            }
        };

        contactInfoCardAdapter = new ContactInfoCardAdapter(getActivity(), contactModel.getContactInfoList(), cardLongClickListener);
        contactInfoListAdapter = new ContactInfoListAdapter(getActivity(), contactModel.getContactInfoList(), listEventListener);
        bus = BaseApplication.getBus();
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
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

        recyclerView.setAdapter(isCardMode() ? contactInfoCardAdapter : contactInfoListAdapter);

        return view;
    }

    private boolean isCardMode() {

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(MainActivity.PREF_VIEW_IN_CARD, false);
    }

    private void refreshContacts() {
        showRefreshProgress();
        GetContactInfoTask.getInstance().getContactInfoListByKey(1);
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
        Log.d("abc", "changeToCardView: " + ContactModel.getInstance().getContactInfoList().size());
        contactInfoCardAdapter.updateContactInfoList(ContactModel.getInstance().getContactInfoList());
        recyclerView.setAdapter(contactInfoCardAdapter);
    }

    public void changeToListView() {
        Log.d("abc", "changeToListView: "  + ContactModel.getInstance().getContactInfoList().size());
        contactInfoListAdapter.updateContactInfoList(ContactModel.getInstance().getContactInfoList());
        recyclerView.setAdapter(contactInfoListAdapter);
    }

    @Subscribe
    public void onDataUpdatedEventReceived(DataUpdatedEvent event) {
        if (recyclerView.getAdapter() instanceof ContactInfoListAdapter) {
            contactInfoListAdapter.updateContactInfoList(contactModel.getContactInfoList());
        } else {
            contactInfoCardAdapter.updateContactInfoList(contactModel.getContactInfoList());
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onTimeOutEventReceived(TimeOutEvent event) {
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

        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        mActionMode = mode;

        if (!isCardMode()) {
            contactInfoListAdapter.setActionMode(true);
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:

                if (isCardMode()) {
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

        if (isCardMode()) {
            contactInfoCardAdapter.cancelAllSelection();
        } else {
            contactInfoListAdapter.cancelAllSelection();
            contactInfoListAdapter.setActionMode(false);
        }
        mActionMode = null;
    }
}
