package jmh

import love.forte.simbot.Timestamp
import love.forte.simbot.toTimestamp
import org.openjdk.jmh.annotations.*
import java.time.Instant
import java.util.concurrent.TimeUnit


@BenchmarkMode(Mode.Throughput)
@Measurement(timeUnit = TimeUnit.MILLISECONDS)
@Threads(8)
open class TimestampCreateTest {
    
    @Benchmark
    fun timestampByNow() {
        Timestamp.now()
    }
    
    @Benchmark
    fun timestampByInstantNow() {
        Instant.now().toTimestamp()
    }
}