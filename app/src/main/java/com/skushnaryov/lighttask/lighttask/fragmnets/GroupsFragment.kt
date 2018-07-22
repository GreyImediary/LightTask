package com.skushnaryov.lighttask.lighttask.fragmnets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.skushnaryov.lighttask.lighttask.R

class GroupsFragment :Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }
}