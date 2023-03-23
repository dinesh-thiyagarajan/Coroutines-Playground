package com.dineshworkspace.coroutinesplayground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Preview
@Composable
fun CoroutineScreen(coroutinesViewModel: CoroutinesViewModel = hiltViewModel()) {

    Column(modifier = Modifier.fillMaxSize()) {

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

    }
}

