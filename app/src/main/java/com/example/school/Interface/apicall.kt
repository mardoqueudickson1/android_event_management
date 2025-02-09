package com.example.school.Interface

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.*

data class User(
    val id: String,
    val name: String,
    val username: String,
    val email: String
)


interface ApiService {
    @GET("users/{id}")
    suspend fun getUsuario(@Path("id") id: Int): User

    @GET("users")
    suspend fun getUsers(): List<User>

    @POST("users")
    suspend fun createUser(@Body usuario: User): User

    @PUT("users/{id}")
    fun updateUser(@Path("id") id: String, @Body user: User): Call<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Call<Void>
}
