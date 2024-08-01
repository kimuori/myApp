package com.example.myapp

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/*
    Response Body for POST (register)
    "...api/users/register?apikey=###..."
    {
    "name": "string"
    "email": "user2@email.com" [use]
    "enabled": true
    "token": "###..." [use]
        (NOTE: this token is used to authorize)
    "admin": false
    "id": 116 [use]
    }
 */

/*
    Response Body for POST (to-do)
    "...api/users/{116}/todos?apikey=###..."
    {
    "description": "Finish Assignment 2"
    "completed": false
    "user_id": 166 [ignore]
    "author": "string" [ignore]
    "id": 1503
    }
 */

/*
    Response Body for GET (to-do)
    "...api/users/{116}/todos?apikey=###..."
    {
    "id": 1503
    "user_id": 166 [ignore?]
    "description": "Finish Assignment 2"
    "completed": 0
    "author": "string" [ignore?]
    "meta": null [ignore?]
    }
 */
data class Todo (
    @Json(name= "id") val id : Int,
    @Json(name= "description") val description : String,
    @Json(name= "completed") val completed : Boolean)

interface ApiService {

    /*
        Retrieve all todos
     */
    @GET("api/users/{user_id}/todos")
    suspend fun getTodos (
        @Query("apikey") apiKey : String,
        @Header("authorization") bearerToken : String,
        @Path("userId") userId : String
    ) : List<Todo>

    /*
        Create a new todos for current user
     */
    @POST("api/users/{user_id}/todos")
    suspend fun createTodo (
        @Query("apikey") apiKey : String,
        @Header("authorization") bearerToken : String,
        @Path("userId") userId : String,
        @Path("todoId") todoID : String,
        @Body todo: Todo
    ): Todo

}

object ApiClient {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://todos.simpleapi.dev/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
