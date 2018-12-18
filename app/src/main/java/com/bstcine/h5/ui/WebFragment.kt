package com.bstcine.h5.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.bstcine.h5.App
import com.bstcine.h5.Config
import com.bstcine.h5.R

private const val ARG_HREF = "param_url"

class WebFragment : Fragment() {
    private var mHref: String? = null

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

        val web = view.findViewById<WebView>(R.id.web)
        // 设置允许加载混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        web.settings.javaScriptEnabled = true
        web.addJavascriptInterface(object {
            @JavascriptInterface
            fun login() {
                startActivity(Intent(activity, LoginActivity::class.java))
            }

            @JavascriptInterface
            fun logout() {
                App.instance.logout()
            }

        }, "Android")
        web.webViewClient = object : WebViewClient() {
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
        }
        web.loadUrl(Config.urlBindInfo(this.mHref!!))

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(href: String) =
                WebFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_HREF, href)
                    }
                }
    }
}
