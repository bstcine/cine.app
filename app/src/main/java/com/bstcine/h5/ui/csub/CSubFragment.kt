package com.bstcine.h5.ui.csub

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.webkit.JavascriptInterface
import android.widget.FrameLayout
import com.blankj.utilcode.util.ActivityUtils
import com.bstcine.h5.CineConfig
import com.bstcine.h5.CineJSInterface
import com.bstcine.h5.model.JsModel
import com.bstcine.h5.ui.MsgActivity
import com.bstcine.h5.ui.h5.H5Fragment
import com.bstcine.h5.widget.X5WebView
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

class CSubFragment : H5Fragment() {

    override fun init(refresh: SwipeRefreshLayout?, parent: FrameLayout?, webView: X5WebView?) {
        super.init(refresh, parent, webView)

        //当用户刷新 csub 页面时，调用 H5 的 reload 更新
        refresh?.setOnRefreshListener {
            webView?.emitJs("reload", true)
            Observable.timer(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(onNext = {
                        refresh.isRefreshing = false
                    })
        }
    }

    override fun addJavascriptInterface(webView: X5WebView?) {
        webView?.addJavascriptInterface(object : CineJSInterface() {
            @JavascriptInterface
            override fun openLessonPlayWindow(arg0: String) {
                val rs = Gson().fromJson(arg0, JsModel::class.java)
                activity?.runOnUiThread {
                    webView.emitJs(rs.callback, rs.data)

                    val bundle = Bundle()
                    bundle.putString("arg0", arg0)
                    ActivityUtils.startActivity(bundle, MsgActivity::class.java)
                }
            }
        }, "Android")
    }

    override fun loadUrl(webView: X5WebView?) {
        webView?.loadUrl(handleUrl(CineConfig.H5_URL_CSUB))
    }

}