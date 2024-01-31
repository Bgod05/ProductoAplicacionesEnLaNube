package com.example.museoapp.database.remote

import com.example.museoapp.database.Utils.Companion.SECRET_KEY
import com.example.museoapp.model.CustomerModel
import com.example.museoapp.model.PaymentIntentModel
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServices {

    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/customers")
    suspend fun getCustomerId(): Response<CustomerModel>

    @Headers(
        "Authorization: Bearer $SECRET_KEY",
        "Stripe-Version: 2023-10-16"
    )
    @POST("v1/ephemeral_keys")
    suspend fun getEphemeralKey(
        @Query("customer") customer: String,
    ): Response<CustomerModel>

    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/payment_intents")
    suspend fun getPaymentIntent(
        @Query("customer") customerId: String,
        @Query("amount") amount: String = "10000",
        @Query("currency") currency: String = "PEN",
        @Query("automatic_payment_methods[enabled]") automaticPay: Boolean = true,
    ): Response<PaymentIntentModel>
}