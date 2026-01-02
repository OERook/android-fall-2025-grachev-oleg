package ru.itis.android.homework_24112025.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.random.Random
import java.util.concurrent.atomic.AtomicInteger

// ============= Custom Exceptions =============
class NetworkException : Exception()
class TimeoutException : Exception()
class ProcessingException : Exception()

// ============= Data Classes =============
data class CoroutineConfig(
    val count: Int = 50,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    val isSequential: Boolean = true,
    val isParallel: Boolean = false,
    val isDeferred: Boolean = false,
    val workInBackground: Boolean = true
)

data class CoroutineException(
    val type: ExceptionType,
    val message: String
)

enum class ExceptionType {
    NETWORK, TIMEOUT, PROCESSING
}

// ============= ViewModel =============
class CoroutinesViewModel : ViewModel() {

    companion object {
        private const val TAG = "Coroutines"
    }

    private var jobs = mutableListOf<Job>()
    private val completedCount = AtomicInteger(0)
    private val failedCount = AtomicInteger(0)

    suspend fun executeCoroutines(config: CoroutineConfig): List<CoroutineException> {
        val exceptions = mutableListOf<CoroutineException>()
        jobs.clear()
        completedCount.set(0)
        failedCount.set(0)

        return when {
            config.isSequential && !config.isParallel -> {
                executeSequential(config, exceptions)
            }
            config.isParallel && !config.isSequential -> {
                executeParallel(config, exceptions)
            }
            else -> exceptions
        }
    }

    private suspend fun executeSequential(
        config: CoroutineConfig,
        exceptions: MutableList<CoroutineException>
    ): List<CoroutineException> {
        repeat(config.count) { index ->
            val job = if (config.isDeferred) {
                viewModelScope.async(config.dispatcher) {
                    runCoroutineTask(index + 1, exceptions)
                }
            } else {
                viewModelScope.launch(config.dispatcher) {
                    runCoroutineTask(index + 1, exceptions)
                }
            }
            jobs.add(job)

            try {
                if (job is Job) {
                    job.join()
                }
            } catch (e: CancellationException) {
            }
        }
        return exceptions
    }

    private suspend fun executeParallel(
        config: CoroutineConfig,
        exceptions: MutableList<CoroutineException>
    ): List<CoroutineException> {
        val tasks = mutableListOf<Job>()

        repeat(config.count) { index ->
            val job = if (config.isDeferred) {
                viewModelScope.async(config.dispatcher) {
                    runCoroutineTask(index + 1, exceptions)
                }
            } else {
                viewModelScope.launch(config.dispatcher) {
                    runCoroutineTask(index + 1, exceptions)
                }
            }
            tasks.add(job)
            jobs.add(job)
        }

        try {
            tasks.joinAll()
        } catch (e: CancellationException) {
        }

        return exceptions
    }

    private suspend fun runCoroutineTask(
        taskNumber: Int,
        exceptions: MutableList<CoroutineException>
    ) {
        try {
            val delayTime = Random.nextLong(1000, 10001)
            Log.d(TAG, "[$taskNumber] Started, delay: ${delayTime}ms")
            delay(delayTime)
            completedCount.incrementAndGet()
            Log.d(TAG, "[$taskNumber] Completed in ${delayTime}ms")

            // 30% шанс выброса исключения если выполнение >= 7 секунд
            if (delayTime >= 7000 && Random.nextDouble() < 0.3) {
                val exceptionType = when (Random.nextInt(3)) {
                    0 -> ExceptionType.NETWORK
                    1 -> ExceptionType.TIMEOUT
                    else -> ExceptionType.PROCESSING
                }

                failedCount.incrementAndGet()
                exceptions.add(CoroutineException(exceptionType, ""))

                when (exceptionType) {
                    ExceptionType.NETWORK -> throw NetworkException()
                    ExceptionType.TIMEOUT -> throw TimeoutException()
                    ExceptionType.PROCESSING -> throw ProcessingException()
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            failedCount.incrementAndGet()
        }
    }

    fun cancelAllJobs(): Int {
        val cancelledCount = jobs.count { it.isActive }
        jobs.forEach { it.cancel() }
        jobs.clear()
        return cancelledCount
    }

    fun getStats(): Pair<Int, Int> = Pair(completedCount.get(), failedCount.get())
}