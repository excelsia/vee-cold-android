package tech.vee.veecoldwallet;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.widget.Toast;


public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_settings);
        final ListPreference listPreference = (ListPreference) findPreference("settings_language");
        // Set default language to English
        listPreference.setValueIndex(0);
    }
}

