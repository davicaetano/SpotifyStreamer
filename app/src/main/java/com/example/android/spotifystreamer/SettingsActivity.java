package com.example.android.spotifystreamer;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.neovisionaries.i18n.CountryCode;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //It was used the neovisionaries library for country codes.
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        ListPreference listLocation = (ListPreference)findPreference("location");
        List<String> countryList = new ArrayList<>();
        List<String> countryListCode = new ArrayList<>();
        for(CountryCode countryCode :CountryCode.values()){
            countryList.add(countryCode.getName());
            countryListCode.add(countryCode.getAlpha2());
        }
        CharSequence[] entries = countryList.toArray(new CharSequence[countryList.size()]);
        CharSequence[] entryValues = countryListCode.toArray(new CharSequence[countryListCode.size()]);
        listLocation.setEntries(entries);
        listLocation.setEntryValues(entryValues);
        bindPreferenceSummaryToValue(findPreference("location"));
        bindPreferenceSummaryToValue(findPreference("notification"));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        if(preference instanceof ListPreference) {
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }else{
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }
}
