package com.bstcine.h5.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.google.gson.Gson

import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

class X5WebView : WebView {
    private val client = object : WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    constructor(arg0: Context, arg1: AttributeSet) : super(arg0, arg1) {
        this.webViewClient = client
        // this.setWebChromeClient(chromeClient);
        // WebStorage webStorage = WebStorage.getINSTANCE();
        initWebViewSettings()
        this.view.isClickable = true
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSettings() {
        val webSetting = this.settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(true)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
    }

    constructor(context: Context) : super(context) {
        setBackgroundColor(85621)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mOnScrollChangedCallback?.onScroll(l, t)
    }

    override fun destroy() {
        clearFormData()
        clearHistory()
        clearCache(true)

        loadUrl("about:blank")
        onPause()
        removeAllViews()

        super.destroy()
    }

    fun emitJs(name: String?, arg: Any?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("window._cine_listener.emit('$name',${Gson().toJson(arg)})", null)
        } else {
            loadUrl("javascript:window._cine_listener.emit('$name',${Gson().toJson(arg)})")
        }
    }

    interface OnScrollChangedCallback {
        fun onScroll(l: Int, t: Int)
    }

    private var mOnScrollChangedCallback: OnScrollChangedCallback? = null

    fun setOnScrollChangedCallback(onScrollChangedCallback: OnScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback
    }

}
