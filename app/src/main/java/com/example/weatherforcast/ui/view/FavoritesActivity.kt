package com.example.weatherforcast.ui.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforcast.FavoritesViewModel
import com.example.weatherforcast.R
import com.example.weatherforcast.data.entity.ApiObj
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.weatherforcast.databinding.ActivityFavoritesBinding
import com.example.weatherforcast.databinding.FavDailogBinding
import com.example.weatherforcast.ui.MapsActivity
import java.text.SimpleDateFormat
import java.util.*

class FavoritesActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoritesViewModel
    lateinit var binding: ActivityFavoritesBinding
    private lateinit var addBtn: FloatingActionButton
    lateinit var favoriteAdapter : FavoriteAdapter
    lateinit var bindingDialog: FavDailogBinding
    var dailyListAdapter = DayAdapter(arrayListOf())
    var hourlyListAdapter = HourAbapter(arrayListOf())
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(
            FavoritesViewModel::class.java)
        favoriteAdapter=FavoriteAdapter(arrayListOf(),viewModel,applicationContext)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("id")) {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            observeViewModel(viewModel,lat,lon)
            Toast.makeText(this,  " " + lon+lat,Toast.LENGTH_SHORT).show()
        }
        else {
            getWeatherData(viewModel)
        }

        binding.addBtn.setOnClickListener{
            val intent: Intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
            finish()
        }
        initUI()
        viewModel.getNavigate().observe(this, Observer<String> { timeZone ->
            viewModel.deleteApiObj(timeZone)
        })

        viewModel.showObj().observe(this, Observer<ApiObj> {
            showDialog(it);
            Toast.makeText(this, "you have arrived" + it.timezone, Toast.LENGTH_SHORT).show()
        })

    }

    private fun initUI() {
        binding.rvFavList.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = favoriteAdapter

        }
    }

    private fun observeViewModel(viewModel: FavoritesViewModel, lat:Double, lon:Double) {
        viewModel.loadWeather(applicationContext,lat, lon).observe(this, {
            favoriteAdapter.updateHours(it)
        })
    }

    private fun getWeatherData(viewModel: FavoritesViewModel) {
        viewModel.getWeatherList().observe(this, {
            favoriteAdapter.updateHours(it)
        })
    }


    private fun showDialog(weatherObj: ApiObj){
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bindingDialog = FavDailogBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)
        initDialog()
        updateDialog(weatherObj)
        bindingDialog.closeBtn.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun updateDialog(item: ApiObj) {
        item?.let {
            item.apply {
                bindingDialog.dialogContent.temp.text=current.temp.toInt().toString()+"°"
                bindingDialog.dialogContent.describtion.text= current.weather.get(0).description.toString()
                bindingDialog.dialogContent.toolbarLayout.title=timezone
                bindingDialog.dialogContent.iContent.humidityTv.text=current.humidity.toString()
                bindingDialog.dialogContent.iContent.wendTv.text=current.wind_speed.toString()
                bindingDialog.dialogContent.iContent.pressureTv.text=current.pressure.toString()
                bindingDialog.dialogContent.iContent.cloudTv.text=current.clouds.toString()
                bindingDialog.dialogContent.iContent.Date.text= dateFormat(current.dt)
                bindingDialog.dialogContent.iContent.Time.text= timeFormat(current.dt)
                dailyListAdapter.updateDays(daily)
                hourlyListAdapter.updateHours(hourly)
            }
        }
    }
    private fun timeFormat(millisSeconds: Int): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }
    private fun dateFormat(milliSeconds: Int):String{
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds.toLong() * 1000)
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        var day=calendar.get(Calendar.DAY_OF_MONTH).toString()
        var year=calendar.get(Calendar.YEAR).toString()
        return day+month +year

    }
    fun initDialog(){
        bindingDialog.dialogContent.iContent.hourList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyListAdapter

        }
        bindingDialog.dialogContent.iContent.dayList.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = dailyListAdapter
        }
    }



}