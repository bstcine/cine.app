package com.bstcine.h5

import android.app.Application
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.bstcine.h5.ui.home.MainActivity
import kotlin.properties.Delegates

class CineApplication : Application() {

    companion object {
        var INSTANCE: CineApplication by Delegates.notNull()
    }

    private var login = false

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        this.login = token() != null
    }

    fun isLogin(): Boolean {
        return this.login
    }

    fun login(token: String) {
        SPUtils.getInstance("auth").put("token", token)
        this.login = true
    }

    fun logout() {
        SPUtils.getInstance("auth").remove("token")
        this.login = false
    }

    fun token(): String? {
        return SPUtils.getInstance("auth").getString("token", null)
    }
}