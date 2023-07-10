import basic.CrawlerCore;
import criterion.Crawler;
import criterion.Pipeline;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

public class Main implements Pipeline {
    public static void main(String[] args) {
        new CrawlerCore.Builder()
                .startUrl("http://www.svimeng.com/")
                .mainPipeline(new Main())
                .addPipeline(new DownloadCPipeline())
//                .addPipeline(new ConsolePipeline())
                .addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"
                )
//                .addProxy("http://127.0.0.1", 7890)
                .threadNumber(8)
                .build()
                .run();
    }

    public void progressItem(Crawler crawler) {
        var title = crawler.getHtml().getElementsByAttribute("title").last().text();
        var subTitle = crawler.getHtml().selectFirst("h1.title").text();
        System.out.println(title + " " + subTitle);
//        crawler.putField("title", title);
//        crawler.putField("content", crawler.getHtml().selectFirst("div#content").toString());
    }

    public void progressItem_(@NotNull Crawler crawler) {
        var title = crawler.getHtml().select(".top>h1").text();
        crawler.putField("title", title);
        for (Element element : crawler.getHtml().getElementById("indexselect").getElementsByAttribute("value")) {
            var href = element.attr("value");
            crawler.addRequest(href, "get", "", this::progressItem_);
        }
        for (Element element : crawler.getHtml().selectXpath("//div[@class='section-box'][2]/ul/li/a")) {
            var href = element.attr("href");
            crawler.addRequest(href, "get", "", this::progressItem);
        }
    }


    @Override
    public void progress(@NotNull Crawler crawler) {
//        crawler.addRequest("/files/article/html/18/18816/", "get", "", this::progressItem_);
//        crawler.addRequest("/files/article/html/0/961/", "get", "", this::progressItem_);
//        crawler.addRequest("/files/article/html/15/15726/", "get", "", this::progressItem_);
//        crawler.addRequest("/files/article/html/1/1051/", "get", "", this::progressItem_);
//        crawler.addRequest("/files/article/html/9/9741/", "get", "", this::progressItem_);
//        crawler.addRequest("/files/article/html/0/56/", "get", "", this::progressItem_);
        crawler.addRequest("/files/article/html/15/15640/", "get", "", this::progressItem_);
    }
}
