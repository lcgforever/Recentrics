package com.citrix.recentrics.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.citrix.recentrics.R;
import com.citrix.recentrics.data.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public class AttendeeListAdapter extends ArrayAdapter<ContactInfo> {

    private LayoutInflater inflater;
    private List<ContactInfo> attendeeList;
    private AttendeeClickListener attendeeClickListener;

    public AttendeeListAdapter(Context context, List<ContactInfo> attendeeList, AttendeeClickListener listener) {
        super(context, R.layout.attendee_list_item_layout, attendeeList);
        inflater = LayoutInflater.from(context);
        this.attendeeList = new ArrayList<>(attendeeList);
        attendeeClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ContactInfo contactInfo = attendeeList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.attendee_list_item_layout, parent, false);
            // configure view holder
            holder = new ViewHolder();
            holder.nameText = (TextView) convertView.findViewById(R.id.name_text);
            holder.emailText = (TextView) convertView.findViewById(R.id.email_text);
            holder.profileImage = (ImageButton) convertView.findViewById(R.id.profile_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameText.setText(contactInfo.getName());
        holder.emailText.setText(contactInfo.getEmail());
        char c = contactInfo.getName().trim().toLowerCase().charAt(0);
        if (c >= 'a' && c < 'f') {
            holder.profileImage.setBackgroundResource(R.drawable.profile_amber_background);
        } else if (c >= 'f' && c < 'l') {
            holder.profileImage.setBackgroundResource(R.drawable.profile_cyan_background);
        } else if (c >= 'l' && c < 'q') {
            holder.profileImage.setBackgroundResource(R.drawable.profile_green_background);
        } else if (c >= 'q' && c < 'u') {
            holder.profileImage.setBackgroundResource(R.drawable.profile_purple_background);
        } else {
            holder.profileImage.setBackgroundResource(R.drawable.profile_red_background);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendeeClickListener.onAttendeeClicked(contactInfo.getEmail());
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return attendeeList == null ? 0 : attendeeList.size();
    }


    private class ViewHolder {

        private ImageButton profileImage;
        private TextView nameText;
        private TextView emailText;
    }

    public interface AttendeeClickListener {

        void onAttendeeClicked(String email);
    }
}
