package com.bstcine.h5.data.source.remote

import com.bstcine.h5.data.ResModel
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST

interface CineRemoteDataSource {

    @POST("api/auth/signin")
    fun login(@Body data: Any): Flowable<ResModel<Any>>

}