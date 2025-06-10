package com.github.eucesinha

import com.github.eucesinha.model.VisionModels


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Base64

private val stopwords = setOf(
    "smile", "happiness", "poster", "advertising", "model", "vacation", "white-collar worker"
)

class GoogleVisionService(private val apiKey: String) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun analyzeImage(file: File): List<String> {
        val base64Image = Base64.getEncoder().encodeToString(file.readBytes())

        val requestBody = VisionModels.VisionRequest(
            requests = listOf(
                VisionModels.ImageRequest(
                    image = VisionModels.ImageContent(base64Image),
                    features = listOf(VisionModels.Feature("LABEL_DETECTION"))
                )
            )
        )

        val response: HttpResponse = client.post("https://vision.googleapis.com/v1/images:annotate") {
            contentType(ContentType.Application.Json)
            url { parameters.append("key", apiKey) }
            setBody(requestBody)
        }

        val rawJson = response.bodyAsText()
//        println("📦 Resultado da Vision API: $rawJson")
        if ("\"error\"" in rawJson) {
            println("🚫 Erro da Vision API: $rawJson")
            return emptyList()
        }

        val json = response.body<VisionModels.VisionResponse>()

        val rawLabels = json.responses
            .firstOrNull()
            ?.labelAnnotations
            ?: emptyList()

        println("🔍 Labels detectadas brutas:")
        rawLabels.forEach {
            println("→ ${it.description} | score=${it.score} | topicality=${it.topicality}")
        }

        val filtered = rawLabels
            .filter { it.score >= 0.65f && it.topicality >= 0.02f }
            .filterNot { it.description.lowercase() in stopwords }
            .sortedByDescending { it.score * it.topicality }
            .map { it.description }
            .take(5)

        val bestQuery = filtered.firstOrNull() ?: "joker"

        if (filtered.isEmpty()) {
            println("⚠️ Usando fallback: pegando top 3 por score.")
            return rawLabels
                .sortedByDescending {
                    it.score
                }
                .map { it.description }
                .take(3)
        }

        return filtered

    }
}
