package com.citrix.recentrics.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.citrix.recentrics.adapter.MeetingInfoCardAdapter;
import com.citrix.recentrics.event.MeetingInfoUpdatedEvent;
import com.citrix.recentrics.event.TimeOutEvent;
import com.citrix.recentrics.model.MeetingModel;
import com.citrix.recentrics.network.GetMeetingInfoTask;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class MeetingsFragment extends Fragment implements MeetingInfoCardAdapter.ClickAttendeeListener {

    private static final int DELAY_IN_MILLIS = 11000;

    private SwipeRefreshLayout swipeRefreshLayout;
    private MeetingInfoCardAdapter meetingInfoCardAdapter;
    private MeetingModel meetingModel;
    private Bus bus;
    private StartActivityListener startActivityListener;

    public static MeetingsFragment newInstance() {
        return new MeetingsFragment();
    }

    public MeetingsFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            startActivityListener = (StartActivityListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        meetingModel = MeetingModel.getInstance();
        meetingInfoCardAdapter = new MeetingInfoCardAdapter(getActivity(), meetingModel.getMeetingInfoList(), this);
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
        View view = inflater.inflate(R.layout.fragment_meetings, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMeetings();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.cyan, R.color.amber, R.color.red);
        if (meetingModel.getMeetingInfoList().size() == 0) {
            refreshMeetings();
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(meetingInfoCardAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                swipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });

        return view;
    }

    @Override
    public void onAttendeeClicked(String json, View view, String transition) {
        startActivityListener.onStartActivity(json, view, transition);
    }

    @Override
    public void onAttendeeWithoutInfoClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("No Information Found")
                .setMessage("Sorry, we cannot find any information related with this contact.")
                .setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void refreshMeetings() {
        showRefreshProgress();
        GetMeetingInfoTask.getInstance().getMeetingInfoListByKey(2);
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
    public void onMeetingInfoUpdatedEventReceived(MeetingInfoUpdatedEvent event) {
        meetingInfoCardAdapter.updateMeetingInfoList(meetingModel.getMeetingInfoList());
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


    public interface StartActivityListener {

        void onStartActivity(String json, View view, String transition);
    }
}
