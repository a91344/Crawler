package request

import criterion.Crawler

interface RequestStandard {

    public fun init(
        headers: HashMap<String, String>,
        proxies: HashMap<String, Int>
    );

    public fun execute(request: Crawler.Request): Response
}