package com.bstcine.h5.base

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bstcine.h5.CineApplication
import com.bstcine.h5.CineJSInterface
import com.bstcine.h5.R
import com.bstcine.h5.widget.CWebView
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.sdk.WebViewClient
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

private const val ARG_HREF = "param_url"

open class BaseWebFragment : Fragment() {

    private var mHref: String? = null
    private var mRefresh: SwipeRefreshLayout? = null
    private var mWebView: CWebView? = null

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
        val view = inflater.inflate(R.layout.fragment_web, container, false)

        mRefresh = view.findViewById(R.id.refresh)
        mWebView = CWebView(context!!)

        initWebView(mWebView, mRefresh)
        addJavascriptInterface(mWebView)
        loadUrl(mWebView)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()

        mWebView?.destroy()
        mWebView = null
    }

    open fun initWebView(wv: CWebView?, refresh: SwipeRefreshLayout?) {
        refresh?.addView(wv)
        refresh?.setOnRefreshListener { wv?.reload() }

        wv?.webViewClient = object : WebViewClient() {

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
        wv?.webChromeClient = object : WebChromeClient() {

            private var mParent: ViewGroup? = null

            private var mCustomView: View? = null

            override fun onJsAlert(webView: WebView?, s: String?, s1: String?, jsResult: JsResult?): Boolean {
                val b = AlertDialog.Builder(context!!)
                b.setTitle("提示")
                b.setMessage(s1)
                b.setPositiveButton(android.R.string.ok) { dialog, which ->
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
                b.setPositiveButton(android.R.string.ok) { dialog, which ->
                    jsResult!!.confirm()
                    dialog.dismiss()
                }
                b.setNegativeButton(android.R.string.cancel) { dialog, which ->
                    jsResult!!.cancel()
                    dialog.dismiss()
                }
                b.setCancelable(false)
                b.create().show()
                return true
            }

            override fun onShowCustomView(view: View?, customViewCallback: IX5WebChromeClient.CustomViewCallback?) {
                super.onShowCustomView(view, customViewCallback)

                mParent = wv!!.parent.parent as ViewGroup
                (activity as AppCompatActivity).supportActionBar?.hide()

                view?.setBackgroundColor(Color.parseColor("#ffffff"))
                mParent?.addView(view)

                mCustomView = view
            }

            override fun onHideCustomView() {
                super.onHideCustomView()

                (activity as AppCompatActivity).supportActionBar?.show()

                if (mCustomView != null) {
                    mParent?.removeView(mCustomView)
                    mCustomView = null
                }
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                refresh?.isRefreshing = newProgress != 100
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    open fun addJavascriptInterface(wv: CWebView?) {
        wv?.addJavascriptInterface(CineJSInterface(wv), "Android")
    }

    open fun loadUrl(wv: CWebView?) {
        wv?.loadUrl(handleUrl(mHref!!))
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
