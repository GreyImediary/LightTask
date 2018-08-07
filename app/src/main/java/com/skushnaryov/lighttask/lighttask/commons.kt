package com.skushnaryov.lighttask.lighttask

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity

fun ViewGroup.inflate(resId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(resId, this, attachToRoot)

fun View.visible() { this.visibility = View.VISIBLE }
fun View.invisible() { this.visibility = View.INVISIBLE }
fun View.gone() { this.visibility = View.GONE }


fun AppCompatActivity.inflateMenu(@MenuRes resId: Int, menu: Menu): Boolean {
    menuInflater.inflate(resId, menu)
    return true
}