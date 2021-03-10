package com.example.weatherforcast.ui.view.Adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.data.entity.AlarmObj
import com.example.weatherforcast.ui.viewModel.FavoritesViewModel
import com.example.weatherforcast.data.entity.ApiObj
import com.example.weatherforcast.databinding.FavItemBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*

class FavoriteAdapter(var FavList: ArrayList<ApiObj>, favoritesViewModel: FavoritesViewModel, context:Context) : RecyclerView.Adapter<FavoriteAdapter.VH>() {
     var favoritesViewModel: FavoritesViewModel
     var context: Context
    lateinit var removedApiObj: ApiObj
    private var removedposition=0
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
        return VH(
            viewBinding
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.myView.favDesc.text =FavList[position].current.weather.get(0).description.toString()
        holder.myView.timezoneTxt.text = FavList[position].timezone.toString()
        holder.myView.favTemb.text = FavList[position].current.temp.toInt().toString()+"°"
        holder.myView.locImg.setOnClickListener {
            favoritesViewModel.onRemoveClick(FavList[position].timezone)
        }

        holder.myView.favItem.setOnClickListener {
            favoritesViewModel.onShowClick(FavList[position])
        }

    }

    fun removeFromAdapter(viewHolder:RecyclerView.ViewHolder){
        removedposition=viewHolder.adapterPosition
        removedApiObj=FavList[viewHolder.adapterPosition]

        FavList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)

        Snackbar.make(viewHolder.itemView, "${removedApiObj.timezone} removed", Snackbar.LENGTH_LONG).apply {
            setAction("UNDO") {
                FavList.add(removedposition, removedApiObj)
                notifyItemInserted(removedposition)
            }
            addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                    Log.i("snack", "onShown")
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    // Snackbar closed on its own
                    Log.i("snack", "on click")
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        // Snackbar closed on its own
                        favoritesViewModel.onRemoveClick(removedApiObj.timezone)
                    }

                }
            })
            setTextColor(Color.parseColor("#FFFFFFFF"))
            setActionTextColor(Color.parseColor("#09A8A8"))
            setBackgroundTint(Color.parseColor("#616161"))
            duration.minus(1)
        }.show()

    }

    override fun getItemCount() = FavList.size

}