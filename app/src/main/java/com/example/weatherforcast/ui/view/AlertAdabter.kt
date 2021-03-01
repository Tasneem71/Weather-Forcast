package com.example.weatherforcast.ui.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.AlarmReceiver
import com.example.weatherforcast.data.entity.AlarmObj
import com.example.weatherforcast.databinding.AlarmItemBinding
import com.example.weatherforcast.ui.viewModel.AlartViewModel
import java.util.ArrayList

class AlertAdabter (var alarmList: ArrayList<AlarmObj>,alartViewModel: AlartViewModel, context: Context) : RecyclerView.Adapter<AlertAdabter.VH>() {
    lateinit var context: Context
    lateinit var alartViewModel: AlartViewModel
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
        return VH(viewBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.myView.alarmType.text =alarmList[position].event
        holder.myView.dateTime.text = ""+alarmList[position].start+" to "+alarmList[position].end
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

}