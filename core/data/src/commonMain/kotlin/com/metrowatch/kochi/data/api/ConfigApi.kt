package com.metrowatch.kochi.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ConfigApi(private val client: HttpClient) {
    private val baseUrl = "https://bray5sxxd3.execute-api.ap-south-1.amazonaws.com"

    suspend fun getVersion(): VersionResponse {
        return client.get("$baseUrl/config/version").body()
    }

    suspend fun getConfig(): ConfigResponse {
        return client.get("$baseUrl/config").body()
    }
}
