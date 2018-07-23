package com.skushnaryov.lighttask.lighttask.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.*
import com.skushnaryov.lighttask.lighttask.db.Task
import kotlinx.android.synthetic.main.item_task.view.*
import java.util.*

class TaskRecyclerView : RecyclerView.Adapter<TaskRecyclerView.TaskHolder>() {
    lateinit var list: List<Task>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder =
            TaskHolder(parent.inflate(R.layout.item_task))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TaskHolder, position: Int) = holder.bind(list[position])

    class TaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: Task) = with(itemView) {
            taskName_textView.text = task.name
            taskDate_textView.text = getStringDate(task.date)

            if (!task.groupName.isEmpty()) {
                taskGroup_textView.visible()
                taskGroup_textView.text = task.groupName
            }

            if (!task.listOfSubtasks.isEmpty()) {
                task_checkbox.invisible()
                taskPercent_textView.visible()

                taskArrow_textView.visible()
                taskArrow_textView.setOnClickListener {
                    if (rv_subtasks.isVisible) {
                        rv_subtasks.visible()
                        //TODO: animation?
                    } else {
                        rv_subtasks.gone()
                    }
                }

                rv_subtasks.adapter = SubtaskRecyclerView(task.listOfSubtasks)

            }
        }

        private fun getStringDate(date: Calendar) =
                "${date.get(Calendar.DAY_OF_MONTH)}.${date.get(Calendar.MONTH)}.${date.get(Calendar.YEAR)}"
    }
}