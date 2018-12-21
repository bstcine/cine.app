package com.bstcine.h5.utils

import com.bstcine.h5.Config
import com.google.gson.Gson
import okhttp3.*
import kotlin.collections.HashMap


object NetUtil {

    private var client = OkHttpClient()

    fun post(url: String, data: Map<String, Any>, callback: Callback) {
        val json = HashMap<String, Any>()
        json["data"] = data
        json["sitecode"] = "cine.web.android.kotlin"

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Gson().toJson(json))
        val request = Request.Builder()
                .url(Config.BASE_URL + url)
                .post(body)
                .build()
        client.newCall(request).enqueue(callback)
    }

}