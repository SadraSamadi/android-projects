package ir.remmargorp.bluetoothcontrol.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import ir.remmargorp.bluetoothcontrol.R;
import ir.remmargorp.bluetoothcontrol.activities.SettingsActivity;

public class AnalogPrefFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_analog_control);
        setHasOptionsMenu(true);
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("analog_up_key"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("analog_up_right_key"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("analog_right_key"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("analog_down_right_key"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("analog_down_key"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("analog_down_left_key"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("analog_left_key"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("analog_up_left_key"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
