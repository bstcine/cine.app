package com.bstcine.h5.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import com.bstcine.h5.App
import com.bstcine.h5.R

private const val ARG_HREF = "param_url"

class WebFragment : Fragment() {
    private var mHref: String? = null

    private var mWebContainer: FrameLayout? = null
    private var mWebView: WebView? = null

    companion object {
        @JvmStatic
        fun newInstance(href: String) =
                WebFragment().apply {
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

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_web, container, false)

        mWebContainer = view.findViewById(R.id.webContainer)
        mWebView = WebView(activity!!.applicationContext)
        mWebContainer?.addView(mWebView)

        // 设置允许加载混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView!!.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.settings.allowFileAccess = true
        mWebView!!.settings.domStorageEnabled = true
        mWebView!!.settings.setAppCacheEnabled(true)
        mWebView!!.addJavascriptInterface(object {
            @JavascriptInterface
            fun login() {
                startActivity(Intent(activity, LoginActivity::class.java))
            }

            @JavascriptInterface
            fun logout() {
                App.instance.logout()
            }

        }, "Android")
        mWebView!!.webViewClient = object : WebViewClient() {
            /**
             * 防止加载网页时调起系统浏览器
             */
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (!url.contains("bstcine.com")) return true

                if (url.contains("bstcine.com/auth/signin")) {
                    App.instance.logout()
                    return true
                }

                val intent = Intent(activity, WebActivity::class.java)
                intent.putExtra("url", url)
                startActivity(intent)
                return true
            }

            /**
             * 允许所有SSL证书
             */
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler!!.proceed()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // 写入 token、sitecode 到 H5 LocalStorage
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    view!!.evaluateJavascript("window.localStorage.setItem('token','" + App.instance.token() + "');", null)
                    view.evaluateJavascript("window.localStorage.setItem('sitecode','cine.web.android.kotlin');", null)
                } else {
                    view!!.loadUrl("javascript:localStorage.setItem('token','" + App.instance.token() + "');")
                    view.loadUrl("javascript:localStorage.setItem('sitecode','cine.web.android.kotlin');")
                }
            }
        }
        mWebView!!.loadUrl(this.mHref!!)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i("WebFragment", "onDestroy")

        mWebContainer!!.removeAllViews()

        mWebView?.clearHistory()

        mWebView?.clearCache(true)

        mWebView?.loadUrl("about:blank")

        mWebView?.onPause()
        mWebView?.removeAllViews()

        mWebView?.destroy()

        mWebView = null
    }
}
