package com.skushnaryov.lighttask.lighttask.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.db.entities.Group
import com.skushnaryov.lighttask.lighttask.db.entities.Task
import com.skushnaryov.lighttask.lighttask.dialogs.DateDialog
import com.skushnaryov.lighttask.lighttask.dialogs.GroupDialog
import com.skushnaryov.lighttask.lighttask.dialogs.TaskRemindDialog
import com.skushnaryov.lighttask.lighttask.dialogs.TimeDialog
import com.skushnaryov.lighttask.lighttask.inflateMenu
import com.skushnaryov.lighttask.lighttask.toStringTime
import com.skushnaryov.lighttask.lighttask.toast
import com.skushnaryov.lighttask.lighttask.utils.Constants
import com.skushnaryov.lighttask.lighttask.utils.Constants.REMIND_DAY
import com.skushnaryov.lighttask.lighttask.utils.Constants.REMIND_HOUR
import com.skushnaryov.lighttask.lighttask.utils.Constants.REMIND_MIN
import com.skushnaryov.lighttask.lighttask.utils.NotificationUtils
import com.skushnaryov.lighttask.lighttask.viewModels.GroupViewModel
import com.skushnaryov.lighttask.lighttask.viewModels.TaskViewModel
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.dialog_add_group_layout.view.*
import kotlinx.android.synthetic.main.dialog_own_task_remind.view.*
import java.util.*
import java.util.Calendar.*

