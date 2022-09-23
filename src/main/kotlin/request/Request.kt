package request

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object RequestApi {
    private var clients = ArrayList<OkHttpClient>()
    private var proxys = ArrayList<Proxy>()

    public fun init(
        headers: HashMap<String, String>,
        proxys: HashMap<String, Int>
    ) {
        for (proxy in proxys) {
            proxy.key.split("://").apply {
                RequestApi.proxys.add(
                    Proxy(
                        (firstOrNull() ?: "").run {
                            if (contains("http"))
                                Proxy.Type.HTTP
                            else if (contains("socks"))
                                Proxy.Type.SOCKS
                            else
                                Proxy.Type.DIRECT
                        },
                        InetSocketAddress(
                            lastOrNull() ?: "",
                            proxy.value
                        )
                    )
                )
            }
        }

        OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request()
                    .newBuilder()
                    .apply {
                        for (header in headers) {
                            addHeader(
                                header.key,
                                header.value
                            )
                        }
                    }
                    .build()
                return@addInterceptor it.proceed(request)
            }
            .readTimeout(8, TimeUnit.SECONDS)
            .connectTimeout(8, TimeUnit.SECONDS)
            .apply {
                for (proxy in RequestApi.proxys) {
                    proxy(proxy)
                    clients.add(build())
                }
                clients.ifEmpty { clients.add(build()) }
            }
    }

    public fun execute(request: Request) = clients.take(1).first().newCall(request).execute()
}