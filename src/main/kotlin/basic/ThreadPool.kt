package basic

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ThreadPool {
    lateinit var pool: ThreadPoolExecutor
    private var AWAIT_TERMINATION_TIMEOUT = 8L
    public fun init(threadSize: Int, keepAliveTime: Long = 60L, takes: Int = 1024) {
        pool = ThreadPoolExecutor(
            threadSize,
            threadSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(takes),
            ThreadPoolExecutor.DiscardPolicy()
        )
    }
    public fun awaitTermination(){
        pool.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS)
    }

    public fun execute(runnable: Runnable) {
        pool.execute(runnable)
    }

}