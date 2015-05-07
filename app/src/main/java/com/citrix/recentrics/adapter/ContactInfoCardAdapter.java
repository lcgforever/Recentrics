package com.citrix.recentrics.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrix.recentrics.R;
import com.citrix.recentrics.data.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public class ContactInfoCardAdapter extends RecyclerView.Adapter<ContactInfoCardAdapter.ContactCardViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<ContactInfo> contactInfoList;

    public ContactInfoCardAdapter(Context context, List<ContactInfo> contactInfoList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.contactInfoList = new ArrayList<>(contactInfoList);
    }

    @Override
    public ContactCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.contact_card_item_layout, parent, false);
        return new ContactCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactCardViewHolder holder, int position) {
        final ContactInfo contactInfo = contactInfoList.get(position);
        holder.nameText.setText(contactInfo.getName());
        holder.titleText.setText("Title: " + contactInfo.getTitle());
        holder.phoneText.setText("Phone: " + contactInfo.getOfficePhoneNumber());
        holder.officeCityText.setText("Office: " + contactInfo.getOfficeCity() + ", " + contactInfo.getOfficeCountry());
        holder.emailTimeText.setText(contactInfo.getLatestEmailTime());
        holder.emailContentText.setText(contactInfo.getLatestEmailContent());
        holder.emailNumberText.setText(String.format(context.getString(R.string.number_of_emails_text),
                contactInfo.getNumberOfEmails()));

        char c = contactInfo.getName().trim().toLowerCase().charAt(0);
        if (c >= 'a' && c < 'f') {
            holder.nameText.setBackgroundResource(R.color.amber);
            holder.profileImage.setBackgroundResource(R.drawable.ic_profile_fox);
        } else if (c >= 'f' && c < 'l') {
            holder.nameText.setBackgroundResource(R.color.cyan);
            holder.profileImage.setBackgroundResource(R.drawable.ic_profile_lion);
        } else if (c >= 'l' && c < 'q') {
            holder.nameText.setBackgroundResource(R.color.green);
            holder.profileImage.setBackgroundResource(R.drawable.ic_profile_monkey);
        } else if (c >= 'q' && c < 'u') {
            holder.nameText.setBackgroundResource(R.color.purple);
            holder.profileImage.setBackgroundResource(R.drawable.ic_profile_panda);
        } else {
            holder.nameText.setBackgroundResource(R.color.red);
            holder.profileImage.setBackgroundResource(R.drawable.ic_profile_tiger);
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
    }

    @Override
    public int getItemCount() {
        return contactInfoList == null ? 0 : contactInfoList.size();
    }

    public void updateContactInfoList(List<ContactInfo> contactInfos) {
        contactInfoList.clear();
        for (ContactInfo contactInfo : contactInfos) {
            contactInfoList.add(contactInfo);
        }
        notifyDataSetChanged();
    }

    protected class ContactCardViewHolder extends RecyclerView.ViewHolder {

        private TextView nameText;
        private TextView titleText;
        private TextView phoneText;
        private TextView officeCityText;
        private TextView emailTimeText;
        private TextView emailContentText;
        private TextView emailNumberText;
        private ImageView profileImage;
        private ImageView callImage;
        private ImageView emailImage;
        private ImageView addContactImage;

        public ContactCardViewHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.name_text);
            titleText = (TextView) itemView.findViewById(R.id.title_text);
            phoneText = (TextView) itemView.findViewById(R.id.phone_text);
            officeCityText = (TextView) itemView.findViewById(R.id.office_city_text);
            emailTimeText = (TextView) itemView.findViewById(R.id.latest_email_time);
            emailContentText = (TextView) itemView.findViewById(R.id.latest_email_content);
            emailNumberText = (TextView) itemView.findViewById(R.id.email_number_text);
            profileImage = (ImageView) itemView.findViewById(R.id.circle_profile_image);
            callImage = (ImageView) itemView.findViewById(R.id.call_image);
            emailImage = (ImageView) itemView.findViewById(R.id.email_image);
            addContactImage = (ImageView) itemView.findViewById(R.id.add_contact_image);
        }
    }
}
