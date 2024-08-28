package edu.metrostate.testingdemo

import retrofit2.http.POST

interface ApiService {

    @POST("/authorize")
    suspend fun login(username: String, password: String): User
}