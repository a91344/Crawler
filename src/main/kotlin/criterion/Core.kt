package criterion

import basic.ThreadPool
import request.RequestApi
import request.RequestStandard
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import kotlin.system.exitProcess

abstract class Core : Runnable {
    protected lateinit var requestApi: RequestStandard
    protected var isShowErrorLog = true
    protected var threadNumber = 4
    protected var startUrl = ""
    protected var mainPipeline: Pipeline by Delegates.notNull()
    protected var consolePipeline: Pipeline? = null
    protected var piplines = ArrayList<Pipeline>()

    abstract fun execute(request: Crawler.Request): Crawler
    override fun run() {
        do {
            ThreadPool.execute(Runnable {
                var request: Crawler.Request? = null
                try {
                    request = Crawler.pollRequest()
                    val crawler = execute(request)
                    request.fields?.let { crawler.setFields(it) }
                    consolePipeline?.progress(crawler)
                    if (request.tag == null) {
                        mainPipeline.progress(crawler)
                    } else {
                        if (request.tag is Pipeline) {
                            (request.tag as Pipeline).progress(crawler)
                        } else {
                            (request.tag as (Crawler) -> Unit).invoke(crawler)
                        }
                    }
                    if (!crawler.next())
                        return@Runnable
                    for (pipline in piplines) {
                        pipline.progress(crawler)
                    }
                } catch (e: NullPointerException) {
                    return@Runnable
                } catch (e: Throwable) {
                    request?.apply {
                        log("error:${e.message} url:${request.url} residue retry count:${request.retryCount}")
                        request.retryCount -= 1
                        Thread.sleep(1500)
                        if (request.retryCount > 0)
                            Crawler.addRequest(request)
                        else
                            return@Runnable
                    }
                }
            })
        } while (ThreadPool.pool.activeCount>0)
        ThreadPool.awaitTermination()
        Crawler.requestQueue.clear()
        exitProcess(0)
    }

    private fun log(log: String) {
        if (isShowErrorLog)
            System.err.println(log)
    }

    abstract class Builder {
        protected lateinit var core: Core
        protected abstract fun initCore()
        protected var headers = HashMap<String, String>()
        protected var proxys = HashMap<String, Int>()

        init {
            initCore()
        }

        public fun requestApi(requestApi: RequestStandard): Builder {
            core.requestApi = requestApi
            return this
        }

        public fun mainPipeline(pipeline: Pipeline): Builder {
            core.mainPipeline = pipeline
            return this
        }

        public fun consolePipeline(pipeline: Pipeline): Builder {
            core.consolePipeline = pipeline
            return this
        }

        public fun addPipeline(pipeline: Pipeline): Builder {
            core.piplines.add(pipeline)
            return this
        }

        public fun addHeader(key: String, value: String): Builder {
            headers.put(key, value)
            return this
        }

        public fun addProxy(host: String, port: Int): Builder {
            proxys[host] = port
            return this
        }

        public fun threadNumber(size: Int): Builder {
            core.threadNumber = size
            ThreadPool.init(core.threadNumber)
            return this;
        }

        public fun startUrl(url: String): Builder {
            Crawler.baseUrl = url.substring(0, url.indexOf('/', 8))
            core.startUrl = url
            Crawler.addRequest(url)
            return this;
        }

        public fun retryCount(count: Int): Builder {
            Crawler.Request.retryCount = count
            Crawler.requestQueue.first()?.retryCount = count
            return this
        }

        public fun build(): Core {
            if (core::requestApi.isLateinit) {
                core.requestApi = RequestApi()
            }
            core.requestApi.init(headers, proxys)
            return core
        }

//        abstract fun build(): Core
    }
}