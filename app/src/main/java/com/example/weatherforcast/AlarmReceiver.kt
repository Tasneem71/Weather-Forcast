package com.example.weatherforcast

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.example.weatherforcast.data.roomdb.LocalDataSource
import com.example.weatherforcast.utils.NotificationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class AlarmReceiver : BroadcastReceiver() {
    lateinit var prefs: SharedPreferences
    lateinit var notificationUtils: NotificationUtils
    var notificationManager: NotificationManager?=null
    override fun onReceive(context: Context, intent: Intent) {
        notificationUtils = NotificationUtils(context)
        notificationManager = notificationUtils.getManager()
        val c: Calendar = Calendar.getInstance()
        val LongEndTime = intent.getLongExtra("endTime", 0)
        var id = intent.getIntExtra("id", 0)
        Log.i("alarmID", "" + id)
        Log.i("alarm", " " + LongEndTime + "  " + c.timeInMillis)
        Toast.makeText(context, " " + LongEndTime + "  " + c.timeInMillis, Toast.LENGTH_SHORT).show()
        if (LongEndTime < c.timeInMillis) {
            cancelAlarm(id, context)
            CoroutineScope(Dispatchers.IO).launch {
                val localDataSource = LocalDataSource(context.applicationContext as Application)
                localDataSource.deleteAlarmObj(id)
            }
            Log.i("alarm", " cancel")
            Toast.makeText(context, "cancel ", Toast.LENGTH_SHORT).show()

        } else {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val timeZone = prefs.getString("timezone", "").toString()
            CoroutineScope(Dispatchers.IO).launch {
                val localDataSource = LocalDataSource(context.applicationContext as Application)
                val apiObj=localDataSource.getApiObj(timeZone)
                val event = intent.getStringExtra("event")
                if (apiObj.current.weather.get(0).description.contains(event + "", ignoreCase = true)) {
                    notifyUser(context,event+"",apiObj.current.weather.get(0).description,id)
                }

            }

        }
        Toast.makeText(context, "This toast will be shown every X minutes", Toast.LENGTH_SHORT).show()
    }


    private fun cancelAlarm(id: Int, context: Context) {
        notificationManager?.cancel(id)
        Log.i("alarmID", "" + id)
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }


    private fun notifyUser(context: Context, event: String,describtion:String,id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nb: NotificationCompat.Builder? = notificationUtils.getAndroidChannelNotification("Event: "+event, "Be aware there is " + describtion)
            notificationUtils.getManager()?.notify(id, nb?.build())
        }

    }

}