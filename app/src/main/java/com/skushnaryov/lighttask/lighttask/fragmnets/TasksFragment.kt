package com.skushnaryov.lighttask.lighttask.fragmnets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.adapters.SubtaskRecyclerView
import com.skushnaryov.lighttask.lighttask.adapters.TaskRecyclerView
import com.skushnaryov.lighttask.lighttask.db.Task
import com.skushnaryov.lighttask.lighttask.viewModels.TaskViewModel
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.contentView
import java.util.*
import kotlin.math.ceil

class TasksFragment : Fragment(), SubtaskRecyclerView.OnSubtaskCheckboxListener {

    private lateinit var viewModel: TaskViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_tasks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = TaskRecyclerView(this)
        rv_tasks.adapter = rv
        rv_tasks.layoutManager = LinearLayoutManager(context)
        viewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        viewModel.insert(Task(name = "Task",
                groupName = "Group",
                listOfSubtasks = mutableListOf("one", "two", "three", "four", "five", "six", "seven"),
                date = Calendar.getInstance().timeInMillis,
                currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))
        viewModel.allTasks.observe(this, Observer {
            rv.list = it
        })
    }

    override fun onCheckboxChange(task: Task, isChecked: Boolean, subtask: String) {
        val subtaskPercent = ceil(100F / task.listOfSubtasks.size).toInt()
        launch {
            var finalPercent: Int
            if (isChecked) {
                finalPercent = getCurrentPercent(task) + subtaskPercent
                viewModel.updatePercent(task.id, "$finalPercent%")

                val index = task.listOfSubtasks.indexOf(subtask)
                task.listOfSubtasks.remove(subtask)
                viewModel.update(task)

                Snackbar.make(activity?.contentView ?: return@launch,
                        getString(R.string.subtaskCompleted), Snackbar.LENGTH_LONG)
                        .setAction(R.string.cancel) {
                            launch { finalPercent = getCurrentPercent(task) - subtaskPercent
                                viewModel.updatePercent(task.id, "$finalPercent%") }
                            task.listOfSubtasks.add(index, subtask)
                            viewModel.update(task)
                        }.show()
            } else {

            }
        }

        launch {
            if (getCurrentPercent(task) < 0) {
                viewModel.updatePercent(task.id, "0%")
            }

            if (getCurrentPercent(task) > 100) {
                viewModel.updatePercent(task.id, "100%")
            }
        }

        launch {
            if (getCurrentPercent(task) == 100) {
                Snackbar.make(activity?.contentView ?: return@launch,
                        getString(R.string.compoundCompleted), Snackbar.LENGTH_LONG)
                        .setAction(R.string.yes) { viewModel.delete(task) }.show()
            }
        }
    }

    private fun getCurrentPercent(task: Task) = viewModel.getCurrentPercent(task.id).trim('%').toInt()
}