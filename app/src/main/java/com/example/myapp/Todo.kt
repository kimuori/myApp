package com.example.myapp

import com.squareup.moshi.Json

//attributes that a to-do list has: a to-do string and a (un)checked box
data class Todo(
    val description: String,
    val completed: Boolean,
)