package com.example.weatherforcast.ui.view

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.FavoritesViewModel
import com.example.weatherforcast.data.entity.ApiObj
import com.example.weatherforcast.databinding.FavItemBinding
import java.util.*

class FavoriteAdapter(var FavList: ArrayList<ApiObj>,favoritesViewModel: FavoritesViewModel,context:Context) : RecyclerView.Adapter<FavoriteAdapter.VH>() {
    lateinit var favoritesViewModel: FavoritesViewModel
    lateinit var context: Context
    init {
        this.favoritesViewModel=favoritesViewModel
        this.context=context
    }


    fun updateHours(newHourlyList: List<ApiObj>) {
        FavList.clear()
        FavList.addAll(newHourlyList)
        notifyDataSetChanged()
    }

    class VH(var myView: FavItemBinding) : RecyclerView.ViewHolder(myView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewBinding =
            FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(viewBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.myView.favDesc.text =FavList[position].current.weather.get(0).description.toString()
        holder.myView.timezoneTxt.text = FavList[position].timezone.toString()
        holder.myView.favTemb.text = FavList[position].current.temp.toInt().toString()+"°"
        holder.myView.favItem.setOnClickListener {
            val intent: Intent = Intent(context,ScrollingActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("timeZone",FavList[position].timezone)
            context.startActivity(intent)
        }

        holder.myView.favItem.setOnClickListener {
            favoritesViewModel.onShowClick(FavList[position])
        }

    }

    override fun getItemCount() = FavList.size

}