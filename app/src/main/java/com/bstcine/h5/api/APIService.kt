package com.bstcine.h5.api

import com.bstcine.h5.model.ResModel
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * REST API access points
 */
interface APIService {

    @POST("api/auth/signin")
    fun login(@Body data: Any): Flowable<ResModel<Any>>

}