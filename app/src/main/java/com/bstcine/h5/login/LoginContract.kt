package com.bstcine.h5.login

import com.bstcine.h5.BasePresenter
import com.bstcine.h5.BaseView

interface LoginContract {

    interface View : BaseView<Presenter> {

        fun onLoginSuccess(token: String, user: Map<*, *>)

    }

    interface Presenter : BasePresenter {

        fun login(username: String, password: String)

    }
}