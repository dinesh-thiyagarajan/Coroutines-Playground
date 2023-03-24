package com.dineshworkspace.coroutinesplayground

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class CoroutinesViewModel @Inject constructor() : ViewModel() {

    val snackMessage: StateFlow<String> get() = _snackMessage
    private val _snackMessage: MutableStateFlow<String> = MutableStateFlow("")

    /** General points
     * Launch is Fire and Forget the code below the coroutine wont wait for the result to be delivered
     * In Async Await the remaining part of the function will wait for the async function to deliver a result
     */

    /**
     * Simply launching a new coroutine by using launch function and not specifying any Dispatchers
     * If we use viewModelScope.launch or lifecycleScope.launch without specifying any Dispatchers, the coroutine will run in the main thread
     */
    fun launchNewCoroutineOnMainThread() {
        viewModelScope.launch(CoroutineName("Luke")) {
            println(messageBuilder(this.coroutineContext))
        }
    }


    /**
     * Launching a new coroutine by using launch function by specifying Dispatcher.IO
     * Using Dispatchers.IO in a coroutine does not necessarily create a new thread for the coroutine.
     * Dispatchers.IO uses a pool of threads that are optimized for I/O tasks such as reading and writing to the network,
     * disk, or other external devices. When you launch a coroutine on Dispatchers.IO,
     * the coroutine will be executed on one of the threads from this pool.
     * The number of threads in the pool is determined by the system and may vary depending on the number of available processors and other factors.
     */
    fun launchNewCoroutineOnNewThread() {
        viewModelScope.launch(Dispatchers.IO + CoroutineName("Leia")) {
            println(messageBuilder(this.coroutineContext))
        }
    }

    /**
     * Launching a new coroutine by using GlobalScope will use the Dispatchers.Default
     * The Dispatchers.Default dispatcher is optimized for CPU-bound work
     * it uses a shared thread pool that has as many threads as there are CPU cores available.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun launchGlobalCoroutineWithDefaultDispatcher() {
        GlobalScope.launch(CoroutineName("Jyn")) {
            println(messageBuilder(this.coroutineContext))
        }
    }

    /**
     * In this case main thread will be blocked because we are running a heavy process on the main thread
     * It run in main thread by default because we are using viewModelScope.launch and Dispatcher is not specified
     */
    fun launchNewCoroutineOnMainThreadWithHeavyProcessing() {
        viewModelScope.launch(CoroutineName("Heavy Process on Main thread")) {
            println(messageBuilder(this.coroutineContext))
            println(newFun())
        }
        println("This will be printed only after the execution of whats inside launch")
    }


    /**
     * In this case main thread will not be blocked because we are specifying Dispatchers.Default which runs the coroutine in worker thread
     * So println statement wont wait for the completion of launch block
     * launch will be ran by the Worker thread and println Statement will be executed by the Main Thread Simultaneously
     */
    fun launchNewCoroutineOnWorkerThreadWithHeavyProcessing() {
        viewModelScope.launch(Dispatchers.Default + CoroutineName("Heavy Process on worker thread")) {
            println(messageBuilder(this.coroutineContext))
            println(newFun())
        }
        println("This will be printed parallely with the launch scope")
    }


    fun launchTwoCoroutinesOnMainWithDelay() {
        viewModelScope.launch(Dispatchers.Main + CoroutineName("First")) {
            println("Started Execution")
            delay(2000)
            println("First")
        }

        viewModelScope.launch(Dispatchers.Main + CoroutineName("Second")) {
            println("Second")
        }
    }


    /**
     * In this case coroutine will use the dispatcher of the immediate scope it is run from.
     * Even though this function is called with IO Dispatcher, since it is run in ViewModelScope without specifying any dispatcher, it will run in main thread
     */
    fun launchCoroutineInViewModelScope() {
        viewModelScope.launch(CoroutineName("Snowy")) {
            println(messageBuilder(this.coroutineContext))
        }
    }

    suspend fun launchMultipleCoroutinesWithFailure() {
        viewModelScope.launch {
            val job1 = launch {
                println("Job 1 completed")
            }
            val job2 = launch {
                try {
                    throw Exception("Job 2 failed")
                } catch (e: Exception) {
                    println("Job 2 failed")
                }
            }
            val job3 = launch {
                println("Job 3 completed")
            }
        }
        println("All jobs completed")
    }

    /**
     * Async Await will wait for the return value and then only the remaining part of the function will execute
     * In the case below the print statement execution will wait until the code inside the async scope is completed
     */
    suspend fun startAsyncAwaitCoroutine(){
        val result = viewModelScope.async(CoroutineName("Async await simple")) {
            println(messageBuilder(this.coroutineContext))
            val data = newFun()
            println("Async execution completed. Result: $data")
        }
        result.await()
        println("This will not execute till async result is completed")
    }

    /**
     * Async Await is useful when there is dependency of one part of the program in another part ie., when sequence of execution has to be preserved
     * In the case below the print statement execution will wait until the code inside the async scope is completed
     */
    suspend fun startMultipleAsyncCoroutinesWithAwait(){
        val result1 = viewModelScope.async(CoroutineName("Async await 1")) {
            println(messageBuilder(this.coroutineContext))
            val data = newFun()
            println("Async 1 execution completed. Result: $data")
            return@async data
        }
        val result2 = viewModelScope.async(CoroutineName("Async await 2")+ Dispatchers.IO) {
            println(messageBuilder(this.coroutineContext))
            val data = newFun()
            val data2 = newFun()
            newFun()
            newFun()
            println("Async 2 execution completed. Result: $data")
            return@async data+data2
        }
        println("Hi" )
        val result = result1.await()
        println("Result : $result" )
    }


    suspend fun startAsyncAwaitCoroutine1() {
        val deferredResult = viewModelScope.async(CoroutineName("Async Await") + Dispatchers.IO, start = CoroutineStart.LAZY) {
            //_snackMessage.value = "Please wait for 3 seconds"
            println(messageBuilder(this.coroutineContext, false))
            val data23 = newFun()
            println(data23)
            //return@async data23
        }

        //deferredResult.await()

        val deferredResult2 = viewModelScope.async(CoroutineName("Async Await 2") +  Dispatchers.IO, start = CoroutineStart.LAZY) {
            //_snackMessage.value = "Please wait for 3 seconds"
            println(messageBuilder(this.coroutineContext, false))
            val data23 = newFun()
            println(data23)
            //return@async data23
        }

        //deferredResult2.await()
        //println(deferredResult2.await())
    }

    fun newFun(): Double {
        val data = 0.0001232 + 39089238.3434
        val data2 = 1 + data
        val data3 = 1 + data2
        val data4 = 1 + data3
        var data5 = 1 + data4

        for (i in 1..1000000000) {
            data5 += i
        }

        return data5
    }


    private fun messageBuilder(
        coroutineContext: CoroutineContext,
        updateSnackMessage: Boolean = true
    ): String {
        val message =
            "${coroutineContext[CoroutineName.Key]} is executing on thread : ${Thread.currentThread().name}"
        if (updateSnackMessage) _snackMessage.value = message
        return message
    }

}