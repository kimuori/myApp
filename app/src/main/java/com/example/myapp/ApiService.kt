package com.example.myapp

import android.content.Context
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    /*
        REQUEST BODY for POST /api/users/register:
            "email": "user1@mail.com",
            "name": "string", [ignore]
            "password": "password"
    */
    @POST("/api/users/register")
    suspend fun registerUser(
        @Query("apikey") apiKey: String,
        @Body request: RegisterRequestBody
    ): User
        /*
            RESPONSE BODY for POST /api/users/register:
            "id": int
            "name": string
            "email": string
            "token": string [given]
         */
    /******************************************************/
    /*
        REQUEST BODY for POST /api/users/login:
          "email": "user1@mail.com",
		  "password": "password"
     */
    @POST("/api/users/login")
    suspend fun loginUser(
        @Query("apikey") apiKey: String,
        @Body request: LoginRequestBody
    ): User
        /*
            RESPONSE BODY for POST /api/users/login:
            "id": int
            "name": string
            "email": string
            "token": string [given]
         */
    /******************************************************/
    /*
        REQUEST BODY for POST /api/users/{user_id}/todos:
          "description": string
		  "completed": boolean
		  "meta": {} [ignore]

		NOTE: use apikey,
		use the provided user_id via "id" and auth the "token".

		@Path is required to replace {user_id} in the URL with a User's unique userId number
     */
    @POST("/api/users/{user_id}/todos")
    suspend fun createTodos(
        @Query("apiKey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Path("user_id") userId: Int,
        @Body request: TodoRequestBody
    ): Todo
        /*
             RESPONSE BODY for POST /api/users/{user_id}/todos:
             "description: string
             "completed": boolean
             "user_id": int (the user account "id") [ignore?]
             "author": string (the user account "name") [ignore]
             "id": 1859 (the unique to-do id number)
         */
    /******************************************************/
    /*
        REQUEST BODY for POST /api/users/{user_id}/todos/{id}:
        "description": string
        "completed": boolean

        NOTE: use apikey,
        use the provided user_id via "id" and auth the "token".

        @Path is required to replace {user_id} in the URL with a User's unique userId number
    */
    @PUT("/api/users/{user_id}/todos/{id}")
    suspend fun updateTodo(
        @Path("user_id") userId: Int,
        @Path("id") todoId: Int,
        @Query("apiKey") apiKey: String,
        @Body request: TodoRequestBody
    ): Todo
        /*
            RESPONSE BODY for POST /api/users/{user_id}/todos/{id}:

         */
    /******************************************************/
    // NOTE: No Request @Body required.
    // @Path is required to replace {user_id} in the URL with a User's unique userId number
    @GET("/api/users/{user_id}/todos")
    suspend fun getAllTodos(
        @Query("apiKey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Path("user_id") userId: Int
    ): List<Todo>
        /*
            RESPONSE BODY for @GET /api/users/{user_id}/todos:
            {
                "description: string
                "completed": boolean
                "user_id": int (the user account "id") [ignore?]
                "author": string (the user account "name") [ignore]
                "id": 1859 (the unique to-do id number) [ignore?]
            }
            {
                "description: string
                "completed": boolean
                "user_id": int (the user account "id") [ignore?]
                "author": string (the user account "name") [ignore]
                "id": 1859 (the unique to-do id number) [ignore?]
            }
            ...
         */

    suspend fun getUser(): User
}

/*
    Request Body to submit a to-do description
    and completion tag for To-doList ViewModel.
    NOTE: "meta:{}" is ignored.
*/
data class TodoRequestBody (
    val description: String,
    val completed: Boolean,
)

/*
    NOTE: "id" via todos is the "userId" in source code
 */
data class User (
    val id: Int,
    val email: String,
    val name: String,
    val token: String
)

/*
    Request Body to submit email and password for Register ViewModel.
    NOTE: "name" is ignored.
*/
data class RegisterRequestBody (
    val email: String,
    val name: String,
    val password: String
)

/*
    Request Body to submit email and password for LogIn ViewModel.
    NOTE: "name" is ignored.
*/
data class LoginRequestBody (
    val email: String,
    val password: String
)

/*
object PreferenceKeys {
    val USER_ID = intPreferencesKey("user_id")
    val BEARER_TOKEN = stringPreferencesKey("token")
}
 */

/*
[HTTP & REST API]
    Create an API Service using Retrofit

    ** User To-do
    GET /api/users/{user_id}/todos
        **fetches the list of to-dos for user with id {user_id}

    POST /api/users/{user_id}/todos
        **creates a new to-do for user with id {user_id}

    PUT /api/users/{user_id}/todos/{id}
        **updates a to-do item for id {user_id} with to-do id {id}

    ** User
    POST /api/users/register
        **creates a new user returns a user object with token and id

    POST /api/users/login
        **logs a user in and returns a user object with token and id

    * Create data classes for each of request and response types.
        * use these types in your Retrofit function definitions
        * DO NOT use Strings to represent complex data types!
            **the same data types are used in multiple routes
            **you should reuse data classes when possible

            Example:
            [POST /api/users/register] and [POST /api/users/login]
            Both return the same data type.
            Use the SAME return type for both of those functions.
 */