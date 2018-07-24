package com.skushnaryov.lighttask.lighttask.fragmnets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.adapters.TaskRecyclerView
import com.skushnaryov.lighttask.lighttask.db.Task
import com.skushnaryov.lighttask.lighttask.viewModels.TaskViewModel
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.android.synthetic.main.item_subtask.*
import kotlinx.android.synthetic.main.item_task.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import java.util.*

class TasksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_tasks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = TaskRecyclerView()
        rv_tasks.adapter = rv
        rv_tasks.layoutManager = LinearLayoutManager(context)
        val mv = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        mv.insert(Task(name = "Task",
                groupName = "Group",
                listOfSubtasks = listOf("one", "two", "three"),
                date = Calendar.getInstance().timeInMillis,
                currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))
        mv.allTasks.observe(this, Observer {
            rv.list = it
        })
    }
}