package com.bstcine.h5.login

import android.util.Log
import com.bstcine.h5.CineApplication
import com.bstcine.h5.utils.NetUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class LoginPresenter(val loginView: LoginContract.View) : LoginContract.Presenter {

    init {
        loginView.presenter = this
    }

    override fun start() {

    }

    override fun login(username: String, password: String) {
        val data = HashMap<String, Any>()
        data["phone"] = username
        data["password"] = password

        NetUtils.post("/auth/signin", data, object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body()!!.string(), object : TypeToken<Map<String, Any>>() {}.rawType) as Map<*, *>
                Log.i("api", result.toString())
                if (result["except_case_desc"] == "") {
                    val rs = result["result"] as Map<*, *>
                    val token = rs["token"].toString()
                    CineApplication.INSTANCE.login(token)

                    loginView.onLoginSuccess(token, rs["user"] as Map<*, *>)
                }
            }
        })
    }

}