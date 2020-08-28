package io.github.pdkst

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import java.net.URI

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    routing {
        install(ContentNegotiation) {
            gson {
                disableHtmlEscaping()
                setPrettyPrinting()
            }
        }
        static {
            defaultResource("index.html", "web")
            // /resource/web/ 下面的所有文件
            //不要用成resource了
            resources("web")
        }

        //http://localhost:8080
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        //http://localhost:8080/parameter/1
        get("/parameter/{id}") {
            val id = call.parameters["id"]
            println("id = $id")
            call.resolveResource("${id}.css", "web")?.let { call.respond(it) }
        }
    }
}
