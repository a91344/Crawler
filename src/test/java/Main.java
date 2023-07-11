import basic.CrawlerCore;
import criterion.Crawler;
import criterion.Pipeline;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

public class Main  {
    public static void main(String[] args) {
        new CrawlerCore.Builder()
                .startUrl("http://www.svimeng.com/")
                .mainPipeline(new SvimengPipeline())
                .addPipeline(new DownloadCPipeline())
                .consolePipeline(crawler -> {
                    System.out.println(crawler.getUrl());
                })
                .addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"
                )
//                .addProxy("http://127.0.0.1", 7890)
                .threadNumber(8)
                .build()
                .run();
    }

}
