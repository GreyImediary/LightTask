package com.skushnaryov.lighttask.lighttask.adapters

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.inflate
import kotlinx.android.synthetic.main.item_subtask.view.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange

class SubtaskRecyclerView : RecyclerView.Adapter<SubtaskRecyclerView.SubtaskHolder>() {
    var list = emptyList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskHolder =
            SubtaskHolder(parent.inflate(R.layout.item_subtask))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SubtaskHolder, position: Int) = holder.bind(list[position])

    class SubtaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String) {
            itemView.subtask_checkbox.onCheckedChange { _, isChecked ->
                if (isChecked) {
                    Log.i("CHECKBOX", "checked")
                    //TODO: make checkBox work
                } else {
                    Log.i("CHECKBOX", "unchecked")
                }
            }
            itemView.subtaskName_textView.text = name
        }
    }
}