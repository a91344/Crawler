import criterion.Crawler;
import criterion.Pipeline;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.FileStore;

public class DownloadCPipeline implements Pipeline {
    private String path = "G:/temp/";
    private File file = new File("");
    private FileWriter fw;

    @Override
    public void progress(@NotNull Crawler crawler) {
        String temp = crawler.getHtml().selectFirst("div#content").toString();
        String title = crawler.getField("title").toString();
        if (!file.getName().equals(title + ".txt")) {
            file = new File(path, title + ".txt");
            try {
                if (fw != null) {
                    fw.flush();
                    fw.close();
                }
                fw = new FileWriter(file, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            fw.write(temp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
