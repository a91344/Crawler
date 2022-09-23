import basic.CrawlerCore
import criterion.Crawler
import criterion.Pipeline
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import kotlin.random.Random

class CuPipeline : Pipeline {
    private var download = DownloadPipeline()
    private var count = 0
    override fun progress(crawler: Crawler) {
        crawler.html.selectXpath("//div[@class='gallery']/a").map {
            crawler.addRequest(it.attr("href"), tag = this::progressItem)
        }.apply {
            count+=size
            println("----------$count------------")
        }
        val next = crawler.html.selectXpath("//a[@class='next']").firstOrNull()?.attr("href") ?: ""
        if (next.contains("101").not() ?: false)
            crawler.addRequest(next)
    }

    private fun progressItem(crawler: Crawler) {
        val cartoon = Cartoon()
        val info = crawler.html.getElementById("info")?.apply {
            val h2 = getElementsByTag("h2").text()
            val h1 = getElementsByTag("h1").text()
            cartoon.title = (if (h2.isNullOrEmpty()) h1 else h2).replace("'", "")
            cartoon._md5 = MD5Utils.EncodeByMD5(cartoon.title)
        } ?: return
        for (element in info.selectXpath("//section[@id='tags']/div")) {
            if (element.text().contains("Artists"))
                cartoon.author = element.selectXpath("./span/a").text()
            else if (element.text().contains("Tags"))
                cartoon.tags = element.selectXpath("./span/a").map { Entocn.tagToCn(it.ownText()) }
                    .filter { !it.contains(Regex("[a-zA-Z]+")) }
        }
        for (element in crawler.html.selectXpath("//div[@class='thumb-container']/a/img")) {
            val src = element.attr("data-src").replace("t.jpg", ".jpg").replace("t.png", ".png")
            cartoon.imgs.add(src)
        }
        cartoon.apply {
            page = imgs.size
            cover = imgs.firstOrNull() ?: ""
            timeOfReceipt = System.currentTimeMillis()
        }
        crawler.putField("cartoon", cartoon)
    }
}


class ConsolePipeline : Pipeline {
    private val connection: Connection

    init {
        Class.forName("org.sqlite.JDBC")
        connection = DriverManager.getConnection("jdbc:sqlite:./nhentai.db");
        val createStatement = connection.createStatement()
        createStatement.execute(SqlCreate.CARTOON_SQL)
        createStatement.execute(SqlCreate.CARTOON_IMGS_SQL)
        createStatement.close()
    }

    override fun progress(crawler: Crawler) {
        crawler.getField("cartoon")?.apply {
            this as Cartoon
            val createStatement = connection.createStatement()
            createStatement.execute(this.toInstallSqlToCartoon())
            createStatement.execute(this.toInstallSqlToCartoonImage())
            createStatement.close()
        }
    }
}

class DownloadPipeline : criterion.DownloadPipeline() {
    override fun getFileName(crawler: Crawler): String {
        return "F:/image/${(crawler.getField("cartoon") as Cartoon)._md5}/${Random.nextInt(10000, 99999)}.jpg"
    }

    override fun filtrate(): String {
        return "jpg|png"
    }
}

fun main() {
    CrawlerCore.Builder()
        .startUrl("https://nhentai.to/language/chinese?page=1")
        .mainPipeline(CuPipeline())
        .addPipeline(ConsolePipeline())
        .threadNumber(6)
        .addHeader(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"
        )
        .addProxy("http://127.0.0.1", 7890)
        .retryCount(4)
        .build()
        .run()
}