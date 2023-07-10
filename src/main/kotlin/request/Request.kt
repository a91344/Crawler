package request

import com.alibaba.fastjson.JSONObject
import criterion.Crawler
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

public class RequestApi() : RequestStandard {
    private var clients = ArrayList<OkHttpClient>()
    private var proxies = ArrayList<Proxy>()

    public override fun init(
        headers: HashMap<String, String>,
        proxies: HashMap<String, Int>
    ) {
        for (proxy in proxies) {
            proxy.key.split("://").apply {
                this@RequestApi.proxies.add(
                    Proxy(
                        (firstOrNull() ?: "").run {
                            if (contains("http"))
                                Proxy.Type.HTTP
                            else if (contains("socks"))
                                Proxy.Type.SOCKS
                            else
                                Proxy.Type.DIRECT
                        },
                        InetSocketAddress(
                            lastOrNull() ?: "",
                            proxy.value
                        )
                    )
                )
            }
        }

        OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request()
                    .newBuilder()
                    .apply {
                        for (header in headers) {
                            addHeader(
                                header.key,
                                header.value
                            )
                        }
                    }
                    .build()
                return@addInterceptor it.proceed(request)
            }
            .readTimeout(8, TimeUnit.SECONDS)
            .connectTimeout(8, TimeUnit.SECONDS)
            .apply {
                for (proxy in this@RequestApi.proxies) {
                    proxy(proxy)
                    clients.add(build())
                }
                clients.ifEmpty { clients.add(build()) }
            }
    }

    override fun execute(request: Crawler.Request): Response {
        val response = Response()
        val responseBoy = clients.take(1).first().newCall(handlerRequest(request)).execute()
        response.url = request.url
        response.code = responseBoy.code
        response.bytes = responseBoy.body?.bytes() ?: ByteArray(0)
        response.body = response.bytes.decodeToString()
        return response
    }

    public fun handlerRequest(request: Crawler.Request): Request {
        request.url = if (!request.url.contains("http://") && request.url.startsWith("/")) {
            "${Crawler.baseUrl}${request.url}"
        } else {
            request.url
        }
        return Request.Builder()
            .url(request.url)
            .tag(request.tag)
            .apply {
                when (request.method) {
                    "post", "POST" -> {
                        method("post", null)
                    }

                    "post@json", "POST@JSON" -> {
                        if (request.body is Map<*, *>) {
                            post(FormBody.Builder().apply {
                                request.body.forEach { any, u ->
                                    add((any ?: "") as String, (u ?: "") as String)
                                }
                            }.build())
                        }
                    }

                    "post@from", "POST@FROM" -> {
                        if (request.body is Map<*, *>) {
                            post(
                                JSONObject.toJSONString(request.body)
                                    .toRequestBody("application/json; charset=utf-8".toMediaType())
                            )
                        }
                    }

                    "post@file", "POST@FILE" -> {
                        if (request.body is File) {
                            MultipartBody.Builder().addFormDataPart(
                                "filename", request.body.name,
                                request.body.asRequestBody("application/octet-stream".toMediaTypeOrNull())
                            )
                        }
                    }

                    "get", "GET" -> {
                        get()
                    }
                }
            }
            .build()
    }
}