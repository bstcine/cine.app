package com.bstcine.h5

import android.app.Application
import java.util.*
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
        Config()[this] = object : Properties() {
            init {
                setProperty("user.token", token)
            }
        }

        this.login = true
    }

    fun logout() {
        Config().remove(this, "user.token")
        this.login = false
    }

    fun token(): String? {
        return Config()[this, "user.token"]
    }
}