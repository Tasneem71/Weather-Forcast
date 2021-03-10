package com.example.weatherforcast.ui.view.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.example.weatherforcast.R
import com.example.weatherforcast.data.entity.Alerts
import com.example.weatherforcast.data.entity.ApiObj
import com.example.weatherforcast.data.retro.SettingsEnum
import com.example.weatherforcast.databinding.ActivityScrollingBinding
import com.example.weatherforcast.ui.view.Adapters.DayAdapter
import com.example.weatherforcast.ui.view.Adapters.HourAbapter
import com.example.weatherforcast.ui.viewModel.ScrollingActivityVM
import com.example.weatherforcast.utils.NotificationUtils
import com.example.weatherforcast.utils.WorkerApi
import com.google.android.gms.location.*
import com.stephentuso.welcome.WelcomeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class ScrollingActivity : localizeActivity(){
    lateinit var notificationUtils: NotificationUtils
    var yourLocationLat:Double=0.0
    var yourLocationLon:Double=0.0
    lateinit var binding:ActivityScrollingBinding
    private lateinit var scrollingActivityViewModal: ScrollingActivityVM
    var dailyListAdapter =DayAdapter(arrayListOf(),this)
    var hourlyListAdapter =HourAbapter(arrayListOf())
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var prefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var lat: String=""
    var timezone: String=""
    var lon: String=""
    var loc:Boolean=true
    var isUpdated:Boolean=false
    var lang:String=""
    var unit:String=""
    lateinit var welcomeScreen: WelcomeHelper

    var handler = Handler(Handler.Callback {
        Toast.makeText(applicationContext,"location:"+yourLocationLat+","+yourLocationLon,Toast.LENGTH_SHORT).show()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor.putString("lat", yourLocationLat.toString())
        editor.putString("lon", yourLocationLon.toString())
        editor.commit()
        lon=prefs.getString("lon", "").toString()
        lat= prefs.getString("lat", "").toString()
        observeViewModel(scrollingActivityViewModal)
        true
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        welcomeScreen = WelcomeHelper(this, WelcomeScreenActivity::class.java)
        welcomeScreen.show(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        editor= prefs.edit()

        whiteList()

        workerInit()

        scrollingActivityViewModal = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(ScrollingActivityVM::class.java)
         findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).title = " "
         initUI()

        loc=prefs.getBoolean("USE_DEVICE_LOCATION", true)
        unit=prefs.getString("UNIT_SYSTEM", SettingsEnum.IMPERIAL.Value).toString()
        lang=prefs.getString("APP_LANG", SettingsEnum.ENGLISH.Value).toString()
        lon=prefs.getString("lon", ("")).toString()
        lat=prefs.getString("lat", ("")).toString()
        timezone=prefs.getString("timezone", ("")).toString()
        if(!timezone.isNullOrEmpty()) {
            getObjByTimezone()
        }

        if(loc){
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            getLastLocation()
        }
        else{
            observeViewModel(scrollingActivityViewModal)
        }

        binding.menu.setOnClickListener {
            showPopupMenu(it)

        }

        binding.addFav.setOnClickListener {
            val intent: Intent = Intent(applicationContext, FavoritesActivity::class.java)
            startActivity(intent)
        }

//        val findLocation= GPSUtils()
//        findLocation.findDeviceLocation(this)
//        Toast.makeText(this,"location:"+findLocation.getLatitude()+","+findLocation.getLongitude(),Toast.LENGTH_SHORT).show()

    }

    private fun workerInit() {
        val saveRequest = PeriodicWorkRequest.Builder(WorkerApi::class.java,15, TimeUnit.MINUTES).addTag("up").build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("up", ExistingPeriodicWorkPolicy.REPLACE,saveRequest)

    }

    private fun whiteList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        welcomeScreen.onSaveInstanceState(outState);
    }

    private fun initUI() {
        binding.iContent.hourList.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = hourlyListAdapter

        }
        binding.iContent.dayList.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = dailyListAdapter
        }
    }

    private fun observeViewModel(viewModel: ScrollingActivityVM) {
        //viewModel.loadingLiveData.observe(this, { showLoading(it) })
        // viewModel.errorLiveData.observe(this, { showError(it) })
//        var loc=prefs.getBoolean("USE_DEVICE_LOCATION", false).toString()
//        var unit=prefs.getString("UNIT_SYSTEM", SettingsEnum.IMPERIAL.Value)
//        var lang= prefs.getString("APP_LANG", SettingsEnum.ENGLISH.Value)
//        var lon=java.lang.Double.longBitsToDouble(prefs.getLong("lon", java.lang.Double.doubleToRawLongBits(0.0)))
//        var lat=java.lang.Double.longBitsToDouble(prefs.getLong("lat", java.lang.Double.doubleToRawLongBits(0.0)))
        Log.i("ola", "location:" + lang + "," + unit + " " + loc + " " + lat + " " + lon)
        viewModel.loadWeatherObj(applicationContext, lat.toDouble(), lon.toDouble(), lang, unit).observe(this, { updateCurrent(it) })

    }

    private fun notifyUser(alert:List<Alerts>){
        notificationUtils = NotificationUtils(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nb: NotificationCompat.Builder? = notificationUtils.getAndroidChannelNotification(alert.get(0)?.event, ""
                    +scrollingActivityViewModal.dateFormat(alert.get(0)?.start.toInt())+","+scrollingActivityViewModal.dateFormat(alert.get(0)?.end.toInt()) +"\n"+alert.get(0)?.description,true)
            notificationUtils.getManager()?.notify(3, nb?.build())
    }
    }

    private fun updateCurrent(item: ApiObj) {

        item?.let {
                item.apply {
                    binding.temp.text= current.temp.toInt().toString()+"°"
                    binding.describtion.text= current.weather.get(0).description.toString()
                    binding.titletv.text= timezone
                    //CoroutineScope(Dispatchers.Main).launch { Picasso.get().load(iconLinkgetter(current.weather.get(0).icon)).into(binding.currentIcon) }

                    CoroutineScope(Dispatchers.Main).launch{
                        Glide.with(binding.currentIcon).
                        load(iconLinkgetter(current.weather.get(0).icon)).
                        placeholder(R.drawable.ic_baseline_wb_sunny_24).into(binding.currentIcon)
                    }



                    binding.iContent.humidityTv.text=current.humidity.toString()
                    binding.iContent.wendTv.text=current.wind_speed.toString()
                    binding.iContent.pressureTv.text=current.pressure.toString()
                    binding.iContent.cloudTv.text=current.clouds.toString()
                    binding.iContent.Date.text= scrollingActivityViewModal.dateFormat(current.dt)
                    binding.iContent.Time.text= scrollingActivityViewModal.timeFormat(current.dt)
                    binding.iContent.feels.text= current.feels_like.toString()
                    dailyListAdapter.updateDays(daily)
                    hourlyListAdapter.updateHours(hourly)
                }

            item.alerts?.let {
                notifyUser(it)
            }

            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString("timezone", item.timezone)
            editor.commit()

        }

    }

    fun iconLinkgetter(iconName:String):String="https://openweathermap.org/img/wn/"+iconName+"@2x.png"


///////////////////////////////////////////////////////////////////////////////////////////////////

    fun showPopupMenu(view: View) = PopupMenu(view.context, view).run {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {

                    val intent: Intent = Intent(applicationContext, SettingsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.action_Alerts -> {
                    val intent: Intent = Intent(applicationContext,
                        AlertActivity::class.java)
                    startActivity(intent)
                    true}
                else -> super.onOptionsItemSelected(item)
            }
            Toast.makeText(view.context, "You Clicked : ${item.title}", Toast.LENGTH_SHORT).show()
            true
        }
        show()
    }


///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        //TODO
                        yourLocationLat=location.latitude
                        yourLocationLon=location.longitude
                        handler.sendEmptyMessage(0)
                        Toast.makeText(this,"location:"+yourLocationLat+","+yourLocationLon,Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            //TODO
            yourLocationLat=mLastLocation.latitude
            yourLocationLon=mLastLocation.longitude
            handler.sendEmptyMessage(0)
            Toast.makeText(applicationContext,"location:"+yourLocationLat+","+yourLocationLon,Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////


    override fun onResume() {
        super.onResume()
        //scrollingActivityViewModal.backgroundBasedOnTime(binding.rootlay,this)
        isUpdated=prefs.getBoolean("isUpdated", false)
        Log.i("ola"," "+isUpdated.toString()+"resume")
        if(isUpdated)
        {

            loc=prefs.getBoolean("USE_DEVICE_LOCATION", true)
            unit=prefs.getString("UNIT_SYSTEM", SettingsEnum.IMPERIAL.Value).toString()
            lang=prefs.getString("APP_LANG", SettingsEnum.ENGLISH.Value).toString()
            Log.i("ola"," "+unit+"resume")
            lon=prefs.getString("lon", ("")).toString()
            lat=prefs.getString("lat", ("")).toString()
            //setLocale(this,lang)
            if(loc){
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                getLastLocation()
            }
            else{
                scrollingActivityViewModal.loadWeatherObj(applicationContext,lat.toDouble(),lon.toDouble(),lang,unit)
            }
            Log.i("onR", "before res:" + lang + "," + unit + " " + loc + " " + lat + " " + lon)
            scrollingActivityViewModal.updateAllData(applicationContext,lang,unit)
            Log.i("onR", "after  res:" + lang + "," + unit + " " + loc + " " + lat + " " + lon)
            editor.putBoolean("isUpdated", false)
            editor.commit()

        }

    }
    override fun onPause() {
        super.onPause()
        editor.putBoolean("isUpdated", false)
        editor.commit()
    }



    private fun getObjByTimezone() {
        CoroutineScope(Dispatchers.IO).launch {
            var weather= scrollingActivityViewModal.getApiObjFromRoom(timezone)
            updateCurrent(weather)
        }
    }


}