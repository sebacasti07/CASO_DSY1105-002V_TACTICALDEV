package com.example.caso_dsy1105_002v_tacticaldev.Network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET

interface ExternalApiService {
    @GET("api")
    suspend fun obtenerIndicadores(): Response<JsonObject>
}
