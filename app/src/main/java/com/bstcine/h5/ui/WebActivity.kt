package com.bstcine.h5.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.webkit.*
import com.bstcine.h5.App
import com.bstcine.h5.Config
import com.bstcine.h5.R

class WebActivity : AppCompatActivity() {

    private lateinit var mWebView: WebView

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mWebView = findViewById(R.id.web)
        // 设置允许加载混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        mWebView.settings.javaScriptEnabled = true
        mWebView.addJavascriptInterface(object {
            @JavascriptInterface
            fun login() {
                startActivity(Intent(this@WebActivity, LoginActivity::class.java))
            }

            @JavascriptInterface
            fun logout() {
                App.instance.logout()
            }

        }, "Android")
        mWebView.webViewClient = object : WebViewClient() {
            /**
             * 防止加载网页时调起系统浏览器
             */
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (!url.contains("bstcine.com")) return true

                val intent = Intent(this@WebActivity, WebActivity::class.java)
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
        }
        mWebView.loadUrl(Config.urlBindInfo(intent.getStringExtra("url")))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
