package com.bstcine.h5

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ActivityUtils
import com.bstcine.h5.ui.BlankActivity
import com.bstcine.h5.ui.login.LoginActivity
import android.os.Bundle


open class CineJsNative {

    @JavascriptInterface
    fun login(arg0: String) {
        ActivityUtils.startActivity(LoginActivity::class.java)
    }

    @JavascriptInterface
    fun logout(arg0: String) {
        CineApplication.INSTANCE.logout()
    }

    @JavascriptInterface
    fun openLessonPlayWindow(arg0: String) {
        val bundle = Bundle()
        bundle.putString("arg0", arg0)
        ActivityUtils.startActivity(bundle, BlankActivity::class.java)
    }
}