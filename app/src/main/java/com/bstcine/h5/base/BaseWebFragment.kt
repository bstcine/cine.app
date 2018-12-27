package com.bstcine.h5.base

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import com.bstcine.h5.CineApplication
import com.bstcine.h5.CineJSInterface
import com.bstcine.h5.R
import com.bstcine.h5.widget.X5WebView
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.sdk.*

private const val ARG_HREF = "param_url"

open class BaseWebFragment : Fragment() {

    private var mHref: String? = null

    private var mViewParent: FrameLayout? = null
    private var mRefresh: SwipeRefreshLayout? = null
    private var mWebView: X5WebView? = null

    companion object {
        @JvmStatic
        fun newInstance(href: String) =
                BaseWebFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_HREF, href)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mHref = it.getString(ARG_HREF)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.base_fragment_web, container, false)

        mRefresh = view.findViewById(R.id.refresh)
        mViewParent = view.findViewById(R.id.webView)
        mWebView = X5WebView(CineApplication.INSTANCE)

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

        val webSetting = webView!!.settings
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

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (!url!!.contains("bstcine.com")) return true

                val intent = Intent(activity, BaseWebActivity::class.java)
                intent.putExtra("url", url)
                startActivity(intent)
                return true
            }

            override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
                p1!!.proceed()
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
        webView?.addJavascriptInterface(CineJSInterface(webView), "Android")
    }

    open fun loadUrl(webView: X5WebView?) {
        val time = System.currentTimeMillis()
        webView?.loadUrl(handleUrl(mHref!!))
        Log.d("main", "cost time: ${System.currentTimeMillis() - time}")
    }

    fun handleUrl(url: String): String {
        var tempUrl = if (url.contains("?")) {
            "$url&sitecode=cine.web.android.kotlin"
        } else {
            "$url?sitecode=cine.web.android.kotlin"
        }

        if (CineApplication.INSTANCE.isLogin()) tempUrl += "&token=" + CineApplication.INSTANCE.token()

        return tempUrl
    }

    fun emitJs(name: String, arg: Any) {
        mWebView?.emitJs(name, arg)
    }

}
