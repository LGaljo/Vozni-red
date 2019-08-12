package com.lukag.voznired.ui;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.lukag.voznired.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
    }
}
