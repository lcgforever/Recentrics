package com.citrix.recentrics.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citrix.recentrics.R;
import com.citrix.recentrics.data.ContactInfo;
import com.citrix.recentrics.data.MeetingInfo;
import com.citrix.recentrics.database.DatabaseManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MeetingInfoCardAdapter extends RecyclerView.Adapter<MeetingInfoCardAdapter.MeetingCardViewHolder> {

    private LayoutInflater inflater;
    private List<MeetingInfo> meetingInfoList;
    private String transitionProfileImage;
    private String transitionMore;
    private ClickAttendeeListener clickAttendeeListener;
    private int amberColor;
    private int cyanColor;
    private int greenColor;
    private int purpleColor;
    private int redColor;

    public MeetingInfoCardAdapter(Context context, List<MeetingInfo> meetingInfoList, ClickAttendeeListener listener) {
        inflater = LayoutInflater.from(context);
        this.meetingInfoList = new ArrayList<>(meetingInfoList);
        clickAttendeeListener = listener;
        transitionProfileImage = context.getString(R.string.transition_profile_image);
        transitionMore = context.getString(R.string.transition_more);
        amberColor = context.getResources().getColor(R.color.amber);
        cyanColor = context.getResources().getColor(R.color.cyan);
        greenColor = context.getResources().getColor(R.color.green);
        purpleColor = context.getResources().getColor(R.color.purple);
        redColor = context.getResources().getColor(R.color.red);
    }

    @Override
    public MeetingCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.meeting_card_item_layout, parent, false);
        return new MeetingCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MeetingCardViewHolder holder, int position) {
        MeetingInfo meetingInfo = meetingInfoList.get(position);
        holder.subjectText.setText(meetingInfo.getSubject());
        String startTime = meetingInfo.getStartTime();
        String endTime = meetingInfo.getEndTime();
        holder.timeText.setText(startTime + " - " + endTime);
        holder.locationText.setText("Location: " + meetingInfo.getLocation());
        holder.bodyText.setText("Description: " + meetingInfo.getBody());
        holder.weatherText.setText(meetingInfo.getWeatherTemperature() + meetingInfo.getWeatherUnit());
        holder.attendeeNumText.setText(meetingInfo.getNumOfAttendees() + " attendees:");

        char c = meetingInfo.getSubject().trim().toLowerCase().charAt(0);
        if (c >= 'a' && c < 'f') {
            holder.subjectText.setBackgroundColor(amberColor);
        } else if (c >= 'f' && c < 'l') {
            holder.subjectText.setBackgroundColor(cyanColor);
        } else if (c >= 'l' && c < 'q') {
            holder.subjectText.setBackgroundColor(greenColor);
        } else if (c >= 'q' && c < 'u') {
            holder.subjectText.setBackgroundColor(purpleColor);
        } else {
            holder.subjectText.setBackgroundColor(redColor);
        }

        switch (meetingInfo.getWeatherCondition()) {
            case "sunny":
                holder.weatherImage.setImageResource(R.drawable.ic_weather_sunny);
                break;

            case "rain":
                holder.weatherImage.setImageResource(R.drawable.ic_weather_rain);
                break;
        }

        holder.attendeeLayout.removeAllViews();
        final List<ContactInfo> allAttendeeList = meetingInfo.getAttendeeList();
        for (int i = 0; i < allAttendeeList.size(); i++) {
            if (i < 4) {
                ContactInfo contactInfo = allAttendeeList.get(i);
                final String email = contactInfo.getEmail();
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(
                        R.layout.meeting_attendee_layout, holder.attendeeLayout, false);
                final ImageView imageView = (ImageView) linearLayout.findViewById(R.id.attendee_profile_image);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName(transitionProfileImage);
                }
                TextView textView = (TextView) linearLayout.findViewById(R.id.attendee_name_text);
                textView.setText(contactInfo.getName());
                char ch = contactInfo.getName().trim().toLowerCase().charAt(0);
                if (ch >= 'a' && ch < 'f') {
                    imageView.setImageResource(R.drawable.ic_profile_fox);
                } else if (ch >= 'f' && ch < 'l') {
                    imageView.setImageResource(R.drawable.ic_profile_lion);
                } else if (ch >= 'l' && ch < 'q') {
                    imageView.setImageResource(R.drawable.ic_profile_monkey);
                } else if (ch >= 'q' && ch < 'u') {
                    imageView.setImageResource(R.drawable.ic_profile_panda);
                } else {
                    imageView.setImageResource(R.drawable.ic_profile_tiger);
                }
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContactInfo newContactInfo = DatabaseManager.getInstance().getContactInfoByEmail(email);
                        if (newContactInfo != null && newContactInfo.isInfoComplete()) {
                            Gson gson = new Gson();
                            String json = gson.toJson(newContactInfo);
                            clickAttendeeListener.onAttendeeClicked(json, imageView, transitionProfileImage);
                        } else {
                            clickAttendeeListener.onAttendeeWithoutInfoClicked();
                        }
                    }
                });
                holder.attendeeLayout.addView(linearLayout);
            } else {
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(
                        R.layout.meeting_more_attendee_layout, holder.attendeeLayout, false);
                final ImageView moreAttendeeImage = (ImageView) linearLayout.findViewById(R.id.more_image);
                moreAttendeeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new Gson();
                        String json = gson.toJson(allAttendeeList);
                        clickAttendeeListener.onAttendeeClicked(json, moreAttendeeImage, transitionMore);
                    }
                });
                holder.attendeeLayout.addView(linearLayout);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return meetingInfoList == null ? 0 : meetingInfoList.size();
    }

    public void updateMeetingInfoList(List<MeetingInfo> meetingInfos) {
        meetingInfoList.clear();
        for (int i = 0; i < meetingInfos.size(); i++) {
            meetingInfoList.add(meetingInfos.get(i));
        }
        notifyDataSetChanged();
    }


    protected class MeetingCardViewHolder extends RecyclerView.ViewHolder {

        private TextView subjectText;
        private TextView timeText;
        private TextView locationText;
        private TextView bodyText;
        private TextView weatherText;
        private TextView attendeeNumText;
        private ImageView weatherImage;
        private LinearLayout attendeeLayout;

        public MeetingCardViewHolder(View itemView) {
            super(itemView);

            subjectText = (TextView) itemView.findViewById(R.id.subject_text);
            timeText = (TextView) itemView.findViewById(R.id.time_text);
            locationText = (TextView) itemView.findViewById(R.id.location_text);
            bodyText = (TextView) itemView.findViewById(R.id.body_text);
            weatherText = (TextView) itemView.findViewById(R.id.weather_text);
            attendeeNumText = (TextView) itemView.findViewById(R.id.attendee_num_text);
            weatherImage = (ImageView) itemView.findViewById(R.id.weather_image);
            attendeeLayout = (LinearLayout) itemView.findViewById(R.id.attendee_layout);
        }
    }

    public interface ClickAttendeeListener {

        void onAttendeeClicked(String json, View view, String transition);

        void onAttendeeWithoutInfoClicked();
    }
}
