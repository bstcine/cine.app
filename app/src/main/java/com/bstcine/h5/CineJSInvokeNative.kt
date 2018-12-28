package com.bstcine.h5

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ActivityUtils
import com.bstcine.h5.ui.login.LoginActivity

open class CineJSInvokeNative {

    /* ----------------- common ----------------- */

    @JavascriptInterface
    fun login(arg0: String) {
        ActivityUtils.startActivity(LoginActivity::class.java)
    }

    @JavascriptInterface
    fun logout(arg0: String) {
        CineApp.INSTANCE.logout()
    }

    /* ----------------- common ----------------- */

    @JavascriptInterface
    open fun openLessonPlayWindow(arg0: String) {
    }
}