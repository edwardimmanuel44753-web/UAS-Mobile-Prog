package com.example.uasmobprog.data.remote

import com.example.uasmobprog.data.ApiResponse
import com.example.uasmobprog.data.Event
import retrofit2.http.*

interface EventApi {

    @GET("api.php")
    suspend fun getEvents(
        @Query("status") status: String? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null
    ): ApiResponse<List<Event>>

    @GET("api.php")
    suspend fun getEventById(@Query("id") id: Int): ApiResponse<Event>

    @POST("api.php")
    suspend fun createEvent(@Body event: Event): ApiResponse<Event>

    @PUT("api.php")
    suspend fun updateEvent(@Query("id") id: Int, @Body event: Event): ApiResponse<Event>

    @DELETE("api.php")
    suspend fun deleteEvent(@Query("id") id: Int): ApiResponse<Unit>
}
