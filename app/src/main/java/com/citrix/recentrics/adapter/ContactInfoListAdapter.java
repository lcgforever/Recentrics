package com.citrix.recentrics.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citrix.recentrics.R;
import com.citrix.recentrics.database.ContactInfo;
import com.citrix.recentrics.model.ContactModel;

import java.util.List;

public class ContactInfoListAdapter extends RecyclerView.Adapter<ContactInfoListAdapter.ContactViewHolder> {

    private static final int TYPE_COLLAPSED = 0;
    private static final int TYPE_EXPANDED = 1;

    private Context context;
    private LayoutInflater inflater;
    private List<ContactInfo> contactInfoList;
    private boolean[] expanded;
    private boolean[] listSelectedArray;
    private int selectedNum;
    private ContactInfoListEventListener listLongClickListener;
    private boolean isActionMode = false;

    public interface ContactInfoListEventListener {

        public void onListItemLongClicked(View v, int position);

        public void onListItemClicked(View v, int position);
    }

    public ContactInfoListAdapter(Context context, List<ContactInfo> contactInfoList, ContactInfoListEventListener listener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.contactInfoList = contactInfoList;
        listLongClickListener = listener;
        listSelectedArray = new boolean[contactInfoList.size()];
        selectedNum = 0;
        expanded = new boolean[contactInfoList.size()];
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ContactListViewHolderEventListener vholderEventListener =
                new ContactListViewHolderEventListener() {
                    @Override
                    public void onLongClickedEvent(View v, int position) {

                        listLongClickListener.onListItemLongClicked(v, position);
                    }

                    @Override
                    public void onClickedEvent(View v, int position) {

                        listLongClickListener.onListItemClicked(v, position);
                    }
                };
        if (viewType == TYPE_COLLAPSED) {
            View view = inflater.inflate(R.layout.contact_list_item_layout, parent, false);
            return new ContactViewHolder(view, vholderEventListener);
        } else {
            View view = inflater.inflate(R.layout.contact_list_expanded_layout, parent, false);
            return new ContactExpandedViewHolder(view, vholderEventListener);
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
        holder.setPosition(position);

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

        if (isSelected(position)) {
            holder.listItem.setBackgroundColor(Color.parseColor("#4f000000"));
        } else {
            holder.listItem.setBackgroundColor(Color.WHITE);
        }
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
        listSelectedArray = new boolean[contactInfoList.size()];
        expanded = new boolean[contactInfoList.size()];
        notifyDataSetChanged();
    }

    protected interface ContactListViewHolderEventListener {

        public void onLongClickedEvent(View v, int position);

        public void onClickedEvent(View v, int position);
    }

    protected class ContactViewHolder extends RecyclerView.ViewHolder {

        private ImageButton profileImage;
        private TextView nameText;
        private TextView emailText;
        private ImageView callImage;
        private RelativeLayout listItem;
        private int position = -1;

        public ContactViewHolder(View itemView, final ContactListViewHolderEventListener listener) {
            super(itemView);

            profileImage = (ImageButton) itemView.findViewById(R.id.profile_image);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            emailText = (TextView) itemView.findViewById(R.id.email_text);
            callImage = (ImageView) itemView.findViewById(R.id.call_image);
            listItem = (RelativeLayout) itemView.findViewById(R.id.list_item);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    if (null != listener && -1 != position) {
                        listener.onLongClickedEvent(v, position);
                        return true;
                    }
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isActionMode) {
                        expanded[position] = !expanded[position];
                        notifyItemChanged(position);
                    }

                    if (null != listener && -1 != position) {
                        listener.onClickedEvent(v, position);
                    }
                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    protected class ContactExpandedViewHolder extends ContactViewHolder {

        private TextView titleText;
        private TextView phoneText;
        private TextView officeCityText;
        private TextView officeCountryText;
        private TextView latestEmailTime;
        private TextView latestEmailContent;
        private RelativeLayout listItem;

        private int position = -1;

        public ContactExpandedViewHolder(View itemView, final ContactListViewHolderEventListener listener) {
            super(itemView, listener);

            titleText = (TextView) itemView.findViewById(R.id.title_text);
            phoneText = (TextView) itemView.findViewById(R.id.phone_text);
            officeCityText = (TextView) itemView.findViewById(R.id.office_city_text);
            officeCountryText = (TextView) itemView.findViewById(R.id.office_country_text);
            latestEmailTime = (TextView) itemView.findViewById(R.id.latest_email_time);
            latestEmailContent = (TextView) itemView.findViewById(R.id.latest_email_content);
            listItem = (RelativeLayout) itemView.findViewById(R.id.list_item);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    if (null != listener && -1 != position) {
                        listener.onLongClickedEvent(v, position);
                        return true;
                    }
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isActionMode) {
                        expanded[position] = !expanded[position];
                        notifyItemChanged(position);
                    }

                    if (null != listener && -1 != position) {
                        listener.onClickedEvent(v, position);
                    }
                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    public void setSelectedPosition(final int position) {

        if (position >= 0 && position < listSelectedArray.length) {
            listSelectedArray[position] = true;
            notifyDataSetChanged();
            ++selectedNum;
        }
    }

    public void cancelSelectedPosition(final int position) {

        if (position >= 0 && position < listSelectedArray.length) {
            listSelectedArray[position] = false;
            notifyDataSetChanged();
            --selectedNum;
        }
    }

    public boolean isSelected(final int position) {

        if (position >= 0 && position < listSelectedArray.length) {
            return listSelectedArray[position];
        }
        return false;
    }

    public void cancelAllSelection() {

        for (int i = 0; i < listSelectedArray.length; ++i) {
            listSelectedArray[i] = false;
            notifyDataSetChanged();
        }
        selectedNum = 0;
    }

    public int getSelectedNum() {

        return selectedNum;
    }

    public void deleteSelectedItems() {

        int offset = 0;
        for (int i = 0; i < listSelectedArray.length; ++i) {
            if (listSelectedArray[i]) {
                ContactModel.getInstance().removeContactInfoItem(contactInfoList.get(i - offset));
                contactInfoList.remove(i - offset);
                notifyItemRemoved(i - offset);
                ++offset;
            }
        }
    }

    public void setActionMode(boolean isActionMode) {

        this.isActionMode = isActionMode;
    }
}
