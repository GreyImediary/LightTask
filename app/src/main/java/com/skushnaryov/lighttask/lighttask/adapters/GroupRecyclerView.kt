package com.skushnaryov.lighttask.lighttask.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.db.Group
import com.skushnaryov.lighttask.lighttask.inflate
import kotlinx.android.synthetic.main.item_group.view.*

class GroupRecyclerView(private val listener: OnGroupItemClickListener) :  RecyclerView.Adapter<GroupRecyclerView.GroupViewHodler>(){
    var groupList: List<Group> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            GroupViewHodler(parent.inflate(R.layout.item_group), listener)

    override fun getItemCount() = groupList.size

    override fun onBindViewHolder(holder: GroupViewHodler, position: Int) = holder.bind(groupList[position])

    class GroupViewHodler(itemView: View, private val listener: OnGroupItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(group: Group) {
            val name = group.name
            itemView.item_group_textView.text = name
            itemView.setOnClickListener {
                listener.onGroupClick(name)
            }

            itemView.setOnLongClickListener {
                val popup = PopupMenu(itemView.context, itemView)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener {
                    listener.onPopupItemClick(it.itemId, group)
                }
                popup.show()
                true
            }
        }
    }

    interface OnGroupItemClickListener {
        fun onGroupClick(name: String)
        fun onPopupItemClick(itemId: Int, group: Group): Boolean
    }
}