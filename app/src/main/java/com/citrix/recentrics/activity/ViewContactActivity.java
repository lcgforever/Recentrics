package com.citrix.recentrics.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.citrix.recentrics.R;
import com.citrix.recentrics.adapter.AttendeeListAdapter;
import com.citrix.recentrics.data.ContactInfo;
import com.citrix.recentrics.database.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ViewContactActivity extends AppCompatActivity implements AttendeeListAdapter.AttendeeClickListener {

    public static final String DATA_CONTACT_INFO = "DATA_CONTACT_INFO";
    public static final String LIST_CONTACT_INFO = "LIST_CONTACT_INFO";

    private ListView listView;
    private CardView cardView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.list_view);
        cardView = (CardView) findViewById(R.id.card_view);
        nameText = (TextView) findViewById(R.id.name_text);
        titleText = (TextView) findViewById(R.id.title_text);
        phoneText = (TextView) findViewById(R.id.phone_text);
        officeCityText = (TextView) findViewById(R.id.office_city_text);
        emailTimeText = (TextView) findViewById(R.id.latest_email_time);
        emailContentText = (TextView) findViewById(R.id.latest_email_content);
        emailNumberText = (TextView) findViewById(R.id.email_number_text);
        profileImage = (ImageView) findViewById(R.id.circle_profile_image);
        callImage = (ImageView) findViewById(R.id.call_image);
        emailImage = (ImageView) findViewById(R.id.email_image);
        addContactImage = (ImageView) findViewById(R.id.add_contact_image);

        Intent intent = getIntent();
        Gson gson = new Gson();
        if (intent.hasExtra(DATA_CONTACT_INFO)) {
            String json = intent.getStringExtra(DATA_CONTACT_INFO);
            ContactInfo contactInfo = gson.fromJson(json, ContactInfo.class);
            setupCard(contactInfo);
        } else {
            String json = intent.getStringExtra(LIST_CONTACT_INFO);
            Type listType = new TypeToken<List<ContactInfo>>() {
            }.getType();
            List<ContactInfo> attendeeList = gson.fromJson(json, listType);
            setupList(attendeeList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (listView.getAdapter() != null) {
                    if (listView.getVisibility() == View.VISIBLE) {
                        supportFinishAfterTransition();
                    } else {
                        showListView();
                    }
                } else {
                    supportFinishAfterTransition();
                }
                break;
        }
        return true;
    }

    @Override
    public void onAttendeeClicked(String email) {
        ContactInfo contactInfo = DatabaseManager.getInstance().getContactInfoByEmail(email);
        if (contactInfo != null && contactInfo.isInfoComplete()) {
            setupCard(contactInfo);
        } else {
            showNoInfoDialog();
        }
    }

    private void setupCard(final ContactInfo contactInfo) {
        showCardView();

        nameText.setText(contactInfo.getName());
        titleText.setText("Title: " + contactInfo.getTitle());
        phoneText.setText("Phone: " + contactInfo.getOfficePhoneNumber());
        officeCityText.setText(contactInfo.getOfficeCity() + ", " + contactInfo.getOfficeCountry());
        if (contactInfo.getNumberOfEmails() > 0) {
            emailNumberText.setText(String.format(getString(R.string.number_of_emails_text), contactInfo.getNumberOfEmails()));
            emailTimeText.setText(contactInfo.getLatestEmailTime());
            emailContentText.setText(contactInfo.getLatestEmailContent());
        } else {
            emailNumberText.setVisibility(View.GONE);
            emailTimeText.setVisibility(View.GONE);
            emailContentText.setVisibility(View.GONE);
        }
        char c = contactInfo.getName().trim().toLowerCase().charAt(0);
        if (c >= 'a' && c < 'f') {
            nameText.setBackgroundResource(R.color.amber);
            profileImage.setImageResource(R.drawable.ic_profile_fox);
        } else if (c >= 'f' && c < 'l') {
            nameText.setBackgroundResource(R.color.cyan);
            profileImage.setImageResource(R.drawable.ic_profile_lion);
        } else if (c >= 'l' && c < 'q') {
            nameText.setBackgroundResource(R.color.green);
            profileImage.setImageResource(R.drawable.ic_profile_monkey);
        } else if (c >= 'q' && c < 'u') {
            nameText.setBackgroundResource(R.color.purple);
            profileImage.setImageResource(R.drawable.ic_profile_panda);
        } else {
            nameText.setBackgroundResource(R.color.red);
            profileImage.setImageResource(R.drawable.ic_profile_tiger);
        }

        final String phoneNumber = contactInfo.getOfficePhoneNumber();
        if (!TextUtils.isEmpty(phoneNumber)) {
            callImage.setVisibility(View.VISIBLE);
            callImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                }
            });

        } else {
            callImage.setVisibility(View.GONE);
        }

        emailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", contactInfo.getEmail(), null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        addContactImage.setOnClickListener(new View.OnClickListener() {
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
                startActivity(intent);
            }
        });
    }

    private void setupList(final List<ContactInfo> attendeeList) {
        showListView();

        AttendeeListAdapter adapter = new AttendeeListAdapter(this, attendeeList, this);
        listView.setAdapter(adapter);
        listView.startLayoutAnimation();
    }

    private void showNoInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void showCardView() {
        listView.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
    }

    private void showListView() {
        cardView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }
}
