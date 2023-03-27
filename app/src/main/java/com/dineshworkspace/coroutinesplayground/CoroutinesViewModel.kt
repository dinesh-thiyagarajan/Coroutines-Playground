package com.dineshworkspace.coroutinesplayground

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class CoroutinesViewModel @Inject constructor(val heavyProcessor: HeavyProcessor) : ViewModel() {

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
        viewModelScope.launch(Dispatchers.Default + CoroutineName("Leia")) {
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
            println(heavyProcessor.processDoubleValues())
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
            println(heavyProcessor.processDoubleValues())
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

    /**
     * Async Await will wait for the return value and then only the remaining part of the function will execute
     * In the case below the print statement execution will wait until the code inside the async scope is completed
     */
    suspend fun startAsyncAwaitCoroutine(){
        val result = viewModelScope.async(CoroutineName("Async await simple")) {
            println(messageBuilder(this.coroutineContext))
            val data = heavyProcessor.processDoubleValues()
            println("Async execution completed. Result: $data")
        }
        result.await()
        println("This will not execute till async result is completed")
    }

    /**
     * Async Await is useful when there is dependency of one part of the program in another part ie., when sequence of execution has to be preserved
     * In the case below result will be calculated only after executing process1 since process1.await() is used. Process 1 and 2 will be executed simultaneously
     * as they are run on different dispatchers/threads. In this case we know that process2 will complete its execution before process 1 so result will
     * not be impacted
     */
    @SuppressLint("UseValueOf")
    suspend fun startMultipleAsyncCoroutinesWithAwaitOnLongProcess(){
        var result1 = 0.0
        var result2 = 0.0

        val process1 = viewModelScope.async(CoroutineName("Async await 1") + Dispatchers.Default) {
            println(messageBuilder(this.coroutineContext))
            result1 = heavyProcessor.processDoubleValues(3)
            println("Async 1 execution completed. Result: $result1")
            return@async result1
        }

        val process2 = viewModelScope.async(CoroutineName("Async await 2")+ Dispatchers.Default) {
            println(messageBuilder(this.coroutineContext))
            result2 = heavyProcessor.processDoubleValues()
            println("Async 2 execution completed. Result: $result2")
            return@async result2
        }

        println("Hi from main thread" )
        process1.await()
        println("Result : ${result1+result2}" )
    }


    /**
    In this case process2 will take more time than process 1 but since await is not called on process2, result will be impacted. Use await wisely
     */
    suspend fun startMultipleAsyncCoroutinesWithAwaitOnShortProcess(){
        var result1 = 0.0
        var result2 = 0.0

        val process1 = viewModelScope.async(CoroutineName("Async await 1") + Dispatchers.Default) {
            println(messageBuilder(this.coroutineContext))
            result1 = heavyProcessor.processDoubleValues()
            println("Async 1 execution completed. Result: $result1")
            return@async result1
        }

        val process2 = viewModelScope.async(CoroutineName("Async await 2")+ Dispatchers.Default) {
            println(messageBuilder(this.coroutineContext))
            result2 = heavyProcessor.processDoubleValues(3)
            println("Async 2 execution completed. Result: $result2")
            return@async result2
        }

        println("Hi from main thread" )
        process1.await()
        println("Result : ${result1+result2}" )
    }

    /**
    In realtime since we would not know the amount of time taken by each process, it is wise that we use await on both processes when end result is dependedent
     on both processes.
     */
    suspend fun startMultipleAsyncCoroutinesWithAwaitOnBothProcess(){
        var result1 = 0.0
        var result2 = 0.0

        val process1 = viewModelScope.async(CoroutineName("Async await 1") + Dispatchers.Default) {
            println(messageBuilder(this.coroutineContext))
            result1 = heavyProcessor.processDoubleValues()
            println("Async 1 execution completed. Result: $result1")
            return@async result1
        }

        val process2 = viewModelScope.async(CoroutineName("Async await 2")+ Dispatchers.Default) {
            println(messageBuilder(this.coroutineContext))
            result2 = heavyProcessor.processDoubleValues(3)
            println("Async 2 execution completed. Result: $result2")
            return@async result2
        }

        println("Hi from main thread" )
        process1.await()
        process2.await()
        println("Result : ${result1+result2}" )
    }

    /**
     * It is important to wisely place the await statement, in this case process 2 is dependent on result 1 but await is placed after execution of
     * process2 has begun. Hence result will be impacted
     */
    suspend fun startDependentAsyncCoroutinesWithAwaitOnBothProcess_WrongWay(){
        var result1 = 0.0
        var result2 = 0.0

        val process1 = viewModelScope.async(CoroutineName("Async await 1") + Dispatchers.Default) {
            println(messageBuilder(this.coroutineContext))
            result1 = heavyProcessor.processDoubleValues(3)
            println("Async 1 execution completed. Result: $result1")
            return@async result1
        }

        val process2 = viewModelScope.async(CoroutineName("Async await 2")+ Dispatchers.Default) {
            println(messageBuilder(this.coroutineContext))
            result2 = heavyProcessor.processDoubleValues()
            println("Async 2 execution for step 1 completed. Result: $result2")
            println("Dependent data from previous process: $result1")
            result2 += result1
            println("Async 2 execution completed. Result: $result2")
            return@async result2
        }

        println("Hi from main thread" )
        process1.await()
        process2.await()
    }

    /**
     * Since process1.await is called before process2 execution, process2 will be started only after result1 is computed. Hence end result
     * will not be impacted
     */
    suspend fun startDependentAsyncCoroutinesWithAwaitOnBothProcess_RightWay(){
        var result1 = 0.0
        var result2 = 0.0

        val process1 = viewModelScope.async(CoroutineName("Async await 1") + Dispatchers.Default) {
            println(messageBuilder(this.coroutineContext))
            result1 = heavyProcessor.processDoubleValues(3)
            println("Async 1 execution completed. Result: $result1")
            return@async result1
        }

        process1.await()

        val process2 = viewModelScope.async(CoroutineName("Async await 2")+ Dispatchers.Default) {
            println(messageBuilder(this.coroutineContext))
            result2 = heavyProcessor.processDoubleValues()
            println("Async 2 execution for step 1 completed. Result: $result2")
            println("Dependent data from previous process: $result1")
            result2 += result1
            println("Async 2 execution completed. Result: $result2")
            return@async result2
        }

        println("Hi from main thread" )
        process2.await()
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