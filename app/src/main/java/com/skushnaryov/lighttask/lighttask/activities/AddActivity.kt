package com.skushnaryov.lighttask.lighttask.activities

import android.os.Bundle
import android.view.Menu
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.inflateMenu

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = inflateMenu(R.menu.add_activity_menu, menu)
}