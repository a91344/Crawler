package basic

import criterion.Core
import criterion.Crawler
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup

public class CrawlerCore private constructor() : Core() {

    override fun execute(request: Crawler.Request): Crawler {
        return Crawler().apply {
            val response = requestApi.execute(request)
            if (response.code != 200) {
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