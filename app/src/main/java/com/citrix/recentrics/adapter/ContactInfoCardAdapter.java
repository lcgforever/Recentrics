package com.citrix.recentrics.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citrix.recentrics.R;
import com.citrix.recentrics.database.ContactInfo;
import com.citrix.recentrics.model.ContactModel;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ContactInfoCardAdapter extends RecyclerView.Adapter<ContactInfoCardAdapter.ContactCardViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<ContactInfo> contactInfoList;
    private boolean[] cardSelectedArray;
    private int selectedNum;
    private ContactInfoCardLongClickListener cardLongClickListener;

    public interface ContactInfoCardLongClickListener {

        public void onCardLongClicked(View v, int position);

        public void onCardClicked(View v, int position);
    }

    public ContactInfoCardAdapter(Context context, List<ContactInfo> contactInfoList, ContactInfoCardLongClickListener listener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.contactInfoList = contactInfoList;
        cardLongClickListener = listener;
        cardSelectedArray = new boolean[contactInfoList.size()];
        selectedNum = 0;
    }

    @Override
    public ContactCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.contact_card_item_layout, parent, false);
        parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        ContactCardViewHolderLongClickListener vholderLongClickListener =
                new ContactCardViewHolderLongClickListener() {
                    @Override
                    public void onLongClickedEvent(View v, int position) {


                        cardLongClickListener.onCardLongClicked(v, position);
                    }

                    @Override
                    public void onClickedEvent(View v, int position) {

                        cardLongClickListener.onCardClicked(v, position);
                    }
                };

        return new ContactCardViewHolder(view, vholderLongClickListener);
    }

    @Override
    public void onBindViewHolder(ContactCardViewHolder holder, int position) {
        final ContactInfo contactInfo = contactInfoList.get(position);
        holder.nameText.setText(contactInfo.getName());
        holder.titleText.setText(contactInfo.getTitle());
        holder.phoneText.setText(contactInfo.getOfficePhoneNumber());
        holder.officeCityText.setText(contactInfo.getOfficeCity());
        holder.emailTimeText.setText(contactInfo.getLatestEmailTime());
        holder.emailContentText.setText(contactInfo.getLatestEmailContent());
        holder.setPosition(position);

        char c = contactInfo.getName().trim().toLowerCase().charAt(0);
        if (c >= 'a' && c < 'f') {
            holder.nameText.setBackgroundResource(R.color.amber);
            holder.circleProfileImage.setBackgroundResource(R.drawable.profile_fox);
        } else if (c >= 'f' && c < 'l') {
            holder.nameText.setBackgroundResource(R.color.cyan);
            holder.circleProfileImage.setBackgroundResource(R.drawable.profile_lion);
        } else if (c >= 'l' && c < 'q') {
            holder.nameText.setBackgroundResource(R.color.green);
            holder.circleProfileImage.setBackgroundResource(R.drawable.profile_monkey);
        } else if (c >= 'q' && c < 'u') {
            holder.nameText.setBackgroundResource(R.color.purple);
            holder.circleProfileImage.setBackgroundResource(R.drawable.profile_panda);
        } else {
            holder.nameText.setBackgroundResource(R.color.red);
            holder.circleProfileImage.setBackgroundResource(R.drawable.profile_tiger);
        }

        final String phoneNumber = contactInfo.getOfficePhoneNumber();
        if (!TextUtils.isEmpty(phoneNumber)) {
            holder.callImage.setVisibility(View.VISIBLE);
            holder.callImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    context.startActivity(intent);
                }
            });

        } else {
            holder.callImage.setVisibility(View.GONE);
        }

        holder.emailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", contactInfo.getEmail(), null));
                context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        holder.addContactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
                        Uri.parse("tel:" + phoneNumber));
                intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, contactInfo.getName());
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, contactInfo.getOfficePhoneNumber());
                intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contactInfo.getEmail());
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
                intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contactInfo.getTitle());
                context.startActivity(intent);
            }
        });

        if (isSelected(position)) {
            holder.selectedView.setVisibility(View.VISIBLE);
        } else {
            holder.selectedView.setVisibility(View.GONE);
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
        cardSelectedArray = new boolean[contactInfoList.size()];
        selectedNum = 0;
        notifyDataSetChanged();
    }


    interface ContactCardViewHolderLongClickListener {

        public void onLongClickedEvent(View v, int position);

        public void onClickedEvent(View v, int position);
    }

    protected class ContactCardViewHolder extends RecyclerView.ViewHolder {

        private TextView nameText;
        private TextView titleText;
        private TextView phoneText;
        private TextView officeCityText;
        private TextView emailTimeText;
        private TextView emailContentText;
        private RoundedImageView circleProfileImage;
        private ImageView callImage;
        private ImageView emailImage;
        private ImageView addContactImage;
        private LinearLayout selectedView;

        private int position = -1;

        public ContactCardViewHolder(View itemView, final ContactCardViewHolderLongClickListener listener) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.name_text);
            titleText = (TextView) itemView.findViewById(R.id.title_text);
            phoneText = (TextView) itemView.findViewById(R.id.phone_text);
            officeCityText = (TextView) itemView.findViewById(R.id.office_city_text);
            emailTimeText = (TextView) itemView.findViewById(R.id.latest_email_time);
            emailContentText = (TextView) itemView.findViewById(R.id.latest_email_content);
            circleProfileImage = (RoundedImageView) itemView.findViewById(R.id.circle_profile_image);
            callImage = (ImageView) itemView.findViewById(R.id.call_image);
            emailImage = (ImageView) itemView.findViewById(R.id.email_image);
            addContactImage = (ImageView) itemView.findViewById(R.id.add_contact_image);
            selectedView = (LinearLayout) itemView.findViewById(R.id.selected_view);

            FrameLayout clickableCard = (FrameLayout) itemView.findViewById(R.id.clickable_card);

            clickableCard.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    if (null != listener && -1 != position) {
                        listener.onLongClickedEvent(v, position);
                        return true;
                    }
                    return false;
                }
            });

            clickableCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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

        if (position >= 0 && position < cardSelectedArray.length) {
            cardSelectedArray[position] = true;
            notifyDataSetChanged();
            ++ selectedNum;
        }
    }

    public void cancelSelectedPosition(final int position) {

        if (position >= 0 && position < cardSelectedArray.length) {
            cardSelectedArray[position] = false;
            notifyDataSetChanged();
            -- selectedNum;
        }
    }

    public boolean isSelected(final int position) {

        if (position >= 0 && position < cardSelectedArray.length) {
            return cardSelectedArray[position];
        }
        return false;
    }

    public void cancelAllSelection() {

        for (int i = 0; i < cardSelectedArray.length; ++i) {
            cardSelectedArray[i] = false;
            notifyDataSetChanged();
        }
        selectedNum = 0;
    }

    public int getSelectedNum() {

        return selectedNum;
    }

    public void deleteSelectedItems() {

        int offset = 0;
        for (int i=0; i<cardSelectedArray.length; ++i) {
            if (cardSelectedArray[i]) {
                ContactModel.getInstance().removeContactInfoItem(contactInfoList.get(i - offset));
                contactInfoList.remove(i - offset);
                notifyItemRemoved(i - offset);
                ++ offset;
            }
        }
    }
}
