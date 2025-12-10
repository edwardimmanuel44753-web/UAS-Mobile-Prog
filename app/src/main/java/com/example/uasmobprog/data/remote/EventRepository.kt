package com.example.uasmobprog.data

import com.example.uasmobprog.data.remote.ApiClient

class EventRepository {

    suspend fun getEvents(status: String? = null): ApiResponse<List<Event>> {
        return ApiClient.api.getEvents(status = status)
    }

    suspend fun getEventById(id: Int): ApiResponse<Event> {
        return ApiClient.api.getEventById(id)
    }

    suspend fun createEvent(event: Event): ApiResponse<Event> {
        return ApiClient.api.createEvent(event)
    }

    suspend fun updateEvent(id: Int, event: Event): ApiResponse<Event> {
        return ApiClient.api.updateEvent(id, event)
    }

    suspend fun deleteEvent(id: Int): ApiResponse<Unit> {
        return ApiClient.api.deleteEvent(id)
    }
}
