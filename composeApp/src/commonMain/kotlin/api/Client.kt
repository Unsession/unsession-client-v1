package api

import api.models.LoginResponse
import api.models.Review
import api.models.ReviewDto
import api.models.TeacherDto
import api.models.User
import apiHttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.isSuccess
import io.ktor.util.InternalAPI
import lol.unsession.db.models.PAGE_SIZE_DEFAULT
import org.slf4j.LoggerFactory.getLogger
import rawHttpClient

val rawClient = rawHttpClient()
val apiClient = apiHttpClient()

const val apiUrl = "http://api.unsession.su/v1"

sealed class Api {
    abstract val endpoint: String
    val logger = getLogger("Api")
    data object Teachers : Api() {
        override val endpoint: String
            get() = "$apiUrl/teachers"

        suspend fun getTeachers(
            page: Int,
            pageSize: Int = PAGE_SIZE_DEFAULT,
            onSuccess: suspend (List<TeacherDto>) -> Unit = {},
            onFailure: (String) -> Unit = {}
        ): List<TeacherDto> {
            try {
                val call = api.apiClient.get {
                    url("${Api.Teachers.endpoint}/get")
                    parameter("page", page)
                    parameter("pageSize", pageSize)
                }
                if (!call.status.isSuccess()) {
                    onFailure(call.bodyAsText())
                    return listOf()
                }
                val result: List<TeacherDto> = call.body()
                logger.debug("Got teachers: $result")
                onSuccess(result)
                return result
            } catch (e: Exception) {
                onFailure(e.message.toString())
            }
            return listOf()
        }

        @OptIn(InternalAPI::class)
        suspend fun searchTeachers(
            page: Int,
            prompt: String,
            pageSize: Int = PAGE_SIZE_DEFAULT,
            onSuccess: suspend (List<TeacherDto>) -> Unit = {},
            onFailure: (String) -> Unit = {}
        ): List<TeacherDto> {
            try {
                val call = api.apiClient.get {
                    url("${Api.Teachers.endpoint}/search")
                    parameter("page", page)
                    parameter("pageSize", pageSize)
                    parameter("prompt", prompt)
                }
                println("Got teachers: ${call.content}")
                println("${Api.Teachers.endpoint}/search: ${call.request.headers.entries()}; ${call.status}")
                if (!call.status.isSuccess()) {
                    onFailure(call.bodyAsText())
                    return listOf()
                }
                val result: List<TeacherDto> = call.body()
                onSuccess(result)
                return result
            } catch (e: Exception) {
                onFailure(e.message.toString())
            }
            return listOf()
        }
    }

    data object Reviews : Api() {
        override val endpoint: String
            get() = "$apiUrl/reviews"

        suspend fun getByTeacher(
            page: Int,
            teacherId: Int,
            pageSize: Int = PAGE_SIZE_DEFAULT,
            onSuccess: suspend (List<Review>) -> Unit = {},
            onFailure: (String) -> Unit = {}
        ): List<Review> {
            try {
                val call = api.apiClient.get {
                    url("$endpoint/getByTeacher")
                    parameter("page", page)
                    parameter("pageSize", pageSize)
                    parameter("teacherId", teacherId)
                }
                if (!call.status.isSuccess()) {
                    onFailure(call.bodyAsText())
                    return listOf()
                }
                val result = call.body<List<Review>>()
                onSuccess(result)
                return result
            } catch (e: Exception) {
                onFailure(e.message.toString())
            }
            return listOf()
        }

        suspend fun create(
            review: ReviewDto,
            onFailure: (String) -> Unit = {},
            onSuccess: suspend () -> Unit
        ) {
            try {
                println("Creating review: $review.")
                println("Creating review: ${review.userId}")
                val call = apiClient.post {
                    url("$endpoint/create")
                    setBody(review)
                }
                println(call.bodyAsText())
                if (!call.status.isSuccess()) {
                    onFailure(call.bodyAsText())
                    return
                }
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.message.toString())
            }
        }
    }

    data object Users : Api() {
        override val endpoint: String
            get() = "$apiUrl/users"

        suspend fun login(
            loginData: User.UserLoginData,
            onSuccess: suspend () -> Unit,
            onFailure: (String) -> Unit = {}
        ) {
            try {
                val call = rawClient.post {
                    url("$endpoint/login")
                    setBody(loginData)
                }
                if (!call.status.isSuccess()) {
                    onFailure(call.status.description)
                    return
                }
                val data = call.body<LoginResponse>()
                data.save()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.message.toString())
            }
        }

        suspend fun register(
            loginData: User.UserLoginData,
            onSuccess: suspend () -> Unit,
            onFailure: (String) -> Unit
        ) {
            loginData.save()
            try {
                val call = rawClient.post {
                    url("$endpoint/register")
                    setBody(loginData)
                }
                if (!call.status.isSuccess()) {
                    onFailure(call.bodyAsText())
                    return
                }
                call.body<LoginResponse>().save()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.message.toString())
            }
        }
    }
}
