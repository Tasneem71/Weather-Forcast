package com.example.weatherforcast.ui.view.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforcast.R
import com.example.weatherforcast.data.entity.Hourly
import com.example.weatherforcast.databinding.HourItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HourAbapter(var items: ArrayList<Hourly>) : RecyclerView.Adapter<HourAbapter.VH>() {

    fun updateHours(newHourlyList: List<Hourly>) {
        items.clear()
        items.addAll(newHourlyList)
        notifyDataSetChanged()
    }

    class VH(var myView: HourItemBinding) : RecyclerView.ViewHolder(myView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewBinding =
            HourItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(
            viewBinding
        )
    }

    private fun timeFormat(millisSeconds:Int ): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.myView.itemTime.text =timeFormat(items[position].dt.toInt())
        holder.myView.itemTemp.text = items[position].temp.toInt().toString()+"°"
        Glide.with(holder.myView.itemIcon.context).
        load(iconLinkgetter(items[position].weather.get(0).icon)).
        placeholder(R.drawable.ic_baseline_wb_sunny_24).into(holder.myView.itemIcon)

    }

    override fun getItemCount() = items.size

    fun iconLinkgetter(iconName:String):String="https://openweathermap.org/img/wn/"+iconName+"@2x.png"

}
