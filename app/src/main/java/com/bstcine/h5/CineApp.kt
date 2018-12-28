package com.bstcine.h5

import android.app.Application
import android.util.Log
import com.blankj.utilcode.util.SPUtils
import com.tencent.smtt.sdk.QbSdk
import kotlin.properties.Delegates

class CineApp : Application() {

    companion object {
        var INSTANCE: CineApp by Delegates.notNull()
    }

    private var mIsLogin = false

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        init()
    }

    private fun init(){
        //获取登录状态
        this.mIsLogin = token() != null

        //加载腾讯 X5 内核
        QbSdk.initX5Environment(applicationContext, object : QbSdk.PreInitCallback {

            override fun onViewInitFinished(arg0: Boolean) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is $arg0")
            }

            override fun onCoreInitFinished() {
            }
        })
    }

    fun isLogin(): Boolean {
        return this.mIsLogin
    }

    fun login(token: String) {
        SPUtils.getInstance("auth").put("token", token)
        this.mIsLogin = true
    }

    fun logout() {
        SPUtils.getInstance("auth").remove("token")
        this.mIsLogin = false
    }

    fun token(): String? {
        return SPUtils.getInstance("auth").getString("token", null)
    }
}