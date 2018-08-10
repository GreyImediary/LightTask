package com.skushnaryov.lighttask.lighttask.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.skushnaryov.lighttask.lighttask.R

class GroupDialog : DialogFragment() {
    lateinit var clickListener: OnGroupDialogItemClickListener
    var groups: List<String> = emptyList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(R.string.groupDialogTitle)
                .setItems(groups.toTypedArray()) { _, i -> clickListener.onGroupItemClick(i) }
                .setPositiveButton(R.string.groupDialogButton) { dialog, _ ->
                    dialog.cancel()
                    val addBuilder = AlertDialog.Builder(activity)
                    val view = layoutInflater.inflate(R.layout.dialog_add_group_layout, null)
                    addBuilder.setTitle(R.string.groupAddDialogTitle)
                            .setView(view)
                            .setPositiveButton(R.string.create) { _, _ ->
                                clickListener.onGroupCreateClick(view)
                            }
                    addBuilder.create().show()
                }

        return builder.create()
    }

    interface OnGroupDialogItemClickListener {
        fun onGroupItemClick(position: Int)
        fun onGroupCreateClick(view: View)
    }
}