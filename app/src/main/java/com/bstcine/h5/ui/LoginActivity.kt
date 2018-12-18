package com.bstcine.h5.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.bstcine.h5.App
import com.bstcine.h5.utils.NetUtils
import com.bstcine.h5.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var mUserName: EditText
    private lateinit var mPassword: EditText
    private lateinit var mBtnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mUserName = findViewById(R.id.username)
        mPassword = findViewById(R.id.password)
        mBtnLogin = findViewById(R.id.btn_login)

        mBtnLogin.setOnClickListener {
            Log.i("to", "onClick")

            val data = HashMap<String, Any>()
            data["phone"] = mUserName.text.toString()
            data["password"] = mPassword.text.toString()
            NetUtils.post("/auth/signin", data, object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body()!!.string(), object : TypeToken<Map<String, Any>>() {}.rawType) as Map<*, *>
                    Log.i("api", result.toString())
                    if (result["except_case_desc"] == "") {
                        val rs = result["result"] as Map<*, *>
                        App.instance.login(rs["token"].toString())
                        runOnUiThread {
                            onBackPressed()
                        }
                    }
                }
            })
        }
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
}
