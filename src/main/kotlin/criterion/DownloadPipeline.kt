package criterion

import java.io.File

public abstract class DownloadPipeline : DisposablePipeline {
    public abstract fun getFileName(crawler: Crawler): String
    public abstract fun filtrate(): String

    override fun progress(crawler: Crawler) {
        if (!crawler.url.contains(Regex(".+(${filtrate()}).*")))
            return
        File(getFileName(crawler)).apply {
            if (!parentFile.exists())
                parentFile.mkdirs()
        }.writeBytes(crawler.responseByte())
    }
}