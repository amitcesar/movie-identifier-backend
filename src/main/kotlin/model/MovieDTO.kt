package com.github.eucesinha.model

data class MovieDTO(
    val title: String,
    val overview: String,
    val release_date: String,
    val fullPosterUrl: String? = null
)