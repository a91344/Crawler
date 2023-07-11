package criterion

import org.jsoup.nodes.Document
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class Crawler {
    public var html: Document by Delegates.notNull()
    public var url: String = ""
    public var bytes: ByteArray = ByteArray(0)
    private var fields = HashMap<String, Any>()
    fun next() = fields["NEXT"] as Boolean

    init {
        nextPipeline()
    }

    fun noNextPipeline() {
        fields["NEXT"] = false
    }

    fun nextPipeline() {
        fields["NEXT"] = true
    }

    fun putField(key: String, value: Any) {
        fields[key] = value
    }

    fun getFields() = fields
    fun setFields(fields: HashMap<String, Any>) {
        this.fields = fields
    }

    fun getField(key: String) = fields[key]

    fun addRequest(url: String) {
        addRequest(Request(url, "get", null, null))
    }

    fun addRequest(url: String, method: String, body: Any? = null) {
        addRequest(Request(url, method, body, null))
    }

    fun addRequest(request: Request) {
        Companion.addRequest(request)
    }

    public fun addRequest(
        url: String,
        method: String = "get",
        body: Any? = null,
        tag: Pipeline? = null
    ) {
        addRequest(Request(url, method, body, tag))
    }

    public fun addRequest(
        url: String,
        method: String = "get",
        body: Any? = null,
        tag: Pipeline? = null,
        fields: HashMap<String, Any>? = null
    ) {
        addRequest(Request(url, method, body, tag, fields))
    }

    public fun addRequest(
        url: String,
        method: String = "get",
        body: Any? = null,
        tag: ((crawler: Crawler) -> Unit?)? = null
    ) {
        addRequest(Request(url, method, body, tag))
    }

    companion object {
        var baseUrl = ""
        val requestQueue = LinkedBlockingQueue<Request>()
        private var QUEUE_TIMEOUT = 15L
        fun addRequest(url: String) {
            addRequest(
                Request(
                    url,
                    "get",
                    null,
                    null
                )
            )
        }

        fun addRequest(url: String, method: String, body: Any? = null) {
            addRequest(
                Request(
                    url,
                    method,
                    body,
                    null
                )
            )
        }

        fun addRequest(request: Request) {
            requestQueue.add(request)
        }

        public fun addRequest(
            url: String,
            method: String = "get",
            body: Any? = null,
            tag: Pipeline? = null
        ) {
            addRequest(
                Request(
                    url,
                    method,
                    body,
                    tag
                )
            )
        }

        public fun addRequest(
            url: String,
            method: String = "get",
            body: Any? = null,
            tag: Pipeline? = null,
            fields: HashMap<String, Any>? = null
        ) {
            addRequest(
                Request(
                    url,
                    method,
                    body,
                    tag,
                    fields
                )
            )
        }

        public fun addRequest(
            url: String,
            method: String = "get",
            body: Any? = null,
            tag: ((crawler: Crawler) -> Unit)? = null
        ) {
            addRequest(
                Request(
                    url,
                    method,
                    body,
                    tag
                )
            )
        }

        fun pollRequest(): Request = requestQueue.poll(QUEUE_TIMEOUT, TimeUnit.SECONDS)
    }

    data class Request(
        var url: String,
        var method: String,
        val body: Any? = null,
        var tag: Any? = null,
        var fields: HashMap<String, Any>? = null,
        var retryCount: Int = Companion.retryCount
    ) {

        companion object {
            var retryCount = 4
        }
    }
}