package com.watchingad.watchingad.login.api

import android.text.Editable
import com.watchingad.watchingad.login.data.SignUpConfigData
import com.watchingad.watchingad.login.data.SignUpEmailAuthData
import com.watchingad.watchingad.login.data.UserData
import com.watchingad.watchingad.message.SuccessMessage
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface LoginAPI {

    @GET("sign-up/get-latest-config")
    fun getLatestConfig(): Call<SuccessMessage<SignUpConfigData>>

    @POST("sign-up/send-auth-mail")
    fun sendAuthMail(@Query("sendTo") sendTo: String): Call<SuccessMessage<Object>>

    @GET("sign-up/check-auth-num")
    fun checkAuthNum(@Query("sendTo") sendTo: String, @Query("authNum") authNum: String): Call<SuccessMessage<SignUpEmailAuthData>>

    @POST("user/save")
    fun userJoin(@Body userData: UserData): Call<SuccessMessage<Object>>

    @FormUrlEncoded
    @POST("user/login")
    fun login(@Field(value = "id", encoded = true) id: String, @Field(value = "password", encoded = true) password: String): Call<SuccessMessage<Object>>

}