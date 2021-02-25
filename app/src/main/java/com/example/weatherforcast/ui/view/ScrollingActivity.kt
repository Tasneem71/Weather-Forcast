package com.example.weatherforcast.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforcast.R
import com.example.weatherforcast.data.entity.Alerts
import com.example.weatherforcast.data.entity.ApiObj
import com.example.weatherforcast.databinding.ActivityScrollingBinding
import com.example.weatherforcast.ui.viewModel.ScrollingActivityVM
import com.example.weatherforcast.utils.NotificationUtils
import com.google.android.gms.location.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ScrollingActivity : AppCompatActivity() {
    lateinit var notificationUtils: NotificationUtils
    var yourLocationLat:Double=0.0
    var yourLocationLon:Double=0.0
    lateinit var binding:ActivityScrollingBinding
    private lateinit var scrollingActivityViewModal: ScrollingActivityVM
    var dailyListAdapter = DayAdapter(arrayListOf())
    var hourlyListAdapter = HourAbapter(arrayListOf())
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    var handler = Handler(Handler.Callback {
        Toast.makeText(applicationContext,"location:"+yourLocationLat+","+yourLocationLon,Toast.LENGTH_SHORT).show()
        observeViewModel(scrollingActivityViewModal)
        true
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val timeZone=intent.getStringExtra("timeZone")
        if(!timeZone.isNullOrEmpty()){
            Toast.makeText(this,"you have arrived"+ timeZone,Toast.LENGTH_SHORT).show()

        }
        else{

            scrollingActivityViewModal = ViewModelProvider(
                    this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(ScrollingActivityVM::class.java)
            findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = " "
            initUI()


        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
//        val findLocation= GPSUtils()
//        findLocation.findDeviceLocation(this)
//        Toast.makeText(this,"location:"+findLocation.getLatitude()+","+findLocation.getLongitude(),Toast.LENGTH_SHORT).show()

    }

    private fun initUI() {
        binding.iContent.hourList.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = hourlyListAdapter

        }
        binding.iContent.dayList.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = dailyListAdapter
        }
    }

    private fun observeViewModel(viewModel: ScrollingActivityVM) {
        //viewModel.loadingLiveData.observe(this, { showLoading(it) })
        // viewModel.errorLiveData.observe(this, { showError(it) })
        viewModel.loadWeather(applicationContext,-24.7847,-65.4315).observe(this, { updateList(it) })

    }
    private fun dateFormat( milliSeconds:Int):String{
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds.toLong()*1000)
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        var day=calendar.get(Calendar.DAY_OF_MONTH).toString()
        var year=calendar.get(Calendar.YEAR).toString()
        return day+month +year

    }
    private fun timeFormat(millisSeconds:Int ): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }
    private fun updateList(items: List<ApiObj>?) {
        items?.let {
            for (item in items) {
                item.apply {
                    binding.temp.text = current.temp.toInt().toString() + "°"
                    binding.describtion.text = current.weather.get(0).description.toString()
                    binding.toolbarLayout.title = timezone
                    binding.iContent.humidityTv.text = current.humidity.toString()
                    binding.iContent.wendTv.text = current.wind_speed.toString()
                    binding.iContent.pressureTv.text = current.pressure.toString()
                    binding.iContent.cloudTv.text = current.clouds.toString()
                    binding.iContent.Date.text = dateFormat(current.dt)
                    binding.iContent.Time.text = timeFormat(current.dt)
                    dailyListAdapter.updateDays(daily)
                    hourlyListAdapter.updateHours(hourly)
                    Toast.makeText(applicationContext,"alerts"+item.alerts?.toString(),Toast.LENGTH_SHORT).show()
                }
                if (!item.alerts.isNullOrEmpty()) {
                    notifyUser(item.alerts)
//                    Toast.makeText(this,"alerts",Toast.LENGTH_SHORT).show()
//                    val tapNotification = Intent(applicationContext, ScrollingActivity::class.java)
//                    tapNotification.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    notificationUtils = NotificationUtils(this)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        var pendingIntent:PendingIntent=PendingIntent.getActivity(this, 1, tapNotification, PendingIntent.FLAG_ONE_SHOT)
//                        val nb: NotificationCompat.Builder? = notificationUtils.getAndroidChannelNotification(item.alerts?.get(0)?.event, ""
//                                +dateFormat(item.alerts?.get(0)?.start.toInt())+","+dateFormat(item.alerts?.get(0)?.end.toInt()) +"\n"+item.alerts?.get(0)?.description, pendingIntent)
//                        notificationUtils.getManager()?.notify(2, nb?.build())
//
//                    }
                }
            }

        }
    }

    private fun notifyUser(alert:List<Alerts>){
//        Toast.makeText(this,"alerts",Toast.LENGTH_SHORT).show()
//        val tapNotification = Intent(applicationContext, ScrollingActivity::class.java)
//        tapNotification.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils = NotificationUtils(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            var pendingIntent:PendingIntent=PendingIntent.getActivity(this, 1,
//                tapNotification, PendingIntent.FLAG_ONE_SHOT)
            val nb: NotificationCompat.Builder? = notificationUtils.getAndroidChannelNotification(alert.get(0)?.event, ""
                    +dateFormat(alert.get(0)?.start.toInt())+","+dateFormat(alert.get(0)?.end.toInt()) +"\n"+alert.get(0)?.description)
            notificationUtils.getManager()?.notify(3, nb?.build())
    }
    }

    private fun updateCurrent(item: ApiObj) {

                item.apply {
                    binding.temp.text= current.temp.toInt().toString()+"°"
                    binding.describtion.text= current.weather.get(0).description.toString()
                    binding.toolbarLayout.title= timezone
                    binding.iContent.humidityTv.text=current.humidity.toString()
                    binding.iContent.wendTv.text=current.wind_speed.toString()
                    binding.iContent.pressureTv.text=current.pressure.toString()
                    binding.iContent.cloudTv.text=current.clouds.toString()
                    binding.iContent.Date.text= dateFormat(current.dt)
                    binding.iContent.Time.text= timeFormat(current.dt)
                    dailyListAdapter.updateDays(daily)
                    hourlyListAdapter.updateHours(hourly)
                }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_favorites -> {
                val intent: Intent = Intent(this,FavoritesActivity::class.java)
                startActivity(intent)
                true}
            else -> super.onOptionsItemSelected(item)
        }
    }


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


    //         scrollingActivityViewModal = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(ScrollingActivityVM::class.java)
//        scrollingActivityViewModal.loadWeather().observe(this,
//            Observer <List<ApiObj>> { tasks -> updateList(tasks) })

//        var ScrollingActivityViewModal = ScrollingActivityVM();
//        ScrollingActivityViewModal.loadWeather();
//        Log.i("oncreate","egergerg")
//        ScrollingActivityViewModal.getApiObj().observe(this,
//            Observer<ApiObj> { tasks -> updateList(tasks) })
}