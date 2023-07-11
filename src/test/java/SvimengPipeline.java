import criterion.Crawler;
import criterion.Pipeline;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

public class SvimengPipeline implements Pipeline {
    public void progressItem(Crawler crawler) {
        crawler.noNextPipeline();
        for (Element element : crawler.getHtml().selectXpath("//div[@class='section-box'][2]/ul/li/a")) {
            var href = element.attr("href");
            crawler.addRequest(href, "get", "");
        }
    }

    public void progressItem_(@NotNull Crawler crawler) {
        crawler.noNextPipeline();
        for (Element element : crawler.getHtml().getElementById("indexselect").getElementsByAttribute("value")) {
            var href = element.attr("value");
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
        crawler.addRequest("/files/article/html/250/250888/", "get", "", this::progressItem_);
    }
}
