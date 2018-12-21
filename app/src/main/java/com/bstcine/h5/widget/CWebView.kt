package com.bstcine.h5.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout

import com.bstcine.h5.R
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.sdk.CookieSyncManager
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * Created by itwangxiang on 2018/1/10.
 */

class CWebView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.webViewStyle) : WebView(mContext, attrs, defStyleAttr) {

    private var mProgressBar: ProgressBar? = null

    private var mSRL: SwipeRefreshLayout? = null

    private val client = object : WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    init {
        this.webViewClient = client

        initWebViewSettings()
        this.view.isClickable = true
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSettings() {
        val webSetting = this.settings
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(false)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(false)
        webSetting.setAppCacheEnabled(true)
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSetting.setAppCachePath(this.context.getDir("appcache", 0).path)
        webSetting.databasePath = this.context.getDir("databases", 0).path
        webSetting.setGeolocationDatabasePath(this.context.getDir("geolocation", 0)
                .path)
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        CookieSyncManager.createInstance(this.context)
        CookieSyncManager.getInstance().sync()

        //设置进度条
        mProgressBar = ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal)
        mProgressBar!!.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 6)
        val drawable = ContextCompat.getDrawable(mContext, R.drawable.cwebview_progress)
        mProgressBar!!.progressDrawable = drawable
        addView(mProgressBar)

        //设置WebChromeClient
        webChromeClient = object : WebChromeClient() {

            private var mParent: ViewGroup? = null

            private var mCustomView: View? = null

            private var mActivity: AppCompatActivity? = null

            override fun onJsAlert(webView: WebView?, s: String?, s1: String?, jsResult: JsResult?): Boolean {
                val b = AlertDialog.Builder((this@CWebView.parent as ViewGroup).context)
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
                val b = AlertDialog.Builder((this@CWebView.parent as ViewGroup).context)
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

                mParent = this@CWebView.parent.parent as ViewGroup
                mActivity = mParent?.context as AppCompatActivity
                mActivity?.supportActionBar?.hide()

                view?.setBackgroundColor(Color.parseColor("#ffffff"))
                mParent?.addView(view)

                mCustomView = view
            }

            override fun onHideCustomView() {
                super.onHideCustomView()

                mActivity?.supportActionBar?.show()

                if (mCustomView != null) {
                    mParent?.removeView(mCustomView)
                    mCustomView = null
                }
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (mProgressBar != null) {
                    if (newProgress == 100) {
                        mProgressBar!!.visibility = View.GONE
                        if (mSRL != null) mSRL!!.isRefreshing = false
                    } else {
                        if (mProgressBar!!.visibility == View.GONE)
                            mProgressBar!!.visibility = View.VISIBLE
                        mProgressBar!!.progress = newProgress
                    }
                }

                super.onProgressChanged(view, newProgress)
            }
        }
    }

    fun setSwipeRefreshLayout(srl: SwipeRefreshLayout) {
        mSRL = srl
    }
}

