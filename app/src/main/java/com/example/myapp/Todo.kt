package com.example.myapp

import com.squareup.moshi.Json

//attributes that a to-do list has: a to-do string and a (un)checked box
data class Todo(
    @Json(name = "description") val description: String?,
    @Json(name = "completed") val completed: Boolean)