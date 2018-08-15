package com.skushnaryov.lighttask.lighttask.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DateDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val listener = activity as DatePickerDialog.OnDateSetListener

        return DatePickerDialog(activity, listener, year, month, day)
    }
}