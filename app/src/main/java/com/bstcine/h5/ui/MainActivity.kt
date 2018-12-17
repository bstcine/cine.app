package com.bstcine.h5.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.bstcine.h5.App
import com.bstcine.h5.Config
import com.bstcine.h5.R

class MainActivity : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        navigation = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            //校验登录
            if ((item.itemId == R.id.action_learn || item.itemId == R.id.action_mine) && !App.instance.isLogin()) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                return@OnNavigationItemSelectedListener false
            }

            val fragmentTransaction = supportFragmentManager.beginTransaction()

            var learnFragment = supportFragmentManager.findFragmentById(R.id.action_learn)
            var storeFragment = supportFragmentManager.findFragmentById(R.id.action_store)
            var mineFragment = supportFragmentManager.findFragmentById(R.id.action_mine)

            if (learnFragment != null) fragmentTransaction.hide(learnFragment)
            if (storeFragment != null) fragmentTransaction.hide(storeFragment)
            if (mineFragment != null) fragmentTransaction.hide(mineFragment)

            when (item.itemId) {
                R.id.action_learn -> {
                    if (learnFragment == null) {
                        learnFragment = WebFragment.newInstance(Config.LEARN_URL)
                        fragmentTransaction.add(R.id.container, learnFragment)
                    }
                    fragmentTransaction.show(learnFragment).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_store -> {
                    if (storeFragment == null) {
                        storeFragment = WebFragment.newInstance(Config.STORE_URL)
                        fragmentTransaction.add(R.id.container, storeFragment)
                    }
                    fragmentTransaction.show(storeFragment).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_mine -> {
                    if (mineFragment == null) {
                        mineFragment = WebFragment.newInstance(Config.MINE_URL)
                        fragmentTransaction.add(R.id.container, mineFragment)
                    }
                    fragmentTransaction.show(mineFragment).commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
        navigation.selectedItemId = R.id.action_store
    }
}
