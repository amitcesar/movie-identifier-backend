package com.github.eucesinha.model

import kotlinx.serialization.Serializable

@Serializable
data class IdentifyResponse(
    val labels: List<String>,
    val movies: List<MovieModels.Movie>
)
