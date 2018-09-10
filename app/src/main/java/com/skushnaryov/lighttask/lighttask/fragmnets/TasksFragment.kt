package com.skushnaryov.lighttask.lighttask.fragmnets

import android.app.AlarmManager
import android.app.PendingIntent
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.skushnaryov.lighttask.lighttask.*
import com.skushnaryov.lighttask.lighttask.Constants.CHANGE_CURRENT_DAY
import com.skushnaryov.lighttask.lighttask.Constants.CHANGE_DATE
import com.skushnaryov.lighttask.lighttask.Constants.CHANGE_GROUP
import com.skushnaryov.lighttask.lighttask.Constants.CHANGE_ID
import com.skushnaryov.lighttask.lighttask.Constants.CHANGE_NAME
import com.skushnaryov.lighttask.lighttask.Constants.CHANGE_REMIND_DATE
import com.skushnaryov.lighttask.lighttask.Constants.CHANGE_REMIND_ID
import com.skushnaryov.lighttask.lighttask.Constants.CHANGE_SUBTASKS
import com.skushnaryov.lighttask.lighttask.activities.AddActivity
import com.skushnaryov.lighttask.lighttask.adapters.SubtaskRecyclerView
import com.skushnaryov.lighttask.lighttask.adapters.TaskRecyclerView
import com.skushnaryov.lighttask.lighttask.db.Task
import com.skushnaryov.lighttask.lighttask.recievers.TaskReciever
import com.skushnaryov.lighttask.lighttask.recievers.TaskRemindReciever
import com.skushnaryov.lighttask.lighttask.viewModels.TaskViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_tasks.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.contentView
import java.util.*

class TasksFragment : Fragment(),
        TaskRecyclerView.OnTaskItemClickListener,
        SubtaskRecyclerView.OnSubtaskCheckboxListener {

    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskRecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_tasks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TaskRecyclerView(this, this)
        rv_tasks.layoutManager = LinearLayoutManager(context)



        viewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)

        val groupName = arguments?.getString("groupName") ?: " "
        if (groupName != " ") {
            if (groupName == getString(R.string.today)) {
                val todayDate = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                viewModel.getTodayTasks(todayDate).observe(this, Observer {
                    observeList(it, groupName)
                })
            } else {
                viewModel.getGroupTasks(groupName).observe(this, Observer {
                    observeList(it, groupName)
                })
            }
        } else {
            viewModel.allTasks.observe(this, Observer {
                observeList(it)
            })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab) ?: return

        rv_tasks.onScrollListener { dy ->
            if (dy > 0 && fab.visibility == View.VISIBLE) {
                activity?.fab?.hide()
            } else if (dy < 0 && fab.visibility != View.VISIBLE) {
                activity?.fab?.show()
            }
        }
    }

    override fun onTaskCheckboxChange(task: Task) {
        val date = Calendar.getInstance().apply {
            timeInMillis = task.date
        }
        val text = "${task.name} at ${date[Calendar.HOUR_OF_DAY]}:${date[Calendar.MINUTE]}"

        viewModel.delete(task)
        deleteAlarm(task.id, task.name)
        deleteTaskRemind(task.remindId, text)
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

    override fun onPopupItemClick(itemId: Int, task: Task): Boolean = when (itemId) {
        R.id.action_delete -> {
            val date = Calendar.getInstance().apply {
                timeInMillis = task.date
            }
            val text = "${task.name} at ${date[Calendar.HOUR_OF_DAY]}:${date[Calendar.MINUTE]}"

            viewModel.delete(task)
            deleteAlarm(task.id, task.name)
            deleteTaskRemind(task.id, text)
            true
        }
        R.id.action_change -> {
            var subtasks = ""
            if (!task.listOfSubtasks.isEmpty()) {
                task.listOfSubtasks.forEach { subtasks += "$it," }

            }
            val bundle = bundleOf(
                    CHANGE_ID to task.id,
                    CHANGE_REMIND_ID to task.remindId,
                    CHANGE_NAME to task.name,
                    CHANGE_DATE to task.date,
                    CHANGE_REMIND_DATE to task.taskRemindDate,
                    CHANGE_CURRENT_DAY to task.currentDay,
                    CHANGE_SUBTASKS to subtasks,
                    CHANGE_GROUP to task.groupName
            )
            val intent = Intent(context, AddActivity::class.java).apply {
                putExtras(bundle)
            }
            startActivity(intent)
            true
        }
        else -> false
    }

    private fun observeList(list: List<Task>, groupName: String = "") {
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
            putExtras(bundleOf(
                    Constants.EXTRAS_ID to id,
                    Constants.EXTRAS_NAME to name
            ))
        }

        val alarmPending = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmPending)
    }

    private fun deleteTaskRemind(id: Int, text: String) {
        val taskRemindIntent = Intent(context, TaskRemindReciever::class.java).apply {
            action = Constants.TASK_REMIND_RECIEVER
            putExtras(bundleOf(
                    Constants.EXTRAS_ID to id,
                    Constants.EXTRAS_REMIND_TEXT to text
            ))
        }

        val taskRemindPending = PendingIntent.getBroadcast(context, id, taskRemindIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(taskRemindPending)
    }
}