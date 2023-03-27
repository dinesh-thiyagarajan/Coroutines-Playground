package com.dineshworkspace.coroutinesplayground

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Preview
@Composable
fun CoroutineScreen(coroutinesViewModel: CoroutinesViewModel = hiltViewModel()) {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    SnackBar(coroutinesViewModel, scaffoldState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                coroutinesViewModel.launchNewCoroutineOnMainThread()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Launch Coroutine on Main Thread")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = { coroutinesViewModel.launchNewCoroutineOnNewThread() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Launch Coroutine on New Thread")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = { coroutinesViewModel.launchGlobalCoroutineWithDefaultDispatcher() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Launch GlobalScope Coroutine w/o Dispatchers")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = { coroutinesViewModel.launchNewCoroutineOnMainThreadWithHeavyProcessing() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Launch Coroutine with Heavy processing on Main thread")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = { coroutinesViewModel.launchNewCoroutineOnWorkerThreadWithHeavyProcessing() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Launch Coroutine with Heavy processing on Worker thread")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = { coroutinesViewModel.launchTwoCoroutinesOnMainWithDelay() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Launch Two Coroutines on Main Thread with Delay")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    coroutinesViewModel.launchCoroutineInViewModelScope()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Launch Coroutine from IO Dispatcher and execute in ViewModelScope")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    coroutinesViewModel.startAsyncAwaitCoroutine()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Simple async with await")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    coroutinesViewModel.startMultipleAsyncCoroutinesWithAwaitOnLongProcess()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Multiple async with await on long process")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    coroutinesViewModel.startMultipleAsyncCoroutinesWithAwaitOnShortProcess()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Multiple async with await on short process")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    coroutinesViewModel.startMultipleAsyncCoroutinesWithAwaitOnBothProcess()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Multiple async with await on both process")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    coroutinesViewModel.startDependentAsyncCoroutinesWithAwaitOnBothProcess_WrongWay()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Multiple dependent async - wrong way")
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    coroutinesViewModel.startDependentAsyncCoroutinesWithAwaitOnBothProcess_RightWay()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Multiple dependent async - right way")
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SnackBar(
    coroutinesViewModel: CoroutinesViewModel,
    scaffoldState: ScaffoldState,
    content: Composable? = null
) {
    LaunchedEffect(Unit) {
        coroutinesViewModel.snackMessage.collectLatest {
            if (it.isNotEmpty()) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Long,
                    actionLabel = "Ok"
                )
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        content =
        { content }
    )
}

