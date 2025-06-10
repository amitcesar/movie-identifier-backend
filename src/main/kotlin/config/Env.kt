package com.github.eucesinha.utils

object Env {
    private val dotenv = io.github.cdimascio.dotenv.dotenv()

    val googleVisionApiKey: String = dotenv["GOOGLE_VISION_API_KEY"]
    val tmdbApiKey: String = dotenv["TMDB_API_KEY"]
}