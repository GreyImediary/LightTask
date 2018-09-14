package com.skushnaryov.lighttask.lighttask.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.db.entities.Task
import com.skushnaryov.lighttask.lighttask.inflate
import kotlinx.android.synthetic.main.item_subtask.view.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange

class SubtaskRecyclerView(private val onSubtaskCheckboxListener: OnSubtaskCheckboxListener,
                          private val rootTask: Task)
    : RecyclerView.Adapter<SubtaskRecyclerView.SubtaskHolder>() {

    var list = emptyList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskHolder =
            SubtaskHolder(parent.inflate(R.layout.item_subtask))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SubtaskHolder, position: Int) = holder.bind(list[position])

    inner class SubtaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String) {
            itemView.subtask_checkbox.onCheckedChange { _, isChecked ->
                onSubtaskCheckboxListener.onSubtaskCheckboxChange(rootTask, isChecked, name)
            }
            itemView.subtaskName_textView.text = name
        }
    }

    interface OnSubtaskCheckboxListener {
        fun onSubtaskCheckboxChange(task: Task, isChecked: Boolean, subtask: String)
    }
}