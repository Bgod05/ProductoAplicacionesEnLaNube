package com.example.museoapp.database.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        val instance: ApiServices by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.stripe.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(ApiServices::class.java)
        }
    }
}
