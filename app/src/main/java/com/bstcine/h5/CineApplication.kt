package com.bstcine.h5

import android.app.Application
import com.blankj.utilcode.util.SPUtils
import kotlin.properties.Delegates

class CineApplication : Application() {

    companion object {
        var INSTANCE: CineApplication by Delegates.notNull()
    }

    private var login = false

    private var change = false

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        this.login = token() != null
    }

    fun isChange(): Boolean {
        return change
    }

    fun change() {
        this.change = false
    }

    fun isLogin(): Boolean {
        return this.login
    }

    fun login(token: String) {
        SPUtils.getInstance("auth").put("token", token)

        this.login = true
        this.change = true
    }

    fun logout() {
        SPUtils.getInstance("auth").remove("token")

        this.login = false
        this.change = true
    }

    fun token(): String? {
        return SPUtils.getInstance("auth").getString("token", null)
    }
}