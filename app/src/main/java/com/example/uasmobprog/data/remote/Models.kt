package com.example.uasmobprog.data

data class Event(
    val id: Int? = null,
    val title: String,
    val date: String,       // YYYY-MM-DD
    val time: String,       // HH:MM or HH:MM:SS
    val location: String,
    val description: String? = null,
    val capacity: Int? = null,
    val status: String = "upcoming",
    val created_at: String? = null,
    val updated_at: String? = null
)

data class ApiResponse<T>(
    val status: Int? = null,
    val message: String? = null,
    val data: T? = null
)
