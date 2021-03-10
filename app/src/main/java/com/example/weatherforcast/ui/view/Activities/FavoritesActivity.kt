package com.example.weatherforcast.ui.view.Activities

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforcast.ui.viewModel.FavoritesViewModel
import com.example.weatherforcast.R
import com.example.weatherforcast.data.entity.ApiObj
import com.example.weatherforcast.data.retro.SettingsEnum
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.weatherforcast.databinding.ActivityFavoritesBinding
import com.example.weatherforcast.databinding.FavDailogBinding
import com.example.weatherforcast.ui.view.Adapters.DayAdapter
import com.example.weatherforcast.ui.view.Adapters.FavoriteAdapter
import com.example.weatherforcast.ui.view.Adapters.HourAbapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FavoritesActivity : localizeActivity() {

    private lateinit var viewModel: FavoritesViewModel
    lateinit var binding: ActivityFavoritesBinding
    private lateinit var addBtn: FloatingActionButton
    lateinit var favoriteAdapter : FavoriteAdapter
    lateinit var bindingDialog: FavDailogBinding
    var dailyListAdapter =
        DayAdapter(
            arrayListOf(),
            this
        )
    var hourlyListAdapter =
        HourAbapter(arrayListOf())
    lateinit var dialog: Dialog
    lateinit var prefs: SharedPreferences
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(
            FavoritesViewModel::class.java)
        favoriteAdapter=FavoriteAdapter(arrayListOf(),viewModel,applicationContext)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        if(intent.hasExtra("id")) {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var unit=prefs.getString("UNIT_SYSTEM", SettingsEnum.IMPERIAL.Value).toString()
            var lang=prefs.getString("APP_LANG", SettingsEnum.ENGLISH.Value).toString()
            observeViewModel(viewModel, lat, lon, lang,unit)
            Toast.makeText(this, " " + lon + lat,Toast.LENGTH_SHORT).show()
        }
        else {
            getWeatherData(viewModel)
        }

        val timeZone=prefs.getString("timezone", "your location").toString()
        binding.timezoneTv.text=timeZone

        binding.addBtn.setOnClickListener{
            val intent: Intent = Intent(this,
                MapsActivity::class.java)
            intent.putExtra("mapId",2)
            startActivity(intent)
            finish()
        }

        colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_delete_forever_24)!!

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
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                favoriteAdapter.removeFromAdapter(viewHolder)
            }
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float,
                                     dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView
                val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                } else {
                    colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvFavList)

    }

    private fun observeViewModel(viewModel: FavoritesViewModel, lat: Double, lon: Double,lang:String,unit:String) {
        viewModel.loadWeather(applicationContext, lat, lon, lang, unit).observe(this, {
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
        bindingDialog.dialogContent.addFav.visibility=View.INVISIBLE
        bindingDialog.dialogContent.menu.visibility=View.INVISIBLE
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
                bindingDialog.dialogContent.titletv.text=timezone
                bindingDialog.dialogContent.iContent.humidityTv.text=current.humidity.toString()
                bindingDialog.dialogContent.iContent.wendTv.text=current.wind_speed.toString()
                bindingDialog.dialogContent.iContent.pressureTv.text=current.pressure.toString()
                bindingDialog.dialogContent.iContent.cloudTv.text=current.clouds.toString()
                bindingDialog.dialogContent.iContent.Date.text= viewModel.dateFormat(current.dt)
                bindingDialog.dialogContent.iContent.Time.text= viewModel.timeFormat(current.dt)

                CoroutineScope(Dispatchers.Main).launch{
                    Glide.with(bindingDialog.dialogContent.currentIcon).
                    load(iconLinkgetter(current.weather.get(0).icon)).
                    placeholder(R.drawable.ic_baseline_wb_sunny_24).into(bindingDialog.dialogContent.currentIcon)
                }
                dailyListAdapter.updateDays(daily)
                hourlyListAdapter.updateHours(hourly)
            }
        }
    }
    fun iconLinkgetter(iconName:String):String="https://openweathermap.org/img/wn/"+iconName+"@2x.png"

    fun initDialog(){
        bindingDialog.dialogContent.iContent.hourList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyListAdapter

        }
        bindingDialog.dialogContent.iContent.dayList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dailyListAdapter
        }
    }



}