package com.citrix.recentrics.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrix.recentrics.R;
import com.citrix.recentrics.database.ContactInfo;

import java.util.List;

public class ContactInfoListAdapter extends RecyclerView.Adapter<ContactInfoListAdapter.ContactViewHolder> {

    private static final int TYPE_COLLAPSED = 0;
    private static final int TYPE_EXPANDED = 1;

    private Context context;
    private LayoutInflater inflater;
    private List<ContactInfo> contactInfoList;
    private boolean[] expanded;

    public ContactInfoListAdapter(Context context, List<ContactInfo> contactInfoList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.contactInfoList = contactInfoList;
        expanded = new boolean[contactInfoList.size()];
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_COLLAPSED) {
            View view = inflater.inflate(R.layout.contact_list_item_layout, parent, false);
            return new ContactViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.contact_list_expanded_layout, parent, false);
            return new ContactExpandedViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (expanded[position]) {
            return TYPE_EXPANDED;
        } else {
            return TYPE_COLLAPSED;
        }
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, final int position) {
        ContactInfo contactInfo = contactInfoList.get(position);
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

        final String phoneNumber = contactInfo.getOfficePhoneNumber();
        if (!TextUtils.isEmpty(phoneNumber)) {
            holder.callImage.setVisibility(View.VISIBLE);
            holder.callImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (phoneNumber != null) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        context.startActivity(intent);
                    }
                }
            });
        } else {
            holder.callImage.setVisibility(View.GONE);
        }

        if (holder instanceof ContactExpandedViewHolder) {
            ((ContactExpandedViewHolder) holder).titleText.setText(contactInfo.getTitle());
            ((ContactExpandedViewHolder) holder).phoneText.setText(contactInfo.getOfficePhoneNumber());
            ((ContactExpandedViewHolder) holder).officeCityText.setText(contactInfo.getOfficeCity());
            ((ContactExpandedViewHolder) holder).officeCountryText.setText(contactInfo.getOfficeCountry());
            ((ContactExpandedViewHolder) holder).latestEmailTime.setText(contactInfo.getLatestEmailTime());
            ((ContactExpandedViewHolder) holder).latestEmailContent.setText(contactInfo.getLatestEmailContent());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded[position] = !expanded[position];
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactInfoList.size();
    }

    public void updateContactInfoList(List<ContactInfo> contactInfos) {
        contactInfoList.clear();
        for (ContactInfo contactInfo : contactInfos) {
            contactInfoList.add(contactInfo);
        }
        expanded = new boolean[contactInfoList.size()];
        notifyDataSetChanged();
    }

    protected class ContactViewHolder extends RecyclerView.ViewHolder {

        private ImageButton profileImage;
        private TextView nameText;
        private TextView emailText;
        private ImageView callImage;

        public ContactViewHolder(View itemView) {
            super(itemView);

            profileImage = (ImageButton) itemView.findViewById(R.id.profile_image);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            emailText = (TextView) itemView.findViewById(R.id.email_text);
            callImage = (ImageView) itemView.findViewById(R.id.call_image);
        }
    }

    protected class ContactExpandedViewHolder extends ContactViewHolder {

        private TextView titleText;
        private TextView phoneText;
        private TextView officeCityText;
        private TextView officeCountryText;
        private TextView latestEmailTime;
        private TextView latestEmailContent;

        public ContactExpandedViewHolder(View itemView) {
            super(itemView);

            titleText = (TextView) itemView.findViewById(R.id.title_text);
            phoneText = (TextView) itemView.findViewById(R.id.phone_text);
            officeCityText = (TextView) itemView.findViewById(R.id.office_city_text);
            officeCountryText = (TextView) itemView.findViewById(R.id.office_country_text);
            latestEmailTime = (TextView) itemView.findViewById(R.id.latest_email_time);
            latestEmailContent = (TextView) itemView.findViewById(R.id.latest_email_content);
        }
    }
}
