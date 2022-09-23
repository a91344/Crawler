package basic

import criterion.Core
import criterion.Crawler
import criterion.Pipeline
import com.alibaba.fastjson.JSONObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.HttpStatusException
import request.RequestApi
import java.io.File

public class CrawlerCore private constructor() : Core() {

    override fun execute(request: Crawler.Request): Crawler {
        return BasicCrawler().apply {
            val response = RequestApi.execute(handlerRequest(request))
            if (response.code != 200) {
                throw HttpStatusException("", response.code, url)
            } else if (response.body == null) {
                throw HttpStatusException("", response.code, url)
            }
            init(response)
        }
    }

    private fun handlerRequest(request: Crawler.Request): Request {
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

    class Builder() : Core.Builder() {
        override fun initCore() {
            core = CrawlerCore()
        }

        override fun build(): Core {
            RequestApi.init(headers, proxys)
            return core
        }
    }

    private fun log(log: String) {
        if (isShowErrorLog)
            System.err.println(log)
    }
}