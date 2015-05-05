package com.citrix.recentrics.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citrix.recentrics.R;
import com.citrix.recentrics.activity.BaseApplication;
import com.citrix.recentrics.adapter.ContactInfoAdapter;
import com.citrix.recentrics.event.DataUpdatedEvent;
import com.citrix.recentrics.event.TimeOutEvent;
import com.citrix.recentrics.model.ContactModel;
import com.citrix.recentrics.network.GetContactInfoTask;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class ContactsFragment extends Fragment {

    private static final int DELAY_IN_MILLIS = 6000;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ContactInfoAdapter contactInfoAdapter;
    private ContactModel contactModel;
    private Bus bus;

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    public ContactsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactModel = ContactModel.getInstance();
        contactInfoAdapter = new ContactInfoAdapter(getActivity(), contactModel.getContactInfoList());
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
        recyclerView.setAdapter(contactInfoAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                swipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });

        return view;
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

    @Subscribe
    public void onDataUpdatedEventReceived(DataUpdatedEvent event) {
        contactInfoAdapter.updateContactInfoList(contactModel.getContactInfoList());
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
    }
}