class AddActivity : AppCompatActivity(),
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        GroupDialog.OnGroupDialogItemClickListener,
        TaskRemindDialog.OnItemRemindClickListener {

    private lateinit var taskViewModel: TaskViewModel

    private lateinit var groupViewModel: GroupViewModel
    private lateinit var groupList: List<Group>

    private var name = ""
    private var group = ""
    private var id = 0
    private var remindId = 0
    private val date = Calendar.getInstance()
    private var remindTaskDate = 0L
    private val currentDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private var subtasks: MutableList<String> = arrayListOf()

    private var remindNumber = 0
    private var remindType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)

        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        groupViewModel.allGroups.observe(this, Observer {
            groupList = it.subList(1, it.size)
        })


        val extras = intent.extras
        if (extras == null) {
            id = Random().nextInt(Int.MAX_VALUE)
            remindId = Random().nextInt(Int.MAX_VALUE)
        } else {
            title = getString(R.string.titleChangeTask)
            id = extras.getInt(Constants.CHANGE_ID)
            remindId = extras.getInt(Constants.CHANGE_REMIND_ID)

            name = extras.getString(Constants.CHANGE_NAME) ?: ""
            name_edit_text.setText(name, TextView.BufferType.EDITABLE)

            date.timeInMillis = extras.getLong(Constants.CHANGE_DATE)
            val dateString = getFullStringDate(date[DAY_OF_MONTH], date[MONTH], date[YEAR], date[HOUR_OF_DAY], date[MINUTE])
            date_edit_text.setText(dateString, TextView.BufferType.EDITABLE)

            remindTaskDate = extras.getLong(Constants.CHANGE_REMIND_DATE)
            val remindDate = Calendar.getInstance().apply { timeInMillis = remindTaskDate }
            val remindDateString = getFullStringDate(remindDate[DAY_OF_MONTH], remindDate[MONTH],
                    remindDate[YEAR], remindDate[HOUR_OF_DAY], remindDate[MINUTE])

            if (remindDateString != dateString) {
                remind_edit_text.setText(remindDateString, TextView.BufferType.EDITABLE)
            }

            subtask_edit_text.setText(extras.getString(Constants.CHANGE_SUBTASKS))
            group_edit_text.setText(extras.getString(Constants.CHANGE_GROUP))
        }

        date_edit_text.setOnClickListener {
            DateDialog().show(supportFragmentManager, "Date dialog")
        }

        group_edit_text.setOnClickListener { _ ->
            val groupDialog = GroupDialog()
            groupDialog.clickListener = this
            groupDialog.groups = groupList.map { it.name }
            groupDialog.show(supportFragmentManager, "Group dialog")
        }

        remind_edit_text.setOnClickListener {
            val remindDialog = TaskRemindDialog()
            remindDialog.clickListener = this
            remindDialog.show(supportFragmentManager, "TaskRemind dialog")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = inflateMenu(R.menu.add_activity_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
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
        date[Calendar.HOUR_OF_DAY] = hour
        date[Calendar.MINUTE] = minute
        date[Calendar.SECOND] = 0

        val dateString = getFullStringDate(date[DAY_OF_MONTH], date[MONTH], date[YEAR], date[HOUR_OF_DAY], date[MINUTE])

        date_edit_text.setText(dateString, TextView.BufferType.EDITABLE)

        if (date < Calendar.getInstance()) {
            date_text_input.error = getString(R.string.wrongDateError)
        } else {
            date_text_input.error = null
        }
    }

    override fun onGroupItemClick(position: Int) {
        group = groupList[position].name
        group_edit_text.setText(group, TextView.BufferType.EDITABLE)
    }

    override fun onGroupCreateClick(view: View) {
        val groupName = view.add_group_edit_text.text.toString()
        if (groupName.trim().isEmpty()) {
            toast(getString(R.string.wrongGroupName))
        } else {
            group = groupName
            groupViewModel.insert(Group(name = groupName))
            group_edit_text.setText(groupName, TextView.BufferType.EDITABLE)
        }
    }

    override fun onRemindItemClick(position: Int) {
        when (position) {
            0 -> setRemindValues(5, REMIND_MIN)

            1 -> setRemindValues(10, REMIND_MIN)

            2 -> setRemindValues(30, REMIND_MIN)

            3 -> setRemindValues(1, REMIND_HOUR)

            4 -> setRemindValues(1, REMIND_DAY)

            5 -> createOwnRemindDialog()

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


        checkSubtasks()

        if (!remind_edit_text.text!!.isEmpty()) {
            val fullDate = getFullStringDate(date[DAY_OF_MONTH], date[MONTH], date[YEAR], date[HOUR_OF_DAY], date[MINUTE])
            NotificationUtils.crtOrRmvTaskRemindNotification(this,
                    remindId, "$name at $fullDate", remindTaskDate)
        }

        val isCompound = !subtasks.isEmpty()

        val task = Task(id,
                remindId,
                name,
                date.timeInMillis,
                currentDay,
                remindTaskDate,
                subtasks,
                isCompound,
                group)
        taskViewModel.insert(task)

        NotificationUtils.crtOrRmvTaskNotification(this, id, name, date.timeInMillis)

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

        val remindDate = date.clone() as Calendar

        when (remindType) {
            REMIND_MIN -> remindDate[Calendar.MINUTE] = date[Calendar.MINUTE] - remindNumber
            REMIND_HOUR -> remindDate[HOUR_OF_DAY] = date[Calendar.HOUR_OF_DAY] - remindNumber
            REMIND_DAY -> remindDate[Calendar.DAY_OF_MONTH] = date[Calendar.DAY_OF_MONTH] - remindNumber
        }

        if (remindDate <= Calendar.getInstance() || remindDate >= date) {
            remind_text_input.error = getString(R.string.remindError)
            return
        }

        val day = remindDate[DAY_OF_MONTH]
        val month = remindDate[MONTH]
        val year = remindDate[YEAR]
        val hour = remindDate[HOUR_OF_DAY]
        val minute = remindDate[MINUTE]

        val stringDate = getFullStringDate(day, month, year, hour, minute)
        remind_edit_text.setText(stringDate, TextView.BufferType.EDITABLE)
        remindTaskDate = remindDate.timeInMillis
    }

    private fun checkSubtasks() {
        val rowSubtaskString = subtask_edit_text.text ?: return
        if (rowSubtaskString.isEmpty()) {
            return
        }

        subtasks = rowSubtaskString.split(",")
                .asSequence()
                .map { it.trimStart() }
                .map { it.trimEnd() }.toMutableList()
    }

    @SuppressLint("InflateParams")
    private fun createOwnRemindDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_own_task_remind, null)
        val adapter = ArrayAdapter.createFromResource(this, R.array.timeArr,
                android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        view.own_remind_spinner.adapter = adapter

        AlertDialog.Builder(this)
                .setTitle(getString(R.string.ownTaskRemindTitle))
                .setView(view)
                .setPositiveButton(R.string.ok) { _, _ -> onRemindButtonClick(view) }
                .setNegativeButton(R.string.cancel) { dialogInterface, _ -> dialogInterface.cancel() }
                .create().show()
    }

    private fun onRemindButtonClick(view: View) {
        val number = view.own_remind_edit_text.text.toString().toInt()
        var type = ""
        val itemId = view.own_remind_spinner.selectedItemId

        when (itemId) {
            0L -> type = REMIND_MIN
            1L -> type = REMIND_HOUR
            2L -> type = REMIND_DAY
        }

        setRemindValues(number, type)
        createTaskRemind()
    }


    private fun getFullStringDate(day: Int, month: Int, year: Int, hour: Int, minute: Int) =
            "${day.toStringTime()}.${month.toStringTime()}.${year.toStringTime()}-${hour.toStringTime()}:${minute.toStringTime()}"
}