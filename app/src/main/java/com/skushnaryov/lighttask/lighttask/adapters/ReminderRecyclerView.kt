package com.skushnaryov.lighttask.lighttask.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.utils.Constants
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.db.entities.Reminder
import com.skushnaryov.lighttask.lighttask.inflate
import kotlinx.android.synthetic.main.item_reminder.view.*

class ReminderRecyclerView(
        private val context: Context,
        private val listener: OnReminderListener) : RecyclerView.Adapter<ReminderRecyclerView.ReminderHolder>() {
    var reminderList = emptyList<Reminder>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderHolder =
            ReminderHolder(parent.inflate(R.layout.item_reminder), context, listener)

    override fun getItemCount(): Int = reminderList.size

    override fun onBindViewHolder(holder: ReminderHolder, position: Int) = holder.bind(reminderList[position])


    class ReminderHolder(itemView: View,
                         private val context: Context,
                         private val listener: OnReminderListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(reminder: Reminder) {
            val timeTypeArr = context.resources.getStringArray(R.array.timeArr)
            val timeType = when (reminder.timeType) {
                Constants.REMIND_MIN -> timeTypeArr[0]
                Constants.REMIND_HOUR -> timeTypeArr[1]
                Constants.REMIND_DAY -> timeTypeArr[2]
                else -> ""
            }
            val timeString = "${itemView.context.getString(R.string.every)} ${reminder.time} $timeType"
            itemView.reminderName_textView.text = reminder.name
            itemView.reminderTime_textView.text = timeString

            itemView.reminderOn_switch.isChecked = reminder.isOn

            itemView.reminderOn_switch.setOnCheckedChangeListener { _, isChecked -> listener.onSwitchChecked(isChecked, reminder) }

            itemView.setOnLongClickListener {
                val popup = PopupMenu(itemView.context, itemView)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener {
                    listener.onPopupItemClick(it.itemId, reminder)
                }
                popup.show()
                true
            }
        }
    }

    interface OnReminderListener {
        fun onSwitchChecked(isChecked: Boolean, reminder: Reminder)
        fun onPopupItemClick(itemId: Int, reminder: Reminder): Boolean
    }
}