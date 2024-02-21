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
import lol.unsession.db.models.DEFAULT_PAGE_SIZE
import org.slf4j.LoggerFactory.getLogger
import rawHttpClient

val rawClient = rawHttpClient()
val apiClient = apiHttpClient()

const val apiUrl = "http://api.unsession.su/v1"

sealed class Api {
    abstract val endpoint: String
    val logger = getLogger("Api")!!
    data object Teachers : Api() {
        override val endpoint: String
            get() = "$apiUrl/teachers"

        suspend fun getTeachers(
            page: Int,
            pageSize: Int = DEFAULT_PAGE_SIZE,
            onSuccess: suspend (List<TeacherDto>) -> Unit = {},
            onFailure: (String) -> Unit = {}
        ): Result<List<TeacherDto>> {
            try {
                val call = apiClient.get {
                    url("$endpoint/get")
                    parameter("page", page)
                    parameter("pageSize", pageSize)
                }
                if (!call.status.isSuccess()) {
                    onFailure(call.bodyAsText())
                    return Result.failure(IllegalArgumentException(call.bodyAsText()))
                }
                val result: List<TeacherDto> = call.body()
                logger.debug("Got teachers: $result")
                onSuccess(result)
                return Result.success(result)
            } catch (e: Exception) {
                onFailure(e.message.toString())
            }
            return Result.failure(IllegalArgumentException("Failed to get teachers"))
        }

        @OptIn(InternalAPI::class)
        suspend fun searchTeachers(
            page: Int,
            prompt: String,
            pageSize: Int = DEFAULT_PAGE_SIZE,
            onSuccess: suspend (List<TeacherDto>) -> Unit = {},
            onFailure: (String) -> Unit = {}
        ): Result<List<TeacherDto>> {
            try {
                val call = apiClient.get {
                    url("$endpoint/search")
                    parameter("page", page)
                    parameter("pageSize", pageSize)
                    parameter("prompt", prompt)
                }
                println("Got teachers: ${call.content}")
                println("$endpoint/search: ${call.request.headers.entries()}; ${call.status}")
                if (!call.status.isSuccess()) {
                    onFailure(call.bodyAsText())
                    return Result.failure(IllegalArgumentException(call.bodyAsText()))
                }
                val result: List<TeacherDto> = call.body()
                onSuccess(result)
                return Result.success(result)
            } catch (e: Exception) {
                onFailure(e.message.toString())
            }
            return Result.failure(IllegalArgumentException("Failed to get teachers"))
        }
    }

    data object Reviews : Api() {
        override val endpoint: String
            get() = "$apiUrl/reviews"

        suspend fun getByTeacher(
            page: Int,
            teacherId: Int,
            pageSize: Int = DEFAULT_PAGE_SIZE,
            onSuccess: suspend (List<Review>) -> Unit = {},
            onFailure: (String) -> Unit = {}
        ): List<Review> {
            try {
                val call = apiClient.get {
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
                loginData.save()
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
                val data = call.body<LoginResponse>()
                data.save()
                loginData.save()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.message.toString())
            }
        }

        suspend fun sendFcmToken(token: String) {
            try {
                val call = apiClient.post {
                    url("$endpoint/fcm")
                    setBody(token)
                }
                if (!call.status.isSuccess()) {
                    println("Failed to send FCM token: ${call.bodyAsText()}")
                }
            } catch (e: Exception) {
                println("Failed to send FCM token: ${e.message}")
            }
        }
    }
}
