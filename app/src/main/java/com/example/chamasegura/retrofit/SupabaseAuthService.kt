package com.example.chamasegura.retrofit

import com.example.chamasegura.retrofit.tabels.Aprovation
import com.example.chamasegura.retrofit.tabels.Users
import com.example.chamasegura.retrofit.tabels.Queimadas
import com.example.chamasegura.retrofit.tabels.Location
import com.example.chamasegura.retrofit.tabels.TypeQueimadas
import com.example.chamasegura.retrofit.tabels.Roles
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

    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @GET("rest/v1/Users")
    fun getRoleIdByUserId(
        @Query("idUsers") idUser: String,
    ): Call<List<Users>>

    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @GET("rest/v1/Roles")
    fun getRole(
        @Query("idRole") idRole: String,
    ): Call<List<Roles>>

    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @POST("rest/v1/Queimadas")
    fun createQueimada(@Body queimadas: Queimadas): Call<Void>

    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @GET("rest/v1/Location")
    fun getLocationByCoordinates(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String
    ): Call<List<Location>>

    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @POST("rest/v1/Location")
    fun createLocation(@Body location: Location): Call<Void>

    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @GET("rest/v1/Queimadas")
    fun getPendingQueimadas(
        @Query("idUser") idUser: String
    ): Call<List<Queimadas>>

    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @GET("rest/v1/Queimadas")
    fun getQueimadasByStatus(
        @Query("status") status: String
    ): Call<List<Queimadas>>

    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @GET("rest/v1/TypeQueimadas")
    fun getTypeQueimadas(): Call<List<TypeQueimadas>>

    @Headers(
        "Content-Type: application/json",
        "apikey:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    )
    @POST("rest/v1/Aprovation")
    fun addAprovationEntry(@Body bombeiros: String): Call<Void>
}
