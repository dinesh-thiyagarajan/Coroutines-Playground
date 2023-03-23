package com.dineshworkspace.coroutinesplayground

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class CoroutinesViewModel @Inject constructor() : ViewModel() {

    val snackMessage: StateFlow<String> get() = _snackMessage
    private val _snackMessage: MutableStateFlow<String> = MutableStateFlow("")

    /**
     * Simply launching a new coroutine by using launch function and not specifying any Dispatchers
     * This creates a new Coroutine and run the coroutine in the caller thread, in this case it will be main thread
     */
    fun launchNewCoroutineOnMainThread() {
        viewModelScope.launch(CoroutineName("Luke")) {
            val message = messageBuilder(this.coroutineContext)
            _snackMessage.value = message
            println(message)
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
            val message = messageBuilder(this.coroutineContext)
            _snackMessage.value = message
            println(message)
        }
    }

    private fun messageBuilder(coroutineContext: CoroutineContext) = "${coroutineContext[CoroutineName.Key]} is executing on thread : ${Thread.currentThread().name}"

}