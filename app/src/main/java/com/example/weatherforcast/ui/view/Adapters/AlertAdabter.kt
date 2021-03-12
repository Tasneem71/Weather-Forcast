package com.example.weatherforcast.ui.view.Adapters

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.utils.AlarmReceiver
import com.example.weatherforcast.data.entity.AlarmObj
import com.example.weatherforcast.databinding.AlarmItemBinding
import com.example.weatherforcast.ui.viewModel.AlartViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList

class AlertAdabter (var alarmList: ArrayList<AlarmObj>,alartViewModel: AlartViewModel, context: Context) : RecyclerView.Adapter<AlertAdabter.VH>() {
    lateinit var context: Context
    lateinit var alartViewModel: AlartViewModel
    lateinit var removedAlarmObj:AlarmObj
    private var removedposition=0
    init {
        this.context=context
        this.alartViewModel=alartViewModel
    }


    fun updateAlarms(newAlarmList: List<AlarmObj>) {
        alarmList.clear()
        alarmList.addAll(newAlarmList)
        notifyDataSetChanged()
    }

    class VH(var myView: AlarmItemBinding) : RecyclerView.ViewHolder(myView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewBinding =
                AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(
            viewBinding
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.myView.alarmType.text =alarmList[position].event
        holder.myView.dateTime.text = ""+alarmList[position].start+" to "+alarmList[position].end+" "+alarmList[position].Date
        holder.myView.details.text =alarmList[position].description

        holder.myView.editBtn.setOnClickListener {
            alartViewModel.onEditClick(alarmList[position])
        }

        holder.myView.deleteBtn.setOnClickListener {
            alartViewModel.deleteAlarmObj(alarmList[position].id)
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context,alarmList[position].id, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }


    }

    override fun getItemCount() = alarmList.size

    fun removeForever(id: Int){
        alartViewModel.deleteAlarmObj(id)
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context,id, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
    fun removeFromAdapter(viewHolder:RecyclerView.ViewHolder){
        removedposition=viewHolder.adapterPosition
        removedAlarmObj=alarmList[viewHolder.adapterPosition]

        alarmList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)

        Snackbar.make(viewHolder.itemView, "${removedAlarmObj.event} removed", Snackbar.LENGTH_LONG).apply {
            setAction("UNDO") {
                alarmList.add(removedposition, removedAlarmObj)
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
                        removeForever(removedAlarmObj.id)
                    }

                }
            })
            setTextColor(Color.parseColor("#FFFFFFFF"))
            setActionTextColor(Color.parseColor("#09A8A8"))
            setBackgroundTint(Color.parseColor("#616161"))
            duration.minus(1)
        }.show()

    }


}