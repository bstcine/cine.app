package com.bstcine.h5.ui.h5

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.blankj.utilcode.util.ActivityUtils
import com.bstcine.h5.CineApp
import com.bstcine.h5.CineJSInvokeNative
import com.bstcine.h5.R
import com.bstcine.h5.widget.X5WebView
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.sdk.*

open class H5Fragment : Fragment() {

    private var mUrl: String? = null

    private var mViewParent: FrameLayout? = null
    private var mRefresh: SwipeRefreshLayout? = null
    private var mWebView: X5WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mUrl = it.getString(KEY_WEB_URL)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_h5, container, false)

        mRefresh = view.findViewById(R.id.refresh)
        mViewParent = view.findViewById(R.id.webView)
        mWebView = X5WebView(CineApp.INSTANCE)

        init(mRefresh, mViewParent, mWebView)
        addJavascriptInterface(mWebView)
        loadUrl(mWebView)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()

        mWebView?.destroy()
        mWebView = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    open fun init(refresh: SwipeRefreshLayout?, parent: FrameLayout?, webView: X5WebView?) {
        refresh?.setOnRefreshListener { webView?.reload() }

        parent!!.addView(webView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT))

        webView!!.setOnScrollChangedCallback(object : X5WebView.OnScrollChangedCallback {
            override fun onScroll(l: Int, t: Int) {
                refresh?.isEnabled = t == 0
            }
        })

        val webSetting = webView.settings
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(false)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSetting.setAppCachePath(context!!.getDir("appcache", 0).path)
        webSetting.databasePath = context!!.getDir("databases", 0).path
        webSetting.setGeolocationDatabasePath(context!!.getDir("geolocation", 0)
                .path)
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        webSetting.cacheMode = WebSettings.LOAD_DEFAULT

        // 设置允许加载混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.webViewClient = object : WebViewClient() {

            private var time: Long? = null

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (!url!!.contains("bstcine.com")) return true

                ActivityUtils.startActivity(Bundle().apply { putString("url", url) }, H5Activity::class.java)
                return true
            }

            override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
                p1!!.proceed()
            }

            override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                super.onPageStarted(p0, p1, p2)
                time = System.currentTimeMillis()
            }

            override fun onPageFinished(p0: WebView?, p1: String?) {
                super.onPageFinished(p0, p1)
                Log.d(this@H5Fragment::class.java.simpleName, "page time: ${System.currentTimeMillis() - time!!}")
                (activity as AppCompatActivity).supportActionBar?.title = p0?.title
            }
        }

        webView.webChromeClient = object : WebChromeClient() {

            private var mCustomView: View? = null

            override fun onJsAlert(webView: WebView?, s: String?, s1: String?, jsResult: JsResult?): Boolean {
                val b = AlertDialog.Builder(context!!)
                b.setTitle("提示")
                b.setMessage(s1)
                b.setPositiveButton(android.R.string.ok) { dialog, _ ->
                    jsResult!!.confirm()
                    dialog.dismiss()
                }
                b.setCancelable(false)
                b.create().show()
                return true
            }

            override fun onJsConfirm(webView: WebView?, s: String?, s1: String?, jsResult: JsResult?): Boolean {
                val b = AlertDialog.Builder(context!!)
                b.setTitle("提示")
                b.setMessage(s1)
                b.setPositiveButton(android.R.string.ok) { dialog, _ ->
                    jsResult!!.confirm()
                    dialog.dismiss()
                }
                b.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    jsResult!!.cancel()
                    dialog.dismiss()
                }
                b.setCancelable(false)
                b.create().show()
                return true
            }

            override fun onShowCustomView(view: View?, customViewCallback: IX5WebChromeClient.CustomViewCallback?) {
                super.onShowCustomView(view, customViewCallback)

                (activity as AppCompatActivity).supportActionBar?.hide()

                view?.setBackgroundColor(Color.parseColor("#ffffff"))
                parent.addView(view)

                mCustomView = view
            }

            override fun onHideCustomView() {
                super.onHideCustomView()

                (activity as AppCompatActivity).supportActionBar?.show()

                if (mCustomView != null) {
                    parent.removeView(mCustomView)
                    mCustomView = null
                }
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                refresh?.isRefreshing = newProgress != 100
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    open fun addJavascriptInterface(webView: X5WebView?) {
        webView?.addJavascriptInterface(CineJSInvokeNative(), "Android")
    }

    open fun loadUrl(webView: X5WebView?) {
        val time = System.currentTimeMillis()
        webView?.loadUrl(handleUrl(mUrl!!))
        Log.d("main", "cost time: ${System.currentTimeMillis() - time}")
    }

    fun handleUrl(url: String): String {
        var tempUrl = if (url.contains("?")) {
            "$url&sitecode=cine.web.android.kotlin"
        } else {
            "$url?sitecode=cine.web.android.kotlin"
        }

        if (CineApp.INSTANCE.isLogin()) tempUrl += "&token=" + CineApp.INSTANCE.token()

        return tempUrl
    }

    fun emitJs(name: String, arg: Any) {
        mWebView?.emitJs(name, arg)
    }

    companion object {
        private const val KEY_WEB_URL = "web_url"

        @JvmStatic
        fun forUrl(url: String) =
                H5Fragment().apply {
                    arguments = Bundle().apply {
                        putString(KEY_WEB_URL, url)
                    }
                }
    }
}
