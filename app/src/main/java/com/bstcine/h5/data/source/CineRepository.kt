package com.bstcine.h5.data.source

import android.text.TextUtils
import com.bstcine.h5.CineApplication
import com.bstcine.h5.CineConfig
import com.bstcine.h5.data.source.remote.CineRemoteDataSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap
import java.util.concurrent.TimeUnit


class CineRepository {

    private object Holder {
        val INSTANCE = CineRepository()
    }

    companion object {
        val instance: CineRepository by lazy { Holder.INSTANCE }
    }

    var mRemoteDataSource: CineRemoteDataSource

    init {
        val httpClient = OkHttpClient.Builder()
                .connectTimeout((20 * 1000).toLong(), TimeUnit.MILLISECONDS)
                .readTimeout((20 * 1000).toLong(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true) // 失败重发
                .addInterceptor { chain ->
                    val original = chain.request()

                    //统一参数
                    val token = CineApplication.INSTANCE.token()
                    val sitecode = "cine.web.android.kotlin"

                    val requestBuilder = original.newBuilder()

                    //封装 Body 参数
                    if ("POST" == original.method() && !(original.body() is FormBody || original.body() is MultipartBody)) {
                        val buffer = Buffer()
                        original.body()!!.writeTo(buffer)
                        val dataJson = buffer.readUtf8()

                        val dataMap = Gson().fromJson<Map<String, Any>>(dataJson, object : TypeToken<Map<String, Any>>() {}.type)

                        val basicParams = HashMap<String, Any>()
                        basicParams["sitecode"] = sitecode
                        basicParams["data"] = dataMap
                        if (!TextUtils.isEmpty(token)) basicParams["token"] = token!!

                        val bodyJson = Gson().toJson(basicParams)

                        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), bodyJson)
                        requestBuilder.post(requestBody)
                    }

                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .addInterceptor { chain ->
                    val resp = chain.proceed(chain.request())

                    val cookieStr = resp.header("Set-Cookie")
                    if (!TextUtils.isEmpty(cookieStr) && cookieStr!!.contains("token=;")) {
                        CineApplication.INSTANCE.logout()
                    }
                    resp
                }

        val retrofit = Retrofit.Builder()
                .baseUrl(CineConfig.BASE_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build()
        mRemoteDataSource = retrofit.create(CineRemoteDataSource::class.java)
    }
}