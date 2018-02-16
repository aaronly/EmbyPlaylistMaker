package us.echols.embyplaylistmaker.ui.settings

import android.os.Bundle
import android.preference.PreferenceFragment

import us.echols.embyplaylistmaker.R

class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }
}
