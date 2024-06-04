package com.example.chamasegura.retrofit

import com.example.chamasegura.retrofit.tabels.Users
import com.example.chamasegura.retrofit.tabels.Queimadas
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Body

interface SupabaseAuthService {
    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @GET("rest/v1/Users")
    fun verifyUser(
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<List<Users>>

    @POST("rest/v1/Queimadas")
    fun createQueimada(@Body queimadas: Queimadas): Call<Void>
}
