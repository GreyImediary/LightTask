package com.skushnaryov.lighttask.lighttask.fragmnets

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.adapters.GroupRecyclerView
import com.skushnaryov.lighttask.lighttask.db.Group
import com.skushnaryov.lighttask.lighttask.toast
import com.skushnaryov.lighttask.lighttask.viewModels.GroupViewModel
import kotlinx.android.synthetic.main.dialog_add_group_layout.view.*
import kotlinx.android.synthetic.main.fragment_groups.*

class GroupsFragment : Fragment(), GroupRecyclerView.OnGroupItemClickListener {

    private lateinit var groupViewModel: GroupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GroupRecyclerView(this)
        rv_groups.layoutManager = LinearLayoutManager(context)
        rv_groups.adapter = adapter

        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        groupViewModel.allGroups.observe(this, Observer {
            adapter.groupList = it
        })
    }

    override fun onGroupClick(name: String) {
        val bundle = bundleOf("groupName" to name)
        view?.findNavController()?.navigate(R.id.group_action, bundle)
    }

    override fun onPopupItemClick(itemId: Int ,group: Group) = when (itemId) {
        R.id.action_change -> {
            if (group.name == getString(R.string.today)) {
                context?.toast(getString(R.string.changingToday))
            } else {
                createChangeGroupDialog(group)
            }
            true
        }
        R.id.action_delete -> {
            if (group.name == getString(R.string.today)) {
                context?.toast(getString(R.string.deletingToday))
            } else {
                groupViewModel.delete(group)
                groupViewModel.deleteTaskGroupName(group.name)
            }
            true
        }
        else -> false
    }

    private fun createChangeGroupDialog(group: Group) {
        val view = layoutInflater.inflate(R.layout.dialog_add_group_layout, null)
        view.add_group_edit_text.setText(group.name, TextView.BufferType.EDITABLE)

        AlertDialog.Builder(activity)
                .setTitle(R.string.changeGroupDialogTitle)
                .setView(view)
                .setPositiveButton(R.string.change) { _, _ ->
                    val newName = view.add_group_edit_text.text.toString()

                    if (newName.trim().isEmpty()) {
                        context?.toast(getString(R.string.wrongGroupName))
                    } else {
                        val updatedGroup = Group(group.id, newName)
                        groupViewModel.update(updatedGroup)
                        groupViewModel.updateTaskGroupName(group.name, newName)
                    }
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, _ -> dialogInterface.cancel() }
                .create().show()
    }
}