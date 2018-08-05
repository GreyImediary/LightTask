package com.skushnaryov.lighttask.lighttask.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.skushnaryov.lighttask.lighttask.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val controller = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(nav_bottom, controller)
        NavigationUI.setupActionBarWithNavController(this, controller)

        fab.setOnClickListener {
            startActivity<AddActivity>()
        }

    }

    override fun onSupportNavigateUp(): Boolean =
            findNavController(R.id.nav_host_fragment).navigateUp()
}
