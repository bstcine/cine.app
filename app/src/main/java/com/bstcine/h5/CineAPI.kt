package com.bstcine.h5

import com.bstcine.h5.data.ResModel
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST

interface CineAPI {

    @POST("api/auth/signin")
    fun login(@Body data: Any): Flowable<ResModel<Any>>

}