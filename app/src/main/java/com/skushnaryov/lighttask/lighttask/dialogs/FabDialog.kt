package com.skushnaryov.lighttask.lighttask.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.skushnaryov.lighttask.lighttask.R

class FabDialog : DialogFragment() {
    lateinit var listener: OnFabDialogItemListener
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
                .setTitle(R.string.fabDialogTitle)
                .setItems(R.array.fabDialogArr) { _, i -> listener.onFabDialogItemClick(i)}

        return builder.create()
    }

    interface OnFabDialogItemListener {
        fun onFabDialogItemClick(position: Int)
    }
}