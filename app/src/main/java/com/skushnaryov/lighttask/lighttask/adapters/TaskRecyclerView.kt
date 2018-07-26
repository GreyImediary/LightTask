package com.skushnaryov.lighttask.lighttask.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.*
import com.skushnaryov.lighttask.lighttask.db.Task
import kotlinx.android.synthetic.main.item_task.view.*
import java.util.*
import kotlin.math.ceil

class TaskRecyclerView : RecyclerView.Adapter<TaskRecyclerView.TaskHolder>() {
    var list: List<Task> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder =
            TaskHolder(parent.inflate(R.layout.item_task))

    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TaskHolder, position: Int) = holder.bind(list[position])

    class TaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: Task) = with(itemView) {
            taskName_textView.text = task.name
            taskDate_textView.text = getStringDate(task.date)

            val innerAdapter = SubtaskRecyclerView(CompoundButton.OnCheckedChangeListener { _, onChecked ->
                val subtaskPercent = ceil(100F / task.listOfSubtasks.size).toInt()
                if (onChecked) {
                    val finalPercent = getCurrentPercent() + subtaskPercent
                    taskPercent_textView.text = context.getString(R.string.percentFormat, finalPercent)
                } else {
                    val finalPercent = getCurrentPercent() - subtaskPercent
                    taskPercent_textView.text = context.getString(R.string.percentFormat, finalPercent)
                }

                if (getCurrentPercent() > 100) {
                    taskPercent_textView.text = context.getString(R.string.percentFormat, 100)
                }

                if (getCurrentPercent() < 0) {
                    taskPercent_textView.text = context.getString(R.string.percentFormat, 0)
                }
            })

            if (!task.groupName.isEmpty()) {
                taskGroup_textView.visible()
                taskGroup_textView.text = task.groupName
            }

            if (!task.listOfSubtasks.isEmpty()) {
                innerAdapter.list = task.listOfSubtasks

                taskPercent_textView.visible()
                taskPercent_textView.text = "0%"
                task_checkbox.invisible()

                rv_subtasks.adapter = innerAdapter
                rv_subtasks.layoutManager = LinearLayoutManager(context)

                taskArrow_textView.visible()

                setOnClickListener {
                    if (!rv_subtasks.isVisible) {
                        rv_subtasks.visible()
                        fadeOutInAnimation(context, rv_subtasks)
                        taskArrow_textView.text = context.getString(R.string.subtasks_arrow_up)
                        fadeOutInAnimation(context, taskArrow_textView)
                    } else {
                        rv_subtasks.gone()
                        taskArrow_textView.text = context.getString(R.string.subtasks_arrow_down)
                        fadeOutInAnimation(context, taskArrow_textView)
                    }
                }
            }
        }

        private fun getCurrentPercent() = itemView.taskPercent_textView.text
                .trim('%')
                .toString().toInt()

        private fun fadeOutInAnimation(context: Context, view: View) {
            var anim = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            anim.reset()
            view.clearAnimation()
            view.startAnimation(anim)

            anim = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            anim.reset()
            view.clearAnimation()
            view.startAnimation(anim)
        }

        private fun getStringDate(time: Long): String {
            val date = Calendar.getInstance().also { it.timeInMillis = time }
            return "${date.get(Calendar.DAY_OF_MONTH)}." +
                    "${date.get(Calendar.MONTH)}." +
                    "${date.get(Calendar.YEAR)}\n" +
                    "${date.get(Calendar.HOUR_OF_DAY)}:${date.get(Calendar.MINUTE)}"
        }
    }
}