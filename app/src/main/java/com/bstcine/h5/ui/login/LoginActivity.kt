package com.bstcine.h5.ui.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.bstcine.h5.R

class LoginActivity : AppCompatActivity(), LoginContract.View {
    override lateinit var presenter: LoginContract.Presenter

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
            presenter.login(mUserName.text.toString(), mPassword.text.toString())
        }

        LoginPresenter(this)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onLoginSuccess(token: String, user: Map<*, *>) {
        setResult(1)
        finish()
    }

    override fun onLoginSuccessWithoutRx(token: String, user: Map<*, *>) {
        runOnUiThread {
            setResult(1)
            finish()
        }
    }
}
