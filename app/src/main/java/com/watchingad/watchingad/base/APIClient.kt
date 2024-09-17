package com.watchingad.watchingad.base

import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Build
import com.google.gson.*
import com.watchingad.watchingad.BuildConfig
import com.watchingad.watchingad.utils.AppUtil
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import java.io.IOException
import java.net.CookieManager
import java.util.regex.Pattern


object APIClient {

    private const val BASE_URL_API = BuildConfig.BASE_URL_API
    private var client: OkHttpClient? = null
    private var httpLoggingInterceptor: HttpLoggingInterceptor? = null
    private var headerInterceptor: Interceptor? = null
    private val gsonDateFormat = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()

    private const val METHOD_GET = "GET"
    private const val METHOD_POST = "POST"

    init {
        // httpLoggingInterceptor 초기화
        httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor!!.level = HttpLoggingInterceptor.Level.BODY

        //headerInterceptor 초기화
        headerInterceptor = Interceptor {
            var request = it.request()

            if(METHOD_POST == request.method) {
                val requestBuilder = request.newBuilder()
                val formBody = FormBody.Builder()
                    .add("dUuid", AppUtil.getSSAID())
                    .add("dOs", "A")
                    .add("dModel", Build.MODEL)
                    .add("dOsVersion", AppUtil.getOsVersion())


                var postBodyString = bodyToString(request.body)
                if(postBodyString.isNotEmpty()) {

                    // {"id":"xxx@xxx.com","name":"jjh","password":"xxx","phone":"000-0000-0000"} - json object
                    try {
                        val jsonObject: JsonObject = JsonParser().parse(postBodyString).asJsonObject
                        val resultMap = Gson().fromJson<HashMap<String, String>>(jsonObject.toString(), HashMap::class.java)
                        resultMap.forEach{ (k, v) -> formBody.add(k, v)}
                    }
                    catch (e: JsonSyntaxException) {
                        //id=xxx@xxx.com&password=xxx - not json object
                        val sanitizer = UrlQuerySanitizer("?$postBodyString")
                        sanitizer.parameterList.forEach { e -> formBody.add(e.mParameter, e.mValue) }
                    }

                }

                postBodyString = bodyToString(formBody.build())

                request = requestBuilder.post(
                    postBodyString
                        .toRequestBody("application/x-www-form-urlencoded;charset=UTF-8".toMediaTypeOrNull())
                ).build()
            }
            else if(METHOD_POST == request.method) {

            }

            return@Interceptor it.proceed(request)
        }

        //client 초기화
        client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .addInterceptor(headerInterceptor!!)
            .addInterceptor(httpLoggingInterceptor!!)
            .build()
    }

    fun <T> getClient(service: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gsonDateFormat))
            .build()
            .create(service)

    }

    private fun bodyToString(request: RequestBody?): String {
        return try {
            var buffer = Buffer()
            request?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "error"
        }
    }

}