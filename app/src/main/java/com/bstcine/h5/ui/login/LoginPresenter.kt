package com.bstcine.h5.ui.login

import android.util.Log
import com.bstcine.h5.CineApplication
import com.bstcine.h5.data.source.CineRepository
import com.bstcine.h5.utils.NetUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginPresenter(val loginView: LoginContract.View) : LoginContract.Presenter {

    init {
        loginView.presenter = this
    }

    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun subscribe() {

    }

    override fun unsubscribe() {
        mCompositeDisposable.clear()
    }

    override fun login(username: String, password: String) {
        val data = HashMap<String, Any>()
        data["phone"] = username
        data["password"] = password

//        loginOkHttp(data)
        loginRetrofit(data)
    }

    fun loginOkHttp(data: Map<String, Any>) {
        val disposable = Observable
                .create(ObservableOnSubscribe<Map<*, *>> { emitter ->
                    NetUtil.post("/api/auth/signin", data, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}
                        override fun onResponse(call: Call, response: Response) {
                            val result = Gson().fromJson(response.body()!!.string(), object : TypeToken<Map<String, Any>>() {}.rawType) as Map<*, *>
                            val rs = result["result"] as Map<*, *>
                            emitter.onNext(rs)
                        }
                    })
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ rs ->
                    val token = rs["token"].toString()
                    CineApplication.INSTANCE.login(token)
                    loginView.onLoginSuccess(token, rs["user"] as Map<*, *>)
                }, { error ->
                    Log.i("tag", error.printStackTrace().toString())
                })
        mCompositeDisposable.add(disposable)
    }

    fun loginRetrofit(data: Map<String,Any>){
        val disposable = CineRepository.instance.mRemoteDataSource
                .login(data)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    val rs = result.result!! as Map<*, *>

                    val token = rs["token"].toString()
                    val user = rs["user"] as Map<*, *>

                    CineApplication.INSTANCE.login(token)
                    loginView.onLoginSuccess(token, user)
                }, { error ->
                    Log.i("tag", error.printStackTrace().toString())
                })
        mCompositeDisposable.add(disposable)
    }

}