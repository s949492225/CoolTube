package com.syiyi.cooltube.api

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object RetrofitInstance {
    private const val url: String = "https://pipedapi.kavin.rocks/"

    val api: PipedApi by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
            .create(PipedApi::class.java)
    }
}
