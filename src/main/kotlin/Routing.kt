package com.github.eucesinha


import com.github.eucesinha.utils.Env
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.*
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import io.ktor.utils.io.readByte
import io.ktor.utils.io.readByteArray
import java.io.File
import kotlin.collections.mapOf


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


            val apiKey = Env.googleVisionApiKey
            val visionService = GoogleVisionService(apiKey)

            val labels = try {
                visionService.analyzeImage(file!!)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Erro ao chamar a Google Vision API")
                return@post
            }

            call.respond(HttpStatusCode.OK, mapOf("labels" to labels))
        }
    }
}
