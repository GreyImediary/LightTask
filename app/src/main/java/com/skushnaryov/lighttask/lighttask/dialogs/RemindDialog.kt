package com.skushnaryov.lighttask.lighttask.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.skushnaryov.lighttask.lighttask.R

class RemindDialog : DialogFragment() {
    lateinit var clickListener: OnItemRemindClickListener
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
                .setTitle(R.string.remindDialogTitle)
                .setItems(R.array.remindArray) { _, i ->
                    clickListener.onRemindItemClick(i)
                }
        return builder.create()
    }

    interface OnItemRemindClickListener {
        fun onRemindItemClick(position: Int)
    }
}