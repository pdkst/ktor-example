package io.github.pdkst

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import java.net.URI
import java.text.DateFormat

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    routing {
        install(ContentNegotiation) {
            gson {
                disableHtmlEscaping()
                setPrettyPrinting()
                setDateFormat(DateFormat.LONG)
            }
        }
        install(CORS)
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

        //http://localhost:8080/object/1?name=123
        get("/object/{id}") {
            val data: SimpleData = SimpleData(
                call.parameters["id"]?.toLong() ?: 0L,
                call.parameters["name"] ?: ""
            )
            println("simple date = $data")
            //json data
            call.respond(data)
        }

        //http://localhost:8080/redirect
        get("/redirect") {
            call.respondRedirect("http://www.baidu.com", permanent = true)
        }
    }
}
