package com.github.eucesinha

import com.github.eucesinha.model.MovieModels
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class TmdbService(private val apiKey: String) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    suspend fun searchMovie(query: String): List<MovieModels.Movie> {
        val response: HttpResponse = client.get("https://api.themoviedb.org/3/search/movie") {
            parameter("api_key", apiKey)
            parameter("query", query)
            parameter("language", "en-US")
        }
        val responseResult = response.body<MovieModels.MovieSearchResult>()
        val moviesFiltered = responseResult.results.filter { it.poster_path != null }
//        return responseResult.results
        return moviesFiltered
    }


}