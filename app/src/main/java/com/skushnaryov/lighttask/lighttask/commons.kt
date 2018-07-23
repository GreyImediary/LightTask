package com.skushnaryov.lighttask.lighttask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(resId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(resId, this, attachToRoot)

fun View.visible() { this.visibility = View.VISIBLE }
fun View.invisible() { this.visibility = View.INVISIBLE }
fun View.gone() { this.visibility = View.GONE }
