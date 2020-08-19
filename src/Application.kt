package com.ailab

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.client.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*

//fun main(args: Array<String>) {
//    embeddedServer(
//        Netty, watchPaths = listOf("solutions/exercise4"), port = 8080,
//        // GOOD!, it will work
//        module = Application::mymodule
//    ).start(true)
//}

// Body extracted to a function acting as a module
fun Application.mymodule() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}

//fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun main(args: Array<String>) {
    embeddedServer(
        Netty, watchPaths = listOf("solutions/exercise4"), port = 8080,
        // GOOD!, it will work
        module = Application::mymodule
    ).start(true)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(Authentication) {
    }

    data class Snippet(val text: String)

    val snippets = Collections.synchronizedList(mutableListOf(
        Snippet("hello"),
        Snippet("world")
    ))


    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
        routing {
            get("/snippets") {
//                call.respond(mapOf("OK" to true))
                call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))

            }
        }
    }

    val client = HttpClient() {
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

