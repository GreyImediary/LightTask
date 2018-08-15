package com.skushnaryov.lighttask.lighttask.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import java.util.*

class TimeDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = Calendar.getInstance()
        val hour = date.get(Calendar.HOUR_OF_DAY)
        val minute = date.get(Calendar.MINUTE)
        val listener = activity as TimePickerDialog.OnTimeSetListener

        return TimePickerDialog(context, listener, hour, minute, DateFormat.is24HourFormat(context))
    }

}