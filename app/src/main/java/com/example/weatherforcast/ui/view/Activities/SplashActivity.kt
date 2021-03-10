package com.example.weatherforcast.ui.view.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.weatherforcast.R
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    val activityScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val exceptionHandlerException = CoroutineExceptionHandler { _, throwable ->throwable.printStackTrace()
            Log.i("tasneem","exption")
        }
        activityScope.launch {
                delay(4000)
                var intent = Intent(this@SplashActivity, ScrollingActivity::class.java)
                startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}