import criterion.Crawler;
import criterion.Pipeline;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DownloadCPipeline implements Pipeline {
    private String path = "E:/temp2/";
    private File file = new File("");
    private FileWriter fw;

    @Override
    public void progress(@NotNull Crawler crawler) {
        String temp = crawler.getHtml().selectFirst("div#content").toString();
        String title = crawler.getHtml().getElementsByAttribute("title").last().text();
        title = title + " " + crawler.getHtml().selectFirst("h1.title").text();
        System.out.println(title);
        file = new File(path, title + ".txt");
        try {
            fw = new FileWriter(file, true);
            fw.write(temp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
