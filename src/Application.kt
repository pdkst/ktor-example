package io.github.pdkst

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.velocity.*
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setDateFormat(DateFormat.LONG).create()
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
        install(Velocity) {
            //模板引擎参数实例化
            val ep = Properties()
            //指定资源的加载类型
            ep.setProperty(RuntimeConstants.RESOURCE_LOADER, "file,classpath")
            //允许#set设置null
            ep.setProperty("directive.set.null.allowed", "true")
            init(ep)
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

        get("/vm/{vmName}") {
            val vmName = call.parameters["vmName"]?.trim()?.let { call.parameters["vmName"] } ?: "test.vm"
            val content = Files.newBufferedReader(Paths.get("resources/vm/$vmName.json"))
            val model = gson.fromJson(content, hashMapOf<String, Any>().javaClass)
            call.respond(VelocityContent("resources/vm/$vmName.vm", model))
        }

        get("/root/{vmName}") {
            val vmName = call.parameters["vmName"]?.trim()?.let { call.parameters["vmName"] } ?: "test.vm"
            val content = Files.newBufferedReader(Paths.get("$vmName.json"))
            val model = gson.fromJson(content, hashMapOf<String, Any>().javaClass)
            call.respond(VelocityContent("$vmName.vm", model))
        }
    }
}
