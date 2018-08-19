package com.skushnaryov.lighttask.lighttask.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.db.Reminder
import com.skushnaryov.lighttask.lighttask.inflate
import kotlinx.android.synthetic.main.item_reminder.view.*

class ReminderRecyclerView(private val listener: OnReminderSwitchChange) : RecyclerView.Adapter<ReminderRecyclerView.ReminderHolder>() {
    var reminderList = emptyList<Reminder>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderHolder =
            ReminderHolder(parent.inflate(R.layout.item_reminder), listener)

    override fun getItemCount(): Int = reminderList.size

    override fun onBindViewHolder(holder: ReminderHolder, position: Int) = holder.bind(reminderList[position])


    class ReminderHolder(itemView: View, private val listener: OnReminderSwitchChange) : RecyclerView.ViewHolder(itemView) {
        fun bind(reminder: Reminder) {
            val timeString = "${itemView.context.getString(R.string.every)} ${reminder.time} ${reminder.timeType}"
            itemView.reminderName_textView.text = reminder.name
            itemView.reminderTime_textView.text = timeString

            itemView.reminderOn_switch.isChecked = reminder.isOn

            itemView.reminderOn_switch.setOnCheckedChangeListener { _, isChecked -> listener.onSwitchChecked(isChecked, reminder) }
        }
    }

    interface OnReminderSwitchChange {
        fun onSwitchChecked(isChecked: Boolean, reminder: Reminder)
    }
}