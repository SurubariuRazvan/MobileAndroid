package com.example.mobileandroid.coreimport com.google.gson.GsonBuilderimport okhttp3.OkHttpClientimport retrofit2.Retrofitimport retrofit2.converter.gson.GsonConverterFactoryobject Api {    const val URL = "jderu.cf:3000/"    val tokenInterceptor = TokenInterceptor()    private val client: OkHttpClient = OkHttpClient.Builder().apply {        this.addInterceptor(tokenInterceptor)    }.build()    private var gson = GsonBuilder().setLenient().create()    val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://$URL").addConverterFactory(GsonConverterFactory.create(gson)).client(client).build()}