package com.skushnaryov.lighttask.lighttask.fragmnets

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skushnaryov.lighttask.lighttask.*
import com.skushnaryov.lighttask.lighttask.adapters.ReminderRecyclerView
import com.skushnaryov.lighttask.lighttask.db.Reminder
import com.skushnaryov.lighttask.lighttask.viewModels.ReminderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_reminder_create.view.*
import kotlinx.android.synthetic.main.fragment_reminders.*

class RemindersFragment : Fragment(), ReminderRecyclerView.OnReminderListener {
    private lateinit var reminderViewModel: ReminderViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ReminderRecyclerView(context!!, this)
        rv_reminders.layoutManager = LinearLayoutManager(context)
        rv_reminders.adapter = adapter

        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
        reminderViewModel.allReminders.observe(this, Observer {
            adapter.reminderList = it

            if (it.isEmpty()) {
                rv_reminders.gone()
                sleep_img.visible()
                noRemindes_textView.visible()
                summary_textView.visible()
            } else {
                rv_reminders.visible()
                sleep_img.gone()
                noRemindes_textView.gone()
                summary_textView.gone()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab) ?: return

        rv_reminders.onScrollListener { dy ->
            if (dy > 0 && fab.visibility == View.VISIBLE) {
                activity?.fab?.hide()
            } else if (dy < 0 && fab.visibility != View.VISIBLE) {
                activity?.fab?.show()
            }
        }
    }

    override fun onSwitchChecked(isChecked: Boolean, reminder: Reminder) {
        val reminderTime = getAlarmTime(reminder.timeType, reminder.time)

        if (reminderTime == -1L) {
            return
        }

        if (isChecked) {
            Constants.crtOrrmvRemindeNotification(context!!, reminder.id, reminder.name, reminderTime)
            reminderViewModel.updateIsOnById(reminder.id, true)
        } else {
            Constants.crtOrrmvRemindeNotification(context!!, reminder.id, reminder.name, reminderTime, true)
            reminderViewModel.updateIsOnById(reminder.id, false)
        }
    }

    override fun onPopupItemClick(itemId: Int, reminder: Reminder) = when (itemId) {
        R.id.action_delete -> {
            crtOrRmvAlarm(reminder, true)
            reminderViewModel.delete(reminder)
            true
        }
        R.id.action_change -> {
            changeReminderDialog(reminder)
            true
        }
        else -> false
    }

    private fun changeReminderDialog(reminder: Reminder) {
        val view = layoutInflater.inflate(R.layout.dialog_reminder_create, null)

        val spinnerAdapter = ArrayAdapter.createFromResource(context, R.array.timeArr,
                android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        view.reminderTimeType_spinner.adapter = spinnerAdapter
        view.reminderTimeType_spinner.setSelection(0)
        view.reminderName_edit_text.setText(reminder.name, TextView.BufferType.EDITABLE)
        view.reminderTime_edit_text.setText(reminder.time.toString(), TextView.BufferType.EDITABLE)
        when (reminder.timeType) {
            Constants.REMIND_MIN -> view.reminderTimeType_spinner.setSelection(0)
            Constants.REMIND_HOUR -> view.reminderTimeType_spinner.setSelection(1)
            Constants.REMIND_DAY -> view.reminderTimeType_spinner.setSelection(2)
        }

        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.changeRemidnerDialogTitle)
                .setView(view)
                .setPositiveButton(R.string.change) { _, _ ->  changeReminder(reminder, view)}
                .setNegativeButton(R.string.cancel) {dialogInterface, _ -> dialogInterface.cancel() }
        return builder.create().show()

    }

    private fun changeReminder(reminder: Reminder, view: View) {
        crtOrRmvAlarm(reminder, true)

        val remidnerName = view.reminderName_edit_text.text.toString()

        if (remidnerName.trim().isEmpty()) {
            view.reminderName_text_input.error = getString(R.string.reminderDialogError)
            return
        } else {
            view.reminderName_text_input.error = null
        }

        val time = view.reminderTime_edit_text.text.toString().toInt()
        val timeType = when (view.reminderTimeType_spinner.selectedItemId) {
            0L -> Constants.REMIND_MIN
            1L -> Constants.REMIND_HOUR
            2L -> Constants.REMIND_DAY
            else -> ""
        }

        val id = reminder.id

        val changeReminder = Reminder(id, remidnerName, time, timeType, reminder.isOn)

        reminderViewModel.update(changeReminder)
        if (changeReminder.isOn) {
            crtOrRmvAlarm(changeReminder)
        }
    }

    private fun crtOrRmvAlarm(reminder: Reminder, isRemoving: Boolean = false) {
        val reminderTime = getAlarmTime(reminder.timeType, reminder.time)

        if (reminderTime == -1L) {
            return
        }

        if (isRemoving) {
            Constants.crtOrrmvRemindeNotification(context!!, reminder.id, reminder.name, reminderTime, true)
        } else {
            Constants.crtOrrmvRemindeNotification(context!!, reminder.id, reminder.name, reminderTime)
        }
    }

    private fun getAlarmTime(timeType: String, time: Int) = when (timeType) {
        Constants.REMIND_MIN -> time.minute
        Constants.REMIND_HOUR -> time.hour
        Constants.REMIND_DAY -> time.day
        else -> -1L
    }
}