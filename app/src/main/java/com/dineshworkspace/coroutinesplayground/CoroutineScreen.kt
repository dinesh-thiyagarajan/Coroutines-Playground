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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Preview
@Composable
fun CoroutineScreen(coroutinesViewModel: CoroutinesViewModel = hiltViewModel()) {

    val scaffoldState = rememberScaffoldState()
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
                    duration = SnackbarDuration.Long
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

