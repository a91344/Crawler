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
import org.jsoup.Jsoup
import request.RequestApi
import java.io.File

public class CrawlerCore private constructor() : Core() {

    override fun execute(request: Crawler.Request): Crawler {
        return Crawler().apply {
            val response = requestApi.execute(request)
            if (response.code != 200) {
                throw HttpStatusException("", response.code, url)
            } else if (response.body == null) {
                throw HttpStatusException("", response.code, url)
            }
            html = Jsoup.parse(response.body)
            url = response.url
            bytes = response.bytes
        }
    }


    class Builder() : Core.Builder() {
        override fun initCore() {
            core = CrawlerCore()
        }
    }

    private fun log(log: String) {
        if (isShowErrorLog)
            System.err.println(log)
    }
}