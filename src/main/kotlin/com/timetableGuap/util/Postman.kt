package com.timetableGuap.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class Postman {
    private val json = Json { coerceInputValues = true; ignoreUnknownKeys = true }
    val semaphore = Semaphore(20)
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }

        install(HttpTimeout) {
            connectTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            requestTimeoutMillis = 30000
        }

        install(HttpRequestRetry) {
            maxRetries = 2
            retryIf { _, response -> response.status.value >= 500 }
            delayMillis { retry -> (retry * 2000).toLong() } // will retry in 2, 4 seconds
            modifyRequest { it.headers.append("X_RETRY_COUNT", retryCount.toString()) }
        }
    }

    /** Return Resource with data from server request GET **/
    suspend inline fun <reified T> get(
        baseUrl: String,
        route: String,
        headers: Map<String, Any> = mapOf(),
        arguments: Map<String, Any?> = mapOf(),
        contentType: ContentType = Json
    ): Resource<T> {
        semaphore.withPermit {
            println("get url $baseUrl$route")
            println("args: $arguments")
            return try {
                val http = httpClient.get {
                    url(baseUrl + route)
                    contentType(contentType)

                    arguments.forEach {
                        if (it.value != null)
                            parameter(it.key, it.value)
                    }

                    headers.forEach {
                        header(it.key, it.value)
                    }
                }

                obtainResult(http)
            } catch (e: Exception) {
                println(baseUrl + route)
                println(arguments)
                obtainException(e)
            }
        }
    }

    /** Return Resource with data from server request POST **/
    suspend inline fun <reified T> post(
        baseUrl: String,
        route: String,
        body: Any?,
        headers: Map<String, Any> = mapOf(),
        contentType: ContentType = Json,
    ): Resource<T> {
        println("post url: $baseUrl$route")
        println(body)
        semaphore.withPermit {
            return try {
                val http = httpClient.post {
                    url(baseUrl + route)
                    contentType(contentType)

                    headers.forEach {
                        header(it.key, it.value)
                    }

                    if (body != null)
                        setBody(body)
                }

                obtainResult(http)
            } catch (e: Exception) {
                obtainException(e)
            }
        }
    }

    suspend inline fun <reified T> submitFormWithBinaryFiles(
        baseUrl: String,
        route: String,
        headers: Map<String, Any> = mapOf(),
        arguments: List<PartData> = listOf(),
        contentType: ContentType = Json
    ): Resource<T> {
        semaphore.withPermit {
            println("get url $baseUrl$route")
            println(semaphore.availablePermits)
            println("args: $arguments")
            return try {
                val http = httpClient.submitFormWithBinaryData(
                    baseUrl + route,
                    arguments
                ) {
                    contentType(contentType)
                    headers.forEach {
                        header(it.key, it.value)
                    }
                }

                obtainResult(http)
            } catch (e: Exception) {
                println(baseUrl + route)
                println(arguments)
                obtainException(e)
            }
        }
    }

    suspend inline fun <reified T> obtainResult(http: HttpResponse): Resource<T> {
        return if (http.status.isSuccess())
            Resource.Success(http.body(), http.status.value)
        else {
            when (http.status) {
                HttpStatusCode.Unauthorized -> Resource.Error<T>(
                    Resource.ErrorsRequest.ERROR_USER_CREDENTIALS.desc + ": " + http.bodyAsText(),
                    http.status.value
                )

                HttpStatusCode.BadRequest -> Resource.Error<T>(
                    Resource.ErrorsRequest.ERROR_NOT_VALID_TOKEN.desc + ": " + http.bodyAsText(),
                    http.status.value
                )

                HttpStatusCode.NotFound -> Resource.Error<T>(
                    Resource.ErrorsRequest.ERROR_NOT_FOUND.desc + ": " + http.bodyAsText(),
                    http.status.value
                )

                else -> Resource.Error(
                    Resource.ErrorsRequest.ERROR_UNKNOWN.desc + "(${http.status.value}): " + http.bodyAsText(),
                    http.status.value
                )
            }
        }
    }

    inline fun <reified T> obtainException(e: Exception): Resource<T> {
        println(e.message)
        return when (e) {
            is SerializationException -> {
                Resource.Error(
                    Resource.ErrorsRequest.ERROR_SERIALIZATION.desc + ": " + e.message.orEmpty(),
                    0
                )
            }

            is HttpRequestTimeoutException -> {
                Resource.Error(
                    Resource.ErrorsRequest.ERROR_TIMEOUT.desc + ": " + e.message.orEmpty(),
                    0
                )
            }

            is ConnectTimeoutException -> {
                Resource.Error(
                    Resource.ErrorsRequest.ERROR_TIMEOUT.desc + ": " + e.message.orEmpty(),
                    0
                )
            }

            else -> {
                println("error Exception post")
                println(e::class)
                Resource.Error(e.message.orEmpty(), 0)
            }
        }
    }
}