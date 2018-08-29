package com.skushnaryov.lighttask.lighttask

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

fun ViewGroup.inflate(resId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(resId, this, attachToRoot)

fun View.visible() { this.visibility = View.VISIBLE }
fun View.invisible() { this.visibility = View.INVISIBLE }
fun View.gone() { this.visibility = View.GONE }


fun AppCompatActivity.inflateMenu(@MenuRes resId: Int, menu: Menu): Boolean {
    menuInflater.inflate(resId, menu)
    return true
}

fun RecyclerView.onScrollListener(f: (dy: Int) -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            f(dy)
        }
    })
}

fun Int.toStringTime() = if (this < 10) "0$this" else "$this"

fun Context.toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

val Int.minute: Long
    get() = this * 60000L
val Int.hour: Long
    get() = this.minute * 60
val Int.day: Long
    get() = this.hour * 24