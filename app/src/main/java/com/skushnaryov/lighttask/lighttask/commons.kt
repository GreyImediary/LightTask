package com.skushnaryov.lighttask.lighttask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(resId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(resId, this, attachToRoot)