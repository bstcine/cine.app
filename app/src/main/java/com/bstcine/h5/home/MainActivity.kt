package com.bstcine.h5.home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.bstcine.h5.CineApplication
import com.bstcine.h5.Config
import com.bstcine.h5.utils.PropUtil
import com.bstcine.h5.R
import com.bstcine.h5.login.LoginActivity
import com.bstcine.h5.web.WebFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView

    private var mCurrentPrimaryItem: Fragment? = null

    private var mNextItemId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        navigation = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val itemId = item.itemId

            if ((itemId == R.id.action_learn || itemId == R.id.action_mine) && !CineApplication.INSTANCE.isLogin()) {
                mNextItemId = itemId
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                return@OnNavigationItemSelectedListener false
            }

            val mFragmentManager = supportFragmentManager
            val mCurTransaction = mFragmentManager.beginTransaction()

            val name = makeFragmentName(itemId)
            var fragment = mFragmentManager.findFragmentByTag(name)
            if (fragment != null) {
                mCurTransaction.show(fragment)
            } else {
                fragment = getItem(itemId)
                mCurTransaction.add(R.id.container, fragment!!, name)
            }

            if (mCurrentPrimaryItem != null && fragment !== mCurrentPrimaryItem) {
                mCurTransaction.hide(mCurrentPrimaryItem!!)
            }

            mCurTransaction.commit()
            mCurrentPrimaryItem = fragment
            true
        })
        navigation.selectedItemId = R.id.action_store
    }

    override fun onResume() {
        super.onResume()
        changeHandle()
    }

    private fun makeFragmentName(id: Int): String {
        return "android:switcher:" + R.id.container + ":" + id
    }

    private fun getItem(itemId: Int): Fragment? {
        var selectedFragment: Fragment? = null
        when (itemId) {
            R.id.action_learn -> selectedFragment = WebFragment.newInstance(Config.LEARN_URL)
            R.id.action_store -> selectedFragment = WebFragment.newInstance(Config.STORE_URL)
            R.id.action_mine -> selectedFragment = WebFragment.newInstance(Config.MINE_URL)
            R.id.action_csub -> selectedFragment = WebFragment.newInstance(Config.CSUB_URL)
        }
        return selectedFragment
    }

    private fun removeFragment() {
        for (fragment in supportFragmentManager.fragments) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    private fun changeHandle() {
        if (!CineApplication.INSTANCE.isChange()) return

        removeFragment()
        navigation.selectedItemId = if (CineApplication.INSTANCE.isLogin()) mNextItemId
                ?: R.id.action_store else R.id.action_store

        CineApplication.INSTANCE.change()
    }
}
