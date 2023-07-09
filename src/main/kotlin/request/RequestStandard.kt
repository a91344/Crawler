package request

import criterion.Crawler
import java.util.HashMap

interface RequestStandard {

    public fun init(
        headers: HashMap<String, String>,
        proxies: HashMap<String, Int>
    );

    public fun execute(request: Crawler.Request): Response
}