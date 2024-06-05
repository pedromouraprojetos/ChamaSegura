package com.example.chamasegura.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.Query

data class UpdateUserRequest(
    val username: String,
    val name: String,
    val FirstEnter: Boolean
)

interface UpdateInfoUserService {
    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @PATCH("rest/v1/Users")
    fun updateUser(
        @Query("email") email: String,
        @Body updateUserRequest: UpdateUserRequest
    ): Call<Void>
}