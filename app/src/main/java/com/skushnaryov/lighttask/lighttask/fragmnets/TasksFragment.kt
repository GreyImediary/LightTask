package com.skushnaryov.lighttask.lighttask.fragmnets

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.AbstractThreadedSyncAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.skushnaryov.lighttask.lighttask.Constants
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.adapters.SubtaskRecyclerView
import com.skushnaryov.lighttask.lighttask.adapters.TaskRecyclerView
import com.skushnaryov.lighttask.lighttask.db.Task
import com.skushnaryov.lighttask.lighttask.gone
import com.skushnaryov.lighttask.lighttask.recievers.TaskReciever
import com.skushnaryov.lighttask.lighttask.recievers.TaskRemindReciever
import com.skushnaryov.lighttask.lighttask.viewModels.TaskViewModel
import com.skushnaryov.lighttask.lighttask.visible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_tasks.*
import org.jetbrains.anko.contentView
import java.util.*

class TasksFragment : Fragment(),
        TaskRecyclerView.OnTaskCheckboxListener,
        SubtaskRecyclerView.OnSubtaskCheckboxListener {

    private lateinit var viewModel: TaskViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_tasks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TaskRecyclerView(this, this)
        rv_tasks.layoutManager = LinearLayoutManager(context)
        viewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)

        val groupName = arguments?.getString("groupName") ?: " "
        if (groupName != " ") {
            if (groupName == getString(R.string.today)) {
                viewModel.getTodayTasks(Calendar.getInstance()[Calendar.DAY_OF_MONTH]).observe(this, Observer {
                    observeList(it, adapter, groupName)
                })
            }
            viewModel.getGroupTasks(groupName).observe(this, Observer {
                observeList(it, adapter, groupName)
            })
        } else {
            viewModel.allTasks.observe(this, Observer {
                observeList(it, adapter)
            })
        }
    }

    override fun onTaskCheckboxChange(task: Task) {
        val date = Calendar.getInstance().apply {
            timeInMillis = task.date
        }
        val text = "${task.name} at ${date[Calendar.HOUR_OF_DAY]}:${date[Calendar.MINUTE]}"

        viewModel.delete(task)
        deleteAlarm(task.id, task.name)
        deleteTaskRemind(task.id, text)
        Snackbar.make(activity?.contentView ?: return,
                R.string.taskCompleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.cancel) {
                    viewModel.insert(task)
                }.show()
    }

    override fun onSubtaskCheckboxChange(task: Task, isChecked: Boolean, subtask: String) {

        if (isChecked) {

            val index = task.listOfSubtasks.indexOf(subtask)
            task.listOfSubtasks.remove(subtask)
            viewModel.update(task)

            Snackbar.make(activity?.contentView ?: return,
                    getString(R.string.subtaskCompleted), Snackbar.LENGTH_LONG)
                    .setAction(R.string.cancel) {
                        task.listOfSubtasks.add(index, subtask)
                        viewModel.update(task)
                    }.show()
        }


        if (task.listOfSubtasks.isEmpty()) {
            Snackbar.make(activity?.contentView ?: return,
                    getString(R.string.compoundCompleted), Snackbar.LENGTH_LONG)
                    .setAction(R.string.yes) { viewModel.delete(task) }.show()
        }
    }

    private fun observeList(list: List<Task>, adapter: TaskRecyclerView, groupName: String = "") {
        adapter.list = list
        rv_tasks.adapter = adapter

        if (groupName != "") {
            activity?.appBar?.toolbar?.title = groupName
        }

        if (list.isEmpty()) {
            rv_tasks.gone()
            sleep_img.visible()
            noTask_textView.visible()
            summary_textView.visible()
        } else {
            rv_tasks.visible()
            sleep_img.gone()
            noTask_textView.gone()
            summary_textView.gone()
        }
    }

    private fun deleteAlarm(id: Int, name: String) {
        val alarmIntent = Intent(context, TaskReciever::class.java).apply {
            action = Constants.TASK_RECIEVER
            putExtra(Constants.EXTRAS_ID, id)
            putExtra(Constants.EXTRAS_NAME, name)
        }

        val alarmPending = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmPending)
    }

    private fun deleteTaskRemind(id: Int, text: String) {
        val taskRemindIntent = Intent(context, TaskRemindReciever::class.java).apply {
            action = Constants.TASK_REMINDER_RECIEVER
            putExtra(Constants.EXTRAS_ID, id)
            putExtra(Constants.EXTRAS_REMIND_TEXT, text)
        }

        val taskRemindPending = PendingIntent.getBroadcast(context, id, taskRemindIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(taskRemindPending)
    }
}