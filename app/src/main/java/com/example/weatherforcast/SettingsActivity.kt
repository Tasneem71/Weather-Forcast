package com.example.weatherforcast

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.weatherforcast.ui.view.MapsActivity
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences



class SettingsActivity : AppCompatActivity() {
    //private lateinit var viewModel: SettingViewModel
    lateinit var listener: (SharedPreferences, String) -> Unit
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.i("ola", "createjj:")

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
//        viewModel = ViewModelProvider(
//                this,
//                ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(
//                SettingViewModel::class.java)

        if (intent.hasExtra("id")) {
            lat = intent.getDoubleExtra("lat", 0.0).toString()
            lon = intent.getDoubleExtra("lon", 0.0).toString()
            prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString("lat", (lat))
            editor.putString("lon", (lon))
            editor.apply()
            editor.commit()
            //viewModel.loadWeather(applicationContext, lat.toDouble(), lon.toDouble(), Setting.ENGLISH.Value, Setting.IMPERIAL.Value)
            Toast.makeText(
                    this, " " + lon + lat,
                    Toast.LENGTH_SHORT
            ).show()
        }
        listener = { sharedPreferences: SharedPreferences, key ->
            Log.i("ola", key + "llll:")
            if (key == "APP_LANG") {
                //Do Something
                Log.i("ola", "llll:")
            }
        }

        getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(listener)


    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            bindPreferenceSummaryToValue(findPreference("CUSTOM_LOCATION"))
            bindPreferenceSummaryToValue(findPreference("latitude"))

            val goToLocationSettings: Preference? = findPreference("CUSTOM_LOCATION")
            if (goToLocationSettings != null) {
                goToLocationSettings.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    activity?.finish()
                    val intent: Intent = Intent(context, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
            }
        }
    }

    companion object {
        private var lat: String = "0"
        private var lon: String = "0"

        private fun bindPreferenceSummaryToValue(preference: Preference?) {
            if (preference != null) {
                preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.context)
                                .getString(preference.key, ""))
            }
        }

        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            val stringValue = newValue.toString()
            Log.i("ola", "riffffffffr:")
            if (preference is EditTextPreference) {
                Log.i("ola", "ltttttt:")
                preference.text = lat
            } else {
                preference.summary = lat
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("ola", "before reg:\":")

        Log.i("ola", "after reg:\":")

    }

    override fun onPause() {
        super.onPause()
        Log.i("ola", "before unreg:")
        Log.i("ola", "after unreg:")

    }

    override fun onDestroy() {
        super.onDestroy()
        getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(listener)
        Log.i("ola", "ss:")
        // getSharedPreferences(PREFS_NAME, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(listener)
        //prefs.unregisterOnSharedPreferenceChangeListener(this);
        //Context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this)

    }
}