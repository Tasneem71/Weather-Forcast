package com.example.weatherforcast

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FLAG_INSISTENT
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
        var sound=intent.getBooleanExtra("sound",true)
        Log.i("alarmID", "" + id)
        Log.i("alarmٍSound", "" + sound)
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
            val event = intent.getStringExtra("event")
            CoroutineScope(Dispatchers.IO).launch {
                val localDataSource = LocalDataSource(context.applicationContext as Application)
                val apiObj=localDataSource.getApiObj(timeZone)
                if (apiObj.current.weather.get(0).description.contains(event + "", ignoreCase = true)) {
                    notifyUser(context,event+"",apiObj.current.weather.get(0).description,id,sound)
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


    private fun notifyUser(context: Context, event: String,describtion:String,id: Int,sound:Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nb: NotificationCompat.Builder? = notificationUtils.getAndroidChannelNotification("Event: "+event, "Be aware there is " + describtion,sound)
            val notification=nb?.build()
            if(!sound){
                notification?.flags= FLAG_INSISTENT
            }
            notificationUtils.getManager()?.notify(id, notification)
        }

    }

}