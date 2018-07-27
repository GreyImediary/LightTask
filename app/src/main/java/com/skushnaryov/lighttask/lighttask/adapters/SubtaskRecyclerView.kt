package com.skushnaryov.lighttask.lighttask.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.inflate
import kotlinx.android.synthetic.main.item_subtask.view.*

class SubtaskRecyclerView(private val onCheckedChangeListener: CompoundButton.OnCheckedChangeListener)
    : RecyclerView.Adapter<SubtaskRecyclerView.SubtaskHolder>() {

    var list = emptyList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskHolder =
            SubtaskHolder(parent.inflate(R.layout.item_subtask))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SubtaskHolder, position: Int) = holder.bind(list[position])

    inner class SubtaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String) {
            itemView.subtask_checkbox.setOnCheckedChangeListener(onCheckedChangeListener)
            itemView.subtaskName_textView.text = name
        }
    }
}