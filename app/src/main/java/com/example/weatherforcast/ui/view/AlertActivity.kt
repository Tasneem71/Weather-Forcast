package com.example.weatherforcast.ui.view

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforcast.AlarmReceiver
import com.example.weatherforcast.R
import com.example.weatherforcast.data.entity.AlarmObj
import com.example.weatherforcast.databinding.ActivityAlertBinding
import com.example.weatherforcast.databinding.NewAlarmBinding
import com.example.weatherforcast.ui.viewModel.AlartViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AlertActivity : AppCompatActivity() {

    private lateinit var viewModel: AlartViewModel
    lateinit var binding: ActivityAlertBinding
    private lateinit var addBtn: FloatingActionButton
    lateinit var alertAdabter : AlertAdabter
    lateinit var bindingDialog: NewAlarmBinding
    lateinit var dialog: Dialog
    lateinit var datePickerDialog: DatePickerDialog
    lateinit var timePickerDialogfrom: TimePickerDialog
    lateinit var timePickerDialogto: TimePickerDialog
    var calStart = Calendar.getInstance()
    var calEnd = Calendar.getInstance()
    companion object{

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(
                AlartViewModel::class.java)
        alertAdabter= AlertAdabter(arrayListOf(),viewModel,applicationContext)
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getNavigate().observe(this, Observer<AlarmObj> {
            showDialog(it);
            Toast.makeText(this, "alarm: " + it.id, Toast.LENGTH_SHORT).show()
        })

        getAlarmData(viewModel)

        initUI()

        binding.addBtn.setOnClickListener { v ->
            var alarmObj:AlarmObj=AlarmObj("","","","",true,"")
            showDialog(alarmObj)

        }


    }

    private fun getAlarmData(viewModel: AlartViewModel) {
        viewModel.getAlarmList().observe(this) {
            alertAdabter.updateAlarms(it)
        }
    }


    private fun showDialog(alarmObj: AlarmObj){
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bindingDialog = NewAlarmBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)


        bindingDialog.fromTimeImg.setOnClickListener { v ->

            val hour = calStart.get(Calendar.HOUR)
            val minute = calStart.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
                calStart.set(Calendar.HOUR,h)
                calStart.set(Calendar.MINUTE,m)
                alarmObj.start= "$h : $m"

                Toast.makeText(this, h.toString() + " : " + m +" : " , Toast.LENGTH_LONG).show()

            }),hour,minute,false)

            tpd.show()
        }

        bindingDialog.toTimeImg.setOnClickListener { v ->

            val hour = calEnd.get(Calendar.HOUR)
            val minute = calEnd.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
                calEnd.set(Calendar.HOUR,h)
                calEnd.set(Calendar.MINUTE,m)
                alarmObj.end= "$h : $m"

                Toast.makeText(this, h.toString() + " : " + m  , Toast.LENGTH_LONG).show()

            }),hour,minute,false)

            tpd.show()

        }

        bindingDialog.calenderBtn.setOnClickListener { v ->
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calStart.set(Calendar.YEAR, year)
                calStart.set(Calendar.MONTH, monthOfYear)
                calStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                calEnd.set(Calendar.YEAR, year)
                calEnd.set(Calendar.MONTH, monthOfYear)
                calEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd.MM.yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                alarmObj.Date = sdf.format(calStart.time)

            }

            DatePickerDialog(this, dateSetListener,
                    calStart.get(Calendar.YEAR),
                    calStart.get(Calendar.MONTH),
                    calStart.get(Calendar.DAY_OF_MONTH)).show()

        }
        bindingDialog.addAlarmBtn.setOnClickListener{
            alarmObj.description=bindingDialog.DescribtionTv.text.toString()
            alarmObj.event=getEvent()
            if(bindingDialog.loopSound.isChecked)
                alarmObj.sound=false
            else
                alarmObj.sound = true

            var id=0
            var jop=CoroutineScope(Dispatchers.IO).launch {
                id= viewModel.insertAlarmObj(alarmObj).toInt()
                //handler.sendEmptyMessage(0)
            }
            jop.invokeOnCompletion { setAlarm(applicationContext,id,calStart,calEnd,alarmObj.event) }




            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setAlarm(context:Context,id:Int,calStart:Calendar,calEnd: Calendar,event:String) {
        Log.i("alarm","the first")
        val mIntent = Intent(context, AlarmReceiver::class.java)
        mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        mIntent.putExtra("endTime",calEnd.timeInMillis)
        mIntent.putExtra("id",id)
        mIntent.putExtra("event",event)
        val mPendingIntent = PendingIntent.getBroadcast(this, id, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        Log.i("cal",""+calStart)
        val mAlarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  calStart.timeInMillis,
                2*1000, mPendingIntent)
        Log.i("alarm",""+calEnd)
    }


    private fun getEvent(): String {
        var repetation = ""
        when (bindingDialog.eventSpinner.getSelectedItemPosition()) {
            0 -> repetation = "rain"
            1 -> repetation = "snow"
            2 -> repetation = "clear"
            3 -> repetation = "thunderstorm"
        }
        return repetation
    }

    private fun initUI() {
        binding.alarmList.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alertAdabter

        }
    }


}