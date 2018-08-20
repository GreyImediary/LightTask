package com.skushnaryov.lighttask.lighttask.fragmnets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.adapters.GroupRecyckerView
import com.skushnaryov.lighttask.lighttask.db.Group
import com.skushnaryov.lighttask.lighttask.toast
import com.skushnaryov.lighttask.lighttask.viewModels.GroupViewModel
import kotlinx.android.synthetic.main.fragment_groups.*

class GroupsFragment : Fragment(), GroupRecyckerView.OnGroupItemClickListener {
    override fun onPopupDeleteClick(group: Group) {
        if (group.name == getString(R.string.today)) {
            context?.toast(getString(R.string.deleting_today))
        } else {
            groupViewModel.delete(group)
        }
    }

    lateinit var groupViewModel: GroupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GroupRecyckerView(this)
        rv_groups.layoutManager = LinearLayoutManager(context)
        rv_groups.adapter = adapter

        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        groupViewModel.allGroups.observe(this, Observer {
            adapter.groupList = it
        })
    }

    override fun onGoupClick(name: String) {
        val bundle = bundleOf("groupName" to name)
        view?.findNavController()?.navigate(R.id.group_action, bundle)
    }

}