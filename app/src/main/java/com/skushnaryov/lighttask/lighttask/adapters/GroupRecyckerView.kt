package com.skushnaryov.lighttask.lighttask.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.db.Group
import com.skushnaryov.lighttask.lighttask.inflate
import kotlinx.android.synthetic.main.item_group.view.*

class GroupRecyckerView(private val listener: OnGroupItemClickListener) :  RecyclerView.Adapter<GroupRecyckerView.GroupViewHodler>(){
    var groupList: List<Group> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            GroupViewHodler(parent.inflate(R.layout.item_group), listener)

    override fun getItemCount() = groupList.size

    override fun onBindViewHolder(holder: GroupViewHodler, position: Int) = holder.bind(groupList[position].name)

    class GroupViewHodler(itemView: View, val listener: OnGroupItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String) {
            itemView.item_group_textView.text = name
            itemView.setOnClickListener {
                listener.onGoupClick(name)
            }
        }
    }

    interface OnGroupItemClickListener {
        fun onGoupClick(name: String)
    }
}