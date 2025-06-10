package com.github.eucesinha


import com.github.eucesinha.model.IdentifyResponse
import com.github.eucesinha.model.MovieModels
import com.github.eucesinha.utils.Env
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import java.io.File


fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/identify") {
            val multipartData = call.receiveMultipart()
            var fileName: String? = null
            var file: File? = null

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileName = part.originalFileName ?: "upload_${System.currentTimeMillis()}.jpg"
                        file = File("uploads/$fileName").apply {
                            parentFile.mkdirs()
                        }
                        part.provider().copyAndClose(file!!.writeChannel())
                        println("Arquivo salvo: ${file!!.absolutePath}")
                    }

                    else -> part.dispose()
                }
            }

            if (fileName == null || file == null) {
                call.respond(HttpStatusCode.BadRequest, "Nenhum Arquivo Encontrado.")
                return@post
            }


            val visionApiKey = Env.googleVisionApiKey
            val visionService = GoogleVisionService(visionApiKey)

            val labels = try {
                visionService.analyzeImage(file)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Erro ao chamar a Google Vision API")
                return@post
            }



//            val query = labels.joinToString(" ")
//
//            val queryPrimary = labels.firstOrNull() ?: "joker"
//            val queryComposta = labels.joinToString(" ")

            val queryCandidates = labels.take(3).map { it.trim() .lowercase()}
            val otherQueries = labels.joinToString(" "){it.trim()}.lowercase()

            val tmdmApikey = Env.tmdbApiKey
            val tmdbService = TmdbService(tmdmApikey)


//            var movies = tmdbService.searchMovie(queryPrimary)
            var movies: List<MovieModels.Movie> = emptyList()

            for (query in queryCandidates) {
                println("🔎 Tentando TMDb com: \"$query\"")
                movies = tmdbService.searchMovie(query)
                if (movies.isNotEmpty()) break
            }

            if (movies.isEmpty()) {
                println("⚠️ Nenhum resultado com individuais. Tentando com composto: \"$otherQueries\"")
                movies = tmdbService.searchMovie(otherQueries)
            }


            call.respond(HttpStatusCode.OK, IdentifyResponse(labels, movies))
        }
    }
}
