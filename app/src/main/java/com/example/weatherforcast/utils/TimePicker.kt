package com.example.weatherforcast.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePicker : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calender = Calendar.getInstance()
        val hour = calender[Calendar.HOUR_OF_DAY]
        val minute = calender[Calendar.MINUTE]
        val listener = activity as TimePickerDialog.OnTimeSetListener?
        return TimePickerDialog(context, listener, hour, minute, DateFormat.is24HourFormat(context))
    }
}