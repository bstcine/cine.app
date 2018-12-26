package com.bstcine.h5.ui.csub

import android.os.Bundle
import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ActivityUtils
import com.bstcine.h5.CineConfig
import com.bstcine.h5.CineJSInterface
import com.bstcine.h5.data.JsModel
import com.bstcine.h5.ui.MsgActivity
import com.bstcine.h5.base.BaseWebFragment
import com.bstcine.h5.widget.CWebView
import com.google.gson.Gson

class CSubFragment : BaseWebFragment() {

    override fun addJavascriptInterface(wv: CWebView?) {
        wv?.addJavascriptInterface(object : CineJSInterface(wv) {
            @JavascriptInterface
            override fun openLessonPlayWindow(arg0: String) {
                val rs = Gson().fromJson(arg0, JsModel::class.java)
                activity?.runOnUiThread {
                    wv.emitJs(rs.callback, rs.data)

                    val bundle = Bundle()
                    bundle.putString("arg0", arg0)
                    ActivityUtils.startActivity(bundle, MsgActivity::class.java)
                }
            }
        }, "Android")
    }

    override fun loadUrl(wv: CWebView?) {
        wv?.loadUrl(handleUrl(CineConfig.H5_URL_CSUB))
    }

}