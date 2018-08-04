package com.skushnaryov.lighttask.lighttask.fragmnets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.adapters.SubtaskRecyclerView
import com.skushnaryov.lighttask.lighttask.adapters.TaskRecyclerView
import com.skushnaryov.lighttask.lighttask.db.Task
import com.skushnaryov.lighttask.lighttask.gone
import com.skushnaryov.lighttask.lighttask.viewModels.TaskViewModel
import com.skushnaryov.lighttask.lighttask.visible
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
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

        val rv = TaskRecyclerView(this, this)
        rv_tasks.layoutManager = LinearLayoutManager(context)
        viewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        viewModel.insert(stubFun()[0])
        viewModel.insert(stubFun()[1])
        viewModel.allTasks.observe(this, Observer {
            rv.list = it
            rv_tasks.adapter = rv

            if (it.isEmpty()) {
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
        })
    }

    override fun onTaskCheckboxChange(task: Task) {
        viewModel.delete(task)
        Snackbar.make(activity?.contentView ?: return,
                R.string.taskCompleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.cancel) {
                    viewModel.insert(task)
                }.show()
    }

    override fun onSubtaskCheckboxChange(task: Task, isChecked: Boolean, subtask: String) {
        launch {
            if (isChecked) {

                val index = task.listOfSubtasks.indexOf(subtask)
                task.listOfSubtasks.remove(subtask)
                viewModel.update(task)

                Snackbar.make(activity?.contentView ?: return@launch,
                        getString(R.string.subtaskCompleted), Snackbar.LENGTH_LONG)
                        .setAction(R.string.cancel) {
                            task.listOfSubtasks.add(index, subtask)
                            viewModel.update(task)
                        }.show()
            }
        }


        if (task.listOfSubtasks.isEmpty()) {
            Snackbar.make(activity?.contentView ?: return,
                    getString(R.string.compoundCompleted), Snackbar.LENGTH_LONG)
                    .setAction(R.string.yes) { viewModel.delete(task) }.show()
        }
    }

    private fun stubFun(): MutableList<Task> {
        val task = Task(name = "Simple",
                groupName = "Group",
                date = Calendar.getInstance().timeInMillis,
                listOfSubtasks = mutableListOf("1", "2", "3", "4", "5", "6", "7"),
                isCompound = false,
                currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        val task2 = Task(name = "Compound",
                groupName = "Group",
                date = Calendar.getInstance().timeInMillis,
                listOfSubtasks = mutableListOf("1", "2", "3", "4", "5", "6", "7"),
                isCompound = true,
                currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        return mutableListOf(task, task2)
    }
}