package com.lukag.voznired.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.voznired.R;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        SwipeBackHelper.onCreate(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
    }

//    private void getAllContacts() {
//        ContentResolver contentResolver = getApplicationContext().getContentResolver();
//        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//        if (cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
//                if (hasPhoneNumber > 0) {
//                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                    Cursor phoneCursor = contentResolver.query(
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id},
//                            null);
//                    if (phoneCursor != null) {
//                        if (phoneCursor.moveToNext()) {
//                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                            String email = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
//                            //At here You can add phoneNUmber and Name to you listView ,ModelClass,Recyclerview,
//                            Log.d("temp: ", name);
//                            phoneCursor.close();
//                        }
//                    }
//                }
//            }
//        }
//        cursor.close();
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
