package com.skushnaryov.lighttask.lighttask.activities

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.skushnaryov.lighttask.lighttask.Constants
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.db.Group
import com.skushnaryov.lighttask.lighttask.db.Task
import com.skushnaryov.lighttask.lighttask.dialogs.DateDialog
import com.skushnaryov.lighttask.lighttask.dialogs.GroupDialog
import com.skushnaryov.lighttask.lighttask.dialogs.RemindDialog
import com.skushnaryov.lighttask.lighttask.dialogs.TimeDialog
import com.skushnaryov.lighttask.lighttask.inflateMenu
import com.skushnaryov.lighttask.lighttask.recievers.TaskReciever
import com.skushnaryov.lighttask.lighttask.recievers.TaskRemindReciever
import com.skushnaryov.lighttask.lighttask.toStringTime
import com.skushnaryov.lighttask.lighttask.viewModels.GroupViewModel
import com.skushnaryov.lighttask.lighttask.viewModels.TaskViewModel
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.dialog_add_group_layout.view.*
import java.util.*
import java.util.Calendar.*

class AddActivity : AppCompatActivity(),
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        GroupDialog.OnGroupDialogItemClickListener,
        RemindDialog.OnItemRemindClickListener {

    companion object {
        const val REMIND_MIN = "min"
        const val REMIND_HOUR = "hour"
        const val REMIND_DAY = "day"
    }

    private lateinit var taskViewModel: TaskViewModel

    private lateinit var groupViewModel: GroupViewModel
    private lateinit var groupList: List<Group>

    private var name = ""
    private var groupName = ""
    private val date = Calendar.getInstance()
    private var remindTaskDate = Calendar.getInstance()
    private var subtasks: MutableList<String> = arrayListOf()

    private var remindNumber = 0
    private var remindType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)

        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        groupViewModel.allGroups.observe(this, Observer {
            groupList = it
        })

        date_edit_text.setOnClickListener {
            DateDialog().show(supportFragmentManager, "Date dialog")
        }

        group_edit_text.setOnClickListener {
            val groupDialog = GroupDialog()
            groupDialog.clickListener = this
            groupDialog.groups = groupList.map { it.name }
            groupDialog.show(supportFragmentManager, "Group dialog")
        }

        remind_edit_text.setOnClickListener {
            val remindDialog = RemindDialog()
            remindDialog.clickListener = this
            remindDialog.show(supportFragmentManager, "TaskRemind dialog")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = inflateMenu(R.menu.add_activity_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.action_add -> {
            taskCreated()
            true
        }
        else -> false
    }

    override fun onDateSet(dialog: DatePicker?, year: Int, month: Int, day: Int) {
        date.set(year, month, day)
        TimeDialog().show(supportFragmentManager, "Time dialog")
    }

    override fun onTimeSet(dialog: TimePicker?, hour: Int, minute: Int) {
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)
        date.set(Calendar.SECOND, 0)

        val dateString = getFullStringDate(date[DAY_OF_MONTH], date[MONTH], date[YEAR], date[HOUR_OF_DAY], date[MINUTE])

        date_edit_text.setText(dateString, TextView.BufferType.EDITABLE)

        if (date < Calendar.getInstance()) {
            date_text_input.error = getString(R.string.wrongDateError)
        } else {
            date_text_input.error = null
        }
    }

    override fun onGroupItemClick(position: Int) {
        groupName = groupList[position].name
        group_edit_text.setText(groupName, TextView.BufferType.EDITABLE)
    }

    override fun onGroupCreateClick(view: View) {
        val groupName = view.add_group_edit_text.text.toString()
        groupViewModel.insert(Group(name = groupName))
        group_edit_text.setText(groupName, TextView.BufferType.EDITABLE)
    }

    override fun onRemindItemClick(position: Int) {
        when(position) {
            0 -> setRemindValues(5, REMIND_MIN)

            1 -> setRemindValues(10, REMIND_MIN)

            2 -> setRemindValues(30, REMIND_MIN)

            3 -> setRemindValues(1, REMIND_HOUR)

            4 -> setRemindValues(1, REMIND_DAY)

            else -> {
                remindNumber = 0
                remindType = ""
            }
        }

        createTaskRemind()
    }

    private fun taskCreated() {
        if (checkNameAndDate()) {
            return
        }

        val id = Random().nextInt(Int.MAX_VALUE)
        val remindId = Random().nextInt(Int.MAX_VALUE)

        checkSubtasks()
        createAlarmNotification(id, name)

        if (!remind_edit_text.text!!.isEmpty()) {
            val fullDate = getFullStringDate(date[DAY_OF_MONTH], date[MONTH], date[YEAR], date[HOUR_OF_DAY], date[MINUTE])
            createTaskReminNotification(remindId, "$name at $fullDate", remindTaskDate.timeInMillis)
        }

        val isCompound = !subtasks.isEmpty()

        val task = Task(id,
                name,
                date.timeInMillis,
                remindTaskDate.timeInMillis,
                date.get(Calendar.DAY_OF_MONTH),
                subtasks,
                isCompound,
                groupName)
        taskViewModel.insert(task)
        finish()
    }

    private fun checkNameAndDate(): Boolean {
        val isEmptyName = if (name_edit_text.text!!.trim().isEmpty()) {
            name_text_input.error = getString(R.string.emptyNameError)
            true
        } else {
            name_text_input.error = null
            name = name_edit_text.text?.toString() ?: return false
            false
        }

        val isEmptyDate = if (name_edit_text.text!!.isEmpty()) {
            date_text_input.error = getString(R.string.wrongDateError)
            true
        } else {
            date_text_input.error = null
            false
        }

        val isWrongDay = if (date < Calendar.getInstance()) {
            date_text_input.error = getString(R.string.wrongDateError)
            true
        } else {
            date_text_input.error = null
            false
        }

        return isEmptyName || isEmptyDate || isWrongDay
    }

    private fun setRemindValues(number: Int, type: String) {
        remindNumber = number
        remindType = type
    }

    private fun createTaskRemind() {

        if (remindNumber == 0 && remindType == "") {
            return
        }

        remindTaskDate = date.clone() as Calendar

        when (remindType) {
            REMIND_MIN -> remindTaskDate.set(Calendar.MINUTE, date[Calendar.MINUTE] - remindNumber)
            REMIND_HOUR -> remindTaskDate.set(Calendar.HOUR_OF_DAY, date[Calendar.HOUR_OF_DAY] - remindNumber)
            REMIND_DAY -> remindTaskDate.set(Calendar.DAY_OF_MONTH, date[Calendar.DAY_OF_MONTH] - remindNumber)
        }

        if (remindTaskDate <= Calendar.getInstance() || remindTaskDate >= date) {
            remind_text_input.error = getString(R.string.remindError)
            return
        }

        val day = remindTaskDate[DAY_OF_MONTH]
        val month = remindTaskDate[MONTH]
        val year = remindTaskDate[YEAR]
        val hour = remindTaskDate[HOUR_OF_DAY]
        val minute = remindTaskDate[MINUTE]

        val stringDate = getFullStringDate(day, month, year, hour, minute)
        remind_edit_text.setText(stringDate, TextView.BufferType.EDITABLE)
    }

    private fun checkSubtasks() {
        val rowSubtaskString = subtask_edit_text.text ?: return
        if (rowSubtaskString.isEmpty()) {
            return
        }

        subtasks = rowSubtaskString.split(",")
                .map { it.trimStart() }
                .map { it.trimEnd() }.toMutableList()
    }

    private fun createTaskReminNotification(id: Int, text: String, date: Long) {
        val taskRemindIntent = Intent(this, TaskRemindReciever::class.java).apply {
            action = Constants.TASK_REMINDER_RECIEVER
            putExtra(Constants.EXTRAS_ID, id)
            putExtra(Constants.EXTRAS_REMIND_TEXT, text)
        }

        val taskRemindPending = PendingIntent.getBroadcast(this, id, taskRemindIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, date, taskRemindPending)
    }

    private fun createAlarmNotification(id: Int, name: String) {
        val alarmIntent = Intent(this, TaskReciever::class.java).apply {
            action = Constants.TASK_RECIEVER
            putExtra(Constants.EXTRAS_ID, id)
            putExtra(Constants.EXTRAS_NAME, name)
        }

        val alarmPending = PendingIntent.getBroadcast(this, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, date.timeInMillis, alarmPending)

    }

    private fun getFullStringDate(day: Int, month: Int, year: Int, hour: Int, minute: Int) =
            "${day.toStringTime()}.${month.toStringTime()}.${year.toStringTime()}-${hour.toStringTime()}:${minute.toStringTime()}"
}