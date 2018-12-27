package com.bstcine.h5.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.bstcine.h5.CineApplication
import com.bstcine.h5.CineConfig
import com.bstcine.h5.R
import com.bstcine.h5.base.BaseWebActivity
import com.bstcine.h5.base.BaseWebFragment
import com.bstcine.h5.ui.csub.CSubFragment
import com.bstcine.h5.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView

    private var mCurrentPrimaryItem: Fragment? = null

    private var mNextItemId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            if (mCurrentPrimaryItem is CSubFragment) {
                (mCurrentPrimaryItem as CSubFragment).emitJs("android_call_h5_test", "joe")
            } else {
                val intent = Intent(this@MainActivity, BaseWebActivity::class.java)
                intent.putExtra("url", "http://soft.imtt.qq.com/browser/tes/feedback.html")
                startActivity(intent)
            }
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnLongClickListener {
            CineApplication.INSTANCE.logout()
            ToastUtils.showLong("logout success.")
            reloadFragment()
            true
        }

        navigation = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val itemId = item.itemId

            if ((itemId == R.id.action_learn || itemId == R.id.action_mine) && !CineApplication.INSTANCE.isLogin()) {
                mNextItemId = itemId
                ActivityUtils.startActivityForResult(this@MainActivity, LoginActivity::class.java, REQUEST_LOGIN)
                return@OnNavigationItemSelectedListener false
            }

            val mFragmentManager = supportFragmentManager
            val mCurTransaction = mFragmentManager.beginTransaction()

            var fragment = mFragmentManager.findFragmentByTag(itemId.toString())
            if (fragment != null) {
                mCurTransaction.show(fragment)

                //当用户点击 csub 页面时，调用 H5 的 reload 更新
                if (itemId == R.id.action_csub) (fragment as CSubFragment).emitJs("reload", false)
            } else {
                fragment = getFragmentByItemId(itemId)
                mCurTransaction.add(R.id.container, fragment!!, itemId.toString()).show(fragment)
            }

            if (mCurrentPrimaryItem != null && fragment !== mCurrentPrimaryItem) {
                mCurTransaction.hide(mCurrentPrimaryItem!!)
            }

            mCurTransaction.commitNowAllowingStateLoss()

            mNextItemId = null
            mCurrentPrimaryItem = fragment

            preloadFragment()

            true
        })
        navigation.selectedItemId = R.id.action_store
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState);解决重启Fragment重叠问题
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_LOGIN && resultCode == 1) {
            reloadFragment()
        }
    }

    /**
     * 预加载
     */
    private fun preloadFragment() {
        val mFragmentManager = supportFragmentManager
        val mCurTransaction = mFragmentManager.beginTransaction()

        var fragment = mFragmentManager.findFragmentByTag(R.id.action_csub.toString())
        if (fragment == null) {
            fragment = getFragmentByItemId(R.id.action_csub)
            mCurTransaction.add(R.id.container, fragment!!, R.id.action_csub.toString()).hide(fragment)
        }

        mCurTransaction.commitNowAllowingStateLoss()
    }

    /**
     * 重新加载
     */
    private fun reloadFragment() {
        val mFragmentManager = supportFragmentManager
        val mCurTransaction = mFragmentManager.beginTransaction()
        for (fragment in mFragmentManager.fragments) {
            mCurTransaction.remove(fragment)
        }
        mCurTransaction.commitNowAllowingStateLoss()

        mCurrentPrimaryItem = null
        navigation.selectedItemId = mNextItemId ?: R.id.action_store
    }

    private fun getFragmentByItemId(itemId: Int): Fragment? {
        val tabConfig = hashMapOf(R.id.action_store to CineConfig.H5_URL_STORE, R.id.action_mine to CineConfig.H5_URL_MINE, R.id.action_learn to CineConfig.H5_URL_LEARN)
        return if (itemId == R.id.action_csub) CSubFragment() else if (tabConfig[itemId] == null) null else BaseWebFragment.forUrl(tabConfig[itemId].toString())
    }

    companion object {
        private const val REQUEST_LOGIN = 10001
    }
}
