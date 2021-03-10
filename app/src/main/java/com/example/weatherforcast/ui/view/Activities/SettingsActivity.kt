package com.example.weatherforcast.ui.view.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.weatherforcast.R


class SettingsActivity : localizeActivity() {
    public var lat: String = "0"
    public var lon: String = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.settings,
                        SettingsFragment()
                    )
                    .commit()
        }


    }

    class SettingsFragment : PreferenceFragmentCompat() , SharedPreferences.OnSharedPreferenceChangeListener{

        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var prefrenceScreen: PreferenceScreen
        private lateinit var editor: SharedPreferences.Editor

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            prefrenceScreen = preferenceScreen
            sharedPreferences =  preferenceScreen.sharedPreferences
            sharedPreferences = getDefaultSharedPreferences(context)
            editor = sharedPreferences.edit()
            //editor.putBoolean("isUpdated", false)
            //editor.commit()

            val goToLocationSettings: Preference? = findPreference("CUSTOM_LOCATION")
            if (goToLocationSettings != null) {
                goToLocationSettings.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    activity?.finish()
                    val intent: Intent = Intent(context, MapsActivity::class.java)
                    editor.putBoolean("isUpdated", true)
                    editor.commit()
                    startActivity(intent)
                    true
                }
            }

        }
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            // Figure out which preference was changed
            editor.putBoolean("isUpdated", true)
            editor.commit()

            val preference: Preference? = key?.let { findPreference(it) }
            preference?.let {
                // Updates the summary for the preference
                Log.i("ola",""+preference+"pree")
                if (preference is SwitchPreference) {
                    val value = sharedPreferences!!.getBoolean(preference.key,false)
                    Log.i("setting",""+value.toString()+"cc" +"")
                    editor.putBoolean("isUpdated", true)
                    editor.commit()

                }
                else if (preference is ListPreference) {
                    if(preference.key=="UNIT_SYSTEM") {
                        val value = sharedPreferences?.getString(preference.key, "")
                        Log.i("setting",value+"uu")

                        editor.putBoolean("isUpdated", true)
                        editor.commit()
                    }
                    else {

                        val value = sharedPreferences?.getString(preference.key, "")
                        Log.i("setting",value+"ll")
                        // activity?.let { it1 -> setLocale(it1,value) }

                    }

                }
                else{
                    if(preference.key=="CUSTOM_LOCATION"){
                        val value = sharedPreferences?.getString(preference.key, "")
                        Log.i("setting",value+"cc" +"")
                    }
                    else{}

                }
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            preferenceScreen.sharedPreferences
                    .registerOnSharedPreferenceChangeListener(this)
        }

        override fun onDestroy() {
            super.onDestroy()
            preferenceScreen.sharedPreferences
                    .unregisterOnSharedPreferenceChangeListener(this)
        }
    }
}