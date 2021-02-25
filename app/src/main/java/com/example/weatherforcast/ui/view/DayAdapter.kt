package com.example.weatherforcast.ui.view

import com.example.weatherforcast.data.entity.Daily
import com.example.weatherforcast.databinding.DayItemBinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforcast.R
import java.util.*
import kotlin.collections.ArrayList

class DayAdapter(var DailyList: ArrayList<Daily>) :
        RecyclerView.Adapter<DayAdapter.DailyViewHolder>() {

    fun updateDays(newDailyList: List<Daily>) {
        DailyList.clear()
        DailyList.addAll(newDailyList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) :DailyViewHolder {
        val viewBinding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyViewHolder(viewBinding)
    }

    override fun getItemCount() = DailyList.size
    /* override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {​​
         holder.bind(DailyList[position])
     }​​*/
    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(DailyList[position].dt.toLong()*1000)
        var date=calendar.get(Calendar.DAY_OF_WEEK).toString()+"/"+calendar.get(Calendar.MONTH+1).toString()
        holder.myView.tempMin.text = DailyList[position].temp.min.toString()
        holder.myView.tempMax.text = DailyList[position].temp.max.toString()
        holder.myView.describ.text = DailyList[position].weather.get(0).description.toString()
        holder.myView.dayname.text = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        holder.myView.daydate.text = date
        Glide.with(holder.myView.imageView.context).
        load(iconLinkgetter(DailyList[position].weather.get(0).icon)).
        placeholder(R.drawable.ic_baseline_wb_sunny_24).into(holder.myView.imageView)

    }
    class DailyViewHolder(var myView: DayItemBinding) : RecyclerView.ViewHolder(myView.root)

    fun iconLinkgetter(iconName:String):String="https://openweathermap.org/img/wn/"+iconName+"@2x.png"
}

