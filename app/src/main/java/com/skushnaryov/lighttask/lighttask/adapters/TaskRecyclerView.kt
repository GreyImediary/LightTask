package com.skushnaryov.lighttask.lighttask.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.*
import com.skushnaryov.lighttask.lighttask.db.entities.Task
import kotlinx.android.synthetic.main.item_task.view.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import java.util.*
import java.util.Calendar.*

class TaskRecyclerView(private val subtaskCheckboxListener: SubtaskRecyclerView.OnSubtaskCheckboxListener,
                       private val taskItemClickListener: OnTaskItemClickListener) :
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
            if (task.date != 0L) {
                taskDate_textView.text = getStringDate(task.date)
            }

            task_checkbox.onCheckedChange { _, _ ->
                taskItemClickListener.onTaskCheckboxChange(task)
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

            mainPart.setOnLongClickListener {
                val popup = PopupMenu(it.context, it)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener {
                    taskItemClickListener.onPopupItemClick(it.itemId, task)
                }
                popup.show()
                return@setOnLongClickListener true
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

            val day = date[DAY_OF_MONTH].toStringTime()
            val month = (date[MONTH] + 1).toStringTime()
            val year = date[YEAR].toStringTime()
            val hour = date[HOUR_OF_DAY].toStringTime()
            val minute = date[MINUTE].toStringTime()

            return "$day.$month.$year\n$hour:$minute"
        }
    }

    interface OnTaskItemClickListener {
        fun onTaskCheckboxChange(task: Task)
        fun onPopupItemClick(itemId: Int, task: Task): Boolean
    }
}