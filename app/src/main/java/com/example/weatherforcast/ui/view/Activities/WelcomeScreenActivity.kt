package com.example.weatherforcast.ui.view.Activities

import com.example.weatherforcast.R
import com.stephentuso.welcome.BasicPage
import com.stephentuso.welcome.WelcomeActivity
import com.stephentuso.welcome.WelcomeConfiguration

class WelcomeScreenActivity : WelcomeActivity() {

    override fun configuration(): WelcomeConfiguration {
        return WelcomeConfiguration.Builder(this)
            .defaultBackgroundColor(R.color.black)
            .page( BasicPage(
                    R.drawable.weather,
                getString(R.string.NeverTitle),getString(R.string.neverDescribe))
                .background(R.color.lightGreen)
            )
            .page( BasicPage(
                    R.drawable.manage,
                getString(R.string.manageTitle),getString(R.string.manageDescribe))
                .background(R.color.lightGreen)
            )
            .page( BasicPage(
                    R.drawable.alart,
                getString(R.string.allwaysTitle),getString(R.string.alwaysDescribe))
                .background(R.color.lightGreen)
            )
            .swipeToDismiss(true)
            .build()
    }
}