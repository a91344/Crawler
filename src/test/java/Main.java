import basic.CrawlerCore;
import criterion.Crawler;
import criterion.Pipeline;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

public class Main implements Pipeline {
    public static void main(String[] args) {
        new CrawlerCore.Builder()
                .startUrl("https://nhentai.to/language/chinese?page=1")
                .mainPipeline(new CuPipeline())
                .addPipeline(new ConsolePipeline())
                .addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"
                )
                .addProxy("http://127.0.0.1", 7890)
                .threadNumber(8)
                .build()
                .run();
    }

    public void progressItem(Crawler crawler) {
        String temp = crawler.getHtml().getElementById("info").getElementsByTag("h2").toString();
        System.out.print(temp);
    }

    @Override
    public void progress(@NotNull Crawler crawler) {
        for (Element element : crawler.getHtml().selectXpath("//div[@class='gallery']/a")) {
            crawler.addRequest(element.attr("href"), "get", "", this::progressItem);
        }
    }


}
