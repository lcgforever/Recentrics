package com.citrix.recentrics.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class MeetingCardAdapter extends RecyclerView.Adapter<MeetingCardAdapter.MeetingCardViewHolder> {


    @Override
    public MeetingCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MeetingCardViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    protected class MeetingCardViewHolder extends RecyclerView.ViewHolder {

        public MeetingCardViewHolder(View itemView) {
            super(itemView);
        }
    }
}
