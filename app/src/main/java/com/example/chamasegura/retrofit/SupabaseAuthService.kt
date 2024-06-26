package com.example.chamasegura.retrofit

import com.example.chamasegura.retrofit.tabels.*
import retrofit2.Call
import retrofit2.http.*

data class UpdateQueimadaRequest(
    val idAprovation: Long
)

data class UpdateAprovationBombeirosRequest(
    val bombeiros: String
)

data class UpdateAprovationProtecaoCivilRequest(
    val protecao_civil: String
)

data class UpdateAprovationMunicipioRequest(
    val municipio: String
)

data class UpdateAprovationAdminRequest(
    val status: String
)

data class UpdateEstadoConta(
    val estado_conta: String
)

data class UpdateUser(
    val email: String,
    val name: String,
    val idRole: Long
)

interface SupabaseAuthService {

    companion object {
        const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhoaWtzb3B2d3R5aGRkeHZza21uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUwMTI2NTksImV4cCI6MjAzMDU4ODY1OX0.Rv-VuClP-0oPTiYf37H0VbGowZaPzyTvtm3Ro-_oGyI"
    }

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Users")
    fun verifyUser(
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<List<Users>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Users")
    fun getRoleIdByUserId(
        @Query("idUsers") idUser: String
    ): Call<List<Users>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Users")
    fun getUserById(
        @Query("idUsers") idUser: String
    ): Call<List<Users>>


    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Users")
    fun getAllUsers(): Call<List<Users>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @PATCH("rest/v1/Users")
    fun updateUser(
        @Query("idUsers") idUsers: String,
        @Body updateUserRequest: UpdateUser
    ): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @DELETE("rest/v1/Users/{idUsers}")
    fun deleteUser(
        @Path("idUsers") idUser: Long
    ): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Roles")
    fun getRole(
        @Query("idRole") idRole: String
    ): Call<List<Roles>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @POST("rest/v1/Queimadas")
    fun createQueimada(@Body queimadas: Queimadas): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @POST("rest/v1/Aprovation")
    fun createAprovation(@Body aprovation: Aprovation): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @PATCH("rest/v1/Queimadas")
    fun updateQueimada(
        @Query("idQueimada") idQueimada: String,
        @Body updateQueimadaRequest: UpdateQueimadaRequest
    ): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Location")
    fun getLocationByCoordinates(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String
    ): Call<List<Location>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @POST("rest/v1/Location")
    fun createLocation(@Body location: Location): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Location")
    fun getLocations(): Call<List<Location>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Queimadas")
    fun getPendingQueimadas(
        @Query("idUser") idUser: String
    ): Call<List<Queimadas>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Queimadas")
    fun getQueimadasByStatus(
        @Query("status") status: String
    ): Call<List<Queimadas>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/TypeQueimadas")
    fun getTypeQueimadas(): Call<List<TypeQueimadas>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Municipalities")
    fun getTypeMunicipalities(): Call<List<Municipalities>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/TypeRules")
    fun getTypeRules(): Call<List<TypeRules>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Queimadas")
    fun getAllQueimadas(): Call<List<Queimadas>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Aprovation")
    fun getAllAprovations(): Call<List<Aprovation>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @PATCH("rest/v1/Aprovation")
    fun updateAprovationBombeiros(
        @Query("idAprovation") idAprovation: String,
        @Body updateAprovationBombeirosRequest: UpdateAprovationBombeirosRequest
    ): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @PATCH("rest/v1/Aprovation")
    fun updateAprovationProtecaoCivil(
        @Query("idAprovation") idAprovation: String,
        @Body updateAprovationProtecaoCivilRequest: UpdateAprovationProtecaoCivilRequest
    ): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @PATCH("rest/v1/Aprovation")
    fun updateAprovationMunicipio(
        @Query("idAprovation") idAprovation: String,
        @Body updateAprovationMunicipioRequest: UpdateAprovationMunicipioRequest
    ): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @PATCH("rest/v1/Queimadas")
    fun updateAprovationAdmin(
        @Query("idAprovation") idAprovation: String,
        @Body updateAprovationAdminRequest: UpdateAprovationAdminRequest
    ): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @PATCH("rest/v1/Users")
    fun UpdateEstadoConta(
        @Query("idUsers") idUsers: String,
        @Body updateEstadoConta: UpdateEstadoConta
    ): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @POST("rest/v1/Rules")
    fun createRules(@Body rules: Rules): Call<Void>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Rules")
    fun getRegrasMunicipio(
        @Query("idMunicipalities") idMunicipio: String
    ): Call<List<Rules>>

    @Headers("Content-Type: application/json", "apikey: $API_KEY")
    @GET("rest/v1/Roles")
    fun getAllCargos(): Call<List<Roles>>


}
