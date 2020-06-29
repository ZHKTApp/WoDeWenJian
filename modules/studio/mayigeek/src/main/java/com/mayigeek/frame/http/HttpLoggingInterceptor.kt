package com.mayigeek.frame.http

import com.mayigeek.frame.http.log.LogLevel
import com.mayigeek.frame.http.log.Logger
import com.mayigeek.frame.http.state.RequestHook
import com.mayigeek.frame.http.state.ResponseHook
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.TimeUnit

/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * [application interceptor][OkHttpClient.interceptors] or as a [ ][OkHttpClient.networkInterceptors].
 *
 * The format of the logs created by
 * this class should not be considered stable and may change slightly between releases. If you need
 * a stable logging format, use your own interceptor.
 */
open class HttpLoggingInterceptor constructor(private var logger: Logger? = Logger.DEFAULT) : Interceptor {
    private val UTF8 = Charset.forName("UTF-8")


    var requestHook: RequestHook? = null
    var responseHook: ResponseHook? = null

    var level: LogLevel = LogLevel.NONE
        private set

    /**
     * Change the logLevel at which this interceptor logs.
     */
    fun setLevel(level: LogLevel?): HttpLoggingInterceptor {
        if (level == null)
            throw NullPointerException("logLevel == null. Use LogLevel.NONE instead.")
        this.level = level
        return this
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val level = this.level

        var request = chain.request()
        val requestBuilder = request.newBuilder()
        requestHook?.onHook(requestBuilder)
        request = requestBuilder.build()
        if (level === LogLevel.NONE) {
            return chain.proceed(request)
        }

        val logBody = level === LogLevel.BODY
        val logHeaders = logBody || level === LogLevel.HEADERS

        val requestBody = request.body()
        val hasRequestBody = requestBody != null

        val connection = chain.connection()
        val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
        var requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody!!.contentLength() + "-byte body)"
        }

        logger!!.log(requestStartMessage)//POST http://qz.test.project.zhiwangyilian.com/api/business-shopping/order/orderListB http/1.1

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody!!.contentType() != null) {

                    //   logger!!.log("Content-Type: " + requestBody.contentType())//--> Content-Type: application/x-www-form-urlencoded
                }
                if (requestBody.contentLength() != -1L) {
                    // logger!!.log("Content-Length: " + requestBody.contentLength())//Content-Length: 209
                }
            }

//            val headers = request.headers()
//            var i = 0
//            val count = headers.size()
//            while (i < count) {
//                val name = headers.name(i)
//                // Skip headers from the request body as they are explicitly logged above.
//                if (!"Content-Type".equals(name, true) && !"Content-Length".equals(name, true)) {
//                    logger!!.log(name + ": " + headers.value(i))
//                }
//                i++
//            }

            if (!logBody || !hasRequestBody) {
                logger!!.log("--> END " + request.method())
            } else if (bodyEncoded(request.headers())) {
                logger!!.log("--> END " + request.method() + " (encoded body omitted)")
            } else {
                val buffer = Buffer()
                requestBody!!.writeTo(buffer)

                var charset = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }

                logger!!.log("")
                logger!!.log(buffer.readString(charset))

                // logger!!.log("--> END " + request.method() + " (" + requestBody.contentLength() + "-byte body)")//END POST (209-byte body)
            }
        }

        val startNs = System.nanoTime()
        var response = chain.proceed(request)
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body()
        val contentLength = responseBody?.contentLength()
        val bodySize = if (contentLength != -1L) contentLength.toString() + "-byte" else "unknown-length"
        logger!!.log("<-- " + response.code() + ' ' + response.message() + ' ' + response.request().url() + " (" + tookMs + "ms" + (if (!logHeaders)
            ", $bodySize body"
        else
            "") + ')')

        if (logHeaders) {
            //     val headers = response.headers()
            //          var i = 0
//            val count = headers.size()
//            while (i < count) {
//                logger!!.log(headers.name(i) + ": " + headers.value(i))// Date: Tue, 24 Apr 2018 03:22:38 GMT#  Server: nginx/1.13.6##  X-Application-Context: gateway:test:8080
//                i++
//            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                logger!!.log("<-- END HTTP")
            } else if (bodyEncoded(response.headers())) {
                logger!!.log("<-- END HTTP (encoded body omitted)")
            } else {
                val source = responseBody?.source()
                source?.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source?.buffer()

                var charset = UTF8
                val contentType = responseBody?.contentType()
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8)
                    } catch (e: UnsupportedCharsetException) {
                        logger!!.log("")
                        logger!!.log("Couldn't decode the response body; charset is likely malformed.")
                        logger!!.log("<-- END HTTP")

                        return response
                    }

                }

                if (contentLength != 0L) {
                    logger!!.log("")
                    var l = buffer?.clone()?.readString(charset)
                    logger!!.log(l+"")
                }

                logger!!.log("<-- END HTTP (" + buffer?.size() + "-byte body)")
            }
        }
        val responseBuilder = response.newBuilder()
        responseHook?.onHook(responseBuilder)
        response = responseBuilder.build()
        return response
    }


    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", true)
    }

    fun setLogger(logger: Logger) {
        this.logger = logger
    }
}
