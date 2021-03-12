package com.example.weatherforcast.ui.view.Activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.R
import com.example.weatherforcast.data.entity.AlarmObj
import com.example.weatherforcast.databinding.ActivityAlertBinding
import com.example.weatherforcast.databinding.NewAlarmBinding
import com.example.weatherforcast.ui.view.Adapters.AlertAdabter
import com.example.weatherforcast.ui.viewModel.AlartViewModel
import com.example.weatherforcast.utils.AlarmReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AlertActivity : localizeActivity() {

    private lateinit var viewModel: AlartViewModel
    lateinit var binding: ActivityAlertBinding
    lateinit var alertAdabter : AlertAdabter
    lateinit var bindingDialog: NewAlarmBinding
    lateinit var dialog: Dialog
    var calStart = Calendar.getInstance()
    var calEnd = Calendar.getInstance()
    lateinit var alarmObj:AlarmObj
    lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(
                AlartViewModel::class.java)
        alertAdabter= AlertAdabter(arrayListOf(),viewModel,applicationContext)
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        var cal=Calendar.getInstance()
        alarmObj=AlarmObj("","","","",true,"")

        viewModel.getNavigate().observe(this, Observer<AlarmObj> {
            showDialog(it);
            Toast.makeText(this, "alarm: " + it.id, Toast.LENGTH_SHORT).show()
        })

        getAlarmData(viewModel)


        initUI()

        binding.fromTimeImg.setOnClickListener { v ->

            val hour = cal.get(Calendar.HOUR)
            val minute = cal.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
                calStart.set(Calendar.HOUR_OF_DAY,h)
                calStart.set(Calendar.MINUTE,m)
                calStart.set(Calendar.SECOND,0)

                val format = SimpleDateFormat("hh:mm aaa")
                alarmObj.start = format.format(calStart.time)
                //binding.fromTimeImg.setText(format.format(calStart.time))

//                alarmObj.start= "$h : $m"
                binding.fromTimeImg.text="$h : $m"

                Toast.makeText(this, h.toString() + " : " + m +" : " , Toast.LENGTH_LONG).show()

            }),hour,minute,false)

            tpd.show()
        }

        binding.toTimeImg.setOnClickListener { v ->

            val hour = cal.get(Calendar.HOUR)
            val minute = cal.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
                calEnd.set(Calendar.HOUR_OF_DAY,h)
                calEnd.set(Calendar.MINUTE,m)
                calEnd.set(Calendar.SECOND,0)
                val format = SimpleDateFormat("hh:mm aaa")
                alarmObj.end = format.format(calEnd.time)
//                alarmObj.end= "$h : $m"
                binding.toTimeImg.text="$h : $m"

                Toast.makeText(this, h.toString() + " : " + m  , Toast.LENGTH_LONG).show()

            }),hour,minute,false)

            tpd.show()

        }

        binding.calenderBtn.setOnClickListener { v ->
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calStart.set(Calendar.YEAR, year)
                calStart.set(Calendar.MONTH, monthOfYear)
                calStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                calEnd.set(Calendar.YEAR,year)
                calEnd.set(Calendar.MONTH, monthOfYear)
                calEnd.set(Calendar.DAY_OF_MONTH,dayOfMonth)

                Log.i("alarm",""+calStart)
                Log.i("alarm",""+calEnd.timeInMillis)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                alarmObj.Date = sdf.format(calStart.time)
                binding.calenderTv.text=sdf.format(calStart.time)

            }

            var datePickerDialog=DatePickerDialog(this, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);


        }

        binding.addAlarmBtn.setOnClickListener{

            if(calStart.timeInMillis<calEnd.timeInMillis) {
                alarmObj.description = binding.DescribtionTv.text.toString()
                alarmObj.event = getEventActivity()
                if (binding.loopSound.isChecked)
                    alarmObj.sound = false
                else
                    alarmObj.sound = true

                var id = 0
                var jop = CoroutineScope(Dispatchers.IO).launch {
                    id = viewModel.insertAlarmObj(alarmObj).toInt()
                    //handler.sendEmptyMessage(0)
                }
                jop.invokeOnCompletion { setAlarm(applicationContext, id, calStart, calEnd, alarmObj.event,alarmObj.sound)}


            }else{
                Toast.makeText(this, "Please Make Sure Your Timing is correct"  , Toast.LENGTH_LONG).show()
            }
            binding.addAarmView.setVisibility(View.GONE)
            binding.addBtn.setImageResource(R.drawable.ic_baseline_add_24)
        }

        binding.addBtn.setOnClickListener { v ->


            if (binding.addAarmView.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(binding.firstPart,
                        AutoTransition())
                binding.addAarmView.setVisibility(View.GONE)
                binding.addBtn.setImageResource(R.drawable.ic_baseline_add_24)
            } else {
                TransitionManager.beginDelayedTransition(binding.firstPart,
                        AutoTransition())
                binding.addAarmView.setVisibility(View.VISIBLE)
                binding.addBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }

//            showDialog(alarmObj)

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
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        var cal=Calendar.getInstance()
        bindingDialog.DescribtionTv.setText(alarmObj.description)
        bindingDialog.fromTimeImg.text=alarmObj.start
        bindingDialog.toTimeImg.text=alarmObj.end
        bindingDialog.calenderTv.text=alarmObj.Date


        bindingDialog.fromTimeImg.setOnClickListener { v ->

            val hour = cal.get(Calendar.HOUR)
            val minute = cal.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
                calStart.set(Calendar.HOUR_OF_DAY,h)
                calStart.set(Calendar.MINUTE,m)

                val format = SimpleDateFormat("hh:mm aaa")
                alarmObj.start = format.format(calStart.time)
                bindingDialog.fromTimeImg.setText(format.format(calStart.time))
                //alarmObj.start= "$h : $m"

                Toast.makeText(this, h.toString() + " : " + m +" : " , Toast.LENGTH_LONG).show()

            }),hour,minute,false)

            tpd.show()
        }

        bindingDialog.toTimeImg.setOnClickListener { v ->

            val hour = cal.get(Calendar.HOUR)
            val minute = cal.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
                calEnd.set(Calendar.HOUR_OF_DAY,h)
                calEnd.set(Calendar.MINUTE,m)

                val format = SimpleDateFormat("hh:mm aaa")
                alarmObj.end = format.format(calEnd.time)
                bindingDialog.toTimeImg.setText(format.format(calEnd.time))

                //alarmObj.end= "$h : $m"

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

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                alarmObj.Date = sdf.format(calStart.time)

            }

            var datePickerDialog=DatePickerDialog(this, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);


        }
        bindingDialog.closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        bindingDialog.addAlarmBtn.setOnClickListener{

            if(calStart.timeInMillis<calEnd.timeInMillis) {
                alarmObj.description = bindingDialog.DescribtionTv.text.toString()
                alarmObj.event = getEvent()
                if (bindingDialog.loopSound.isChecked)
                    alarmObj.sound = false
                else
                    alarmObj.sound = true

                var id = 0
                var jop = CoroutineScope(Dispatchers.IO).launch {
                    id = viewModel.insertAlarmObj(alarmObj).toInt()
                    //handler.sendEmptyMessage(0)
                }
                jop.invokeOnCompletion { setAlarm(applicationContext, id, calStart, calEnd, alarmObj.event,alarmObj.sound) }


            }else{
                Toast.makeText(this, "Please Make Sure Your Timing is correct"  , Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }
        dialog.show()
        dialog.getWindow()?.setAttributes(lp)
    }

    private fun setAlarm(context:Context,id:Int,calStart:Calendar,calEnd: Calendar,event:String,sound:Boolean) {
        Log.i("alarm","the first")
        val mIntent = Intent(context, AlarmReceiver::class.java)
        mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        mIntent.putExtra("endTime",calEnd.timeInMillis)
        mIntent.putExtra("id",id)
        mIntent.putExtra("event",event)
        mIntent.putExtra("sound",sound)
        val mPendingIntent = PendingIntent.getBroadcast(this, id, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        Log.i("cal",""+calStart)
        val mAlarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  calStart.timeInMillis,
                2*1000, mPendingIntent)
        Log.i("alarm",""+calEnd)
    }


    private fun getEvent(): String {
        var event = ""
        var arr = this.resources.getStringArray(R.array.event_options)
        when (bindingDialog.eventSpinner.getSelectedItemPosition()) {
            0 -> event = arr[0]
            1 -> event = arr[1]
            2 -> event = arr[2]
            3 -> event = arr[3]
            4 -> event = arr[4]
            5 -> event = arr[5]
        }
        return event
    }

    private fun getEventActivity(): String {
        var event = ""
        var arr = this.resources.getStringArray(R.array.event_options)
        when (binding.eventSpinner.getSelectedItemPosition()) {
            0 -> event = arr[0]
            1 -> event = arr[1]
            2 -> event = arr[2]
            3 -> event = arr[3]
            4 -> event = arr[4]
            5 -> event = arr[5]
        }
        return event
    }

    private fun initUI() {
        binding.alarmList.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alertAdabter

        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                alertAdabter.removeFromAdapter(viewHolder)
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.alarmList)



    }

    override fun onResume() {
        if (prefs.getBoolean("THEME_MODE", false)){
            backgroundBasedOnTime(binding.rootlay,this)
        }
        super.onResume()
    }






}