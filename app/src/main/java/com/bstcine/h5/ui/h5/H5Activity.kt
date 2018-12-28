package com.bstcine.h5.ui.h5

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bstcine.h5.CineConfig
import com.bstcine.h5.R

class H5Activity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h5)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val url = intent.extras?.getString("url") ?: CineConfig.H5_URL_STORE
        supportFragmentManager.beginTransaction()
                .add(R.id.fragContainer, H5Fragment.forUrl(url))
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
}
