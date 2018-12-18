package com.bstcine.h5.ui

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bstcine.h5.App
import com.bstcine.h5.R

class WebActivity : AppCompatActivity(), WebFragment.OnFragmentInteractionListener {

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val url = intent.getStringExtra("url")
        supportFragmentManager.beginTransaction()
                .add(R.id.fragContainer, WebFragment.newInstance(url))
                .commit()
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

    override fun onLogout() {
        App.instance.logout()
    }
}
