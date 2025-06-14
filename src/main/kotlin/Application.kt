package com.github.eucesinha

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*

import io.github.cdimascio.dotenv.dotenv
val dotenv = dotenv()

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}
