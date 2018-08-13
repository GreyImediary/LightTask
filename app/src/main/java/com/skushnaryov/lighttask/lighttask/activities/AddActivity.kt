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
import com.skushnaryov.lighttask.lighttask.dialogs.TimeDialog
import com.skushnaryov.lighttask.lighttask.inflateMenu
import com.skushnaryov.lighttask.lighttask.recievers.TaskReciever
import com.skushnaryov.lighttask.lighttask.viewModels.GroupViewModel
import com.skushnaryov.lighttask.lighttask.viewModels.TaskViewModel
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.dialog_add_group_layout.view.*
import java.util.*

class AddActivity : AppCompatActivity(),
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, GroupDialog.OnGroupDialogItemClickListener {

    private lateinit var taskViewModel: TaskViewModel

    private lateinit var groupViewModel: GroupViewModel
    private lateinit var groupList: List<Group>

    private var name = ""
    private var groupName = ""
    private val date = Calendar.getInstance()
    private var subtasks: MutableList<String> = arrayListOf()

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

    override fun onTimeSet(dialog: TimePicker?, intHour: Int, intMinute: Int) {
        date.set(Calendar.HOUR_OF_DAY, intHour)
        date.set(Calendar.MINUTE, intMinute)
        date.set(Calendar.SECOND, 0)

        val year = date.get(Calendar.YEAR)
        val rowMonth = date.get(Calendar.MONTH)
        val rowDay = date.get(Calendar.DAY_OF_MONTH)

        val day = if (rowDay < 10) "0$rowDay" else "$rowDay"
        val month = if (rowMonth < 10) "0$rowMonth" else "$rowMonth"
        val hour = if (intHour < 10) "0$intHour" else "$intHour"
        val minute = if (intMinute < 10) "0$intMinute" else "$intMinute"

        val dateString = "$day.$month.$year-$hour:$minute"

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

    private fun taskCreated() {
        if (checkNamAndDate()) {
            return
        }

        val id = Random().nextInt(Int.MAX_VALUE)

        checkSubtasks()
        createAlarmNotification(id, name)

        val isCompound = !subtasks.isEmpty()

        val task = Task(id,
                name,
                date.timeInMillis,
                date.get(Calendar.DAY_OF_MONTH),
                subtasks,
                isCompound,
                groupName)
        taskViewModel.insert(task)
        finish()
    }

    private fun checkNamAndDate(): Boolean {
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

    private fun checkSubtasks() {
        val rowSubtaskString = subtask_edit_text.text ?: return
        if (rowSubtaskString.isEmpty()) {
            return
        }

        subtasks = rowSubtaskString.split(",")
                .map { it.trimStart() }
                .map { it.trimEnd() }.toMutableList()
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
}