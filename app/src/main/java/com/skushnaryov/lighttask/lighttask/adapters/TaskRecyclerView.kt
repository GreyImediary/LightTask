package com.skushnaryov.lighttask.lighttask.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.*
import com.skushnaryov.lighttask.lighttask.db.Task
import kotlinx.android.synthetic.main.item_task.view.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import java.util.*

class TaskRecyclerView(private val subtaskCheckboxListener: SubtaskRecyclerView.OnSubtaskCheckboxListener,
                       private val taskCheckboxListener: OnTaskCheckboxListener) :
        RecyclerView.Adapter<TaskRecyclerView.TaskHolder>() {

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

    inner class TaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: Task) = with(itemView) {
            taskName_textView.text = task.name
            taskDate_textView.text = getStringDate(task.date)

            task_checkbox.onCheckedChange { _, _ ->
                taskCheckboxListener.onTaskCheckboxChange(task)
            }

            val innerAdapter = SubtaskRecyclerView(subtaskCheckboxListener, task)

            if (!task.groupName.isEmpty()) {
                taskGroup_textView.visible()
                taskGroup_textView.text = task.groupName
            }

            if (task.isCompound) {
                innerAdapter.list = task.listOfSubtasks

                rv_subtasks.adapter = innerAdapter
                rv_subtasks.layoutManager = LinearLayoutManager(context)

                if (task.listOfSubtasks.isEmpty()) {
                    taskArrow_textView.invisible()
                } else {
                    taskArrow_textView.visible()

                    mainPart.setOnClickListener {
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
        }

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

            val rowDay = date.get(Calendar.DAY_OF_MONTH)
            val day = if (rowDay < 10) "0$rowDay" else "$rowDay"

            val rowMonth = date.get(Calendar.MONTH)
            val month = if (rowMonth < 10) "0$rowMonth" else "$rowMonth"

            val rowHour = date.get(Calendar.HOUR_OF_DAY)
            val hour = if (rowHour < 10) "0$rowHour" else "$rowHour"

            val rowMinute = date.get(Calendar.MINUTE)
            val minute = if (rowMinute < 10) "0$rowMinute" else "$rowMinute"
            return "$day." +
                    "$month." +
                    "${date.get(Calendar.YEAR)}\n" +
                    "$hour:$minute"
        }
    }

    interface OnTaskCheckboxListener {
        fun onTaskCheckboxChange(task: Task)
    }
}