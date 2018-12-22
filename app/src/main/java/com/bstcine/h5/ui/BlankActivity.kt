package com.bstcine.h5.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.bstcine.h5.CineApplication
import com.bstcine.h5.R

class BlankActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val msg: TextView = findViewById(R.id.msg)
        msg.text = intent?.extras?.getString("arg0")

        val logout: Button = findViewById(R.id.logout)
        logout.setOnClickListener {
            CineApplication.INSTANCE.logout()
            onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
