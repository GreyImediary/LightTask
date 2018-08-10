package com.skushnaryov.lighttask.lighttask.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.db.Group
import com.skushnaryov.lighttask.lighttask.dialogs.DateDialog
import com.skushnaryov.lighttask.lighttask.dialogs.GroupDialog
import com.skushnaryov.lighttask.lighttask.dialogs.TimeDialog
import com.skushnaryov.lighttask.lighttask.inflateMenu
import com.skushnaryov.lighttask.lighttask.viewModels.GroupViewModel
import kotlinx.android.synthetic.main.activity_add.*
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.dialog_add_group_layout.view.*
import java.util.*

class AddActivity : AppCompatActivity(),
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, GroupDialog.OnGroupDialogItemClickListener {

    private lateinit var groupViewModel: GroupViewModel
    private lateinit var groupList: List<Group>

    private var name = ""
    private val date = Calendar.getInstance()
    private var groupName = ""
    private var subtasks = emptyList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

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

    override fun onTimeSet(dialog: TimePicker?, hour: Int, minute: Int) {
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)

        val year = date.get(Calendar.YEAR)
        val rowMonth = date.get(Calendar.MONTH)
        val rowDay = date.get(Calendar.DAY_OF_MONTH)

        val day = if (rowDay < 10) "0$rowDay" else "$rowDay"
        val month = if (rowMonth < 10) "0$rowMonth" else "$rowMonth"
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

        checkSubtasks()
    }

    private fun checkNamAndDate(): Boolean {
        val isEmptyName = if (name_edit_text.text!!.isEmpty()) {
            name_text_input.error = getString(R.string.emptyNameError)
            true
        } else {
            name_text_input.error = null
            name = name_edit_text.text?.toString() ?: return false
            false
        }

        val isEmptyDate = if (name_edit_text.text!!.isEmpty()) {
            date_text_input.error = getString(R.string.emptyDateError)
            true
        } else {
            date_text_input.error = null
            false
        }

        return isEmptyName || isEmptyDate
    }

    private fun checkSubtasks() {
        val rowSubtaskString = subtask_edit_text.text ?: return
        if (rowSubtaskString.isEmpty()) {
            return
        }

        subtasks = rowSubtaskString.split(",")
                .map { it.trimStart() }
                .map { it.trimEnd() }

        Log.i("subt", subtasks.toString())
    }
}