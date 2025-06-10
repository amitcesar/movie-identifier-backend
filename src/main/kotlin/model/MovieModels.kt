package com.github.eucesinha.model

import kotlinx.serialization.Serializable

class MovieModels {

@Serializable
data class MovieSearchResult(
    val results: List<Movie>
)

@Serializable
data class Movie (
    val title: String,
    val overview: String,
    val release_date: String,
    val poster_path: String? = null
){
    val fullPosterUrl: String?
        get() = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" }
}
}