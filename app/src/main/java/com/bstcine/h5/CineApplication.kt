package com.bstcine.h5

import android.app.Application
import com.bstcine.h5.utils.PropUtil
import java.util.*
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

    fun change(){
        this.change = false
    }

    fun isLogin(): Boolean {
        return this.login
    }

    fun login(token: String) {
        PropUtil[this] = object : Properties() {
            init {
                setProperty("user.token", token)
            }
        }

        this.login = true
        this.change = true
    }

    fun logout() {
        PropUtil.remove(this, "user.token")
        this.login = false
        this.change = true
    }

    fun token(): String? {
        return PropUtil[this, "user.token"]
    }
}