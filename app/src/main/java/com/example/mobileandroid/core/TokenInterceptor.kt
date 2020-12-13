package com.example.mobileandroid.core

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
  var token: String? = Constants.instance()?.fetchValueString("token")

  override fun intercept(chain: Interceptor.Chain): Response {
    val original = chain.request()
    val originalUrl = original.url
    if (token == null) {
      return chain.proceed(original)
    }
    val requestBuilder = original.newBuilder()
      .addHeader("Authorization", "Bearer $token")
      .url(originalUrl)
    val request = requestBuilder.build()
    return chain.proceed(request)
  }
}