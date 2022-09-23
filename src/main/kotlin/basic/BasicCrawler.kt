package basic

import criterion.Crawler
import okhttp3.*
import org.jsoup.Jsoup
import kotlin.properties.Delegates

class BasicCrawler : Crawler() {
    private var responseBody: ResponseBody by Delegates.notNull()
    public fun init(responseBody: Response) {
        this.responseBody = responseBody.body!!
        this.responseByte = this.responseBody.bytes()
        html = Jsoup.parse(responseText())
        url = responseBody.request.url.toString()
    }

}