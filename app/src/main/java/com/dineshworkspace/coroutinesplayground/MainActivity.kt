package com.dineshworkspace.coroutinesplayground

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dineshworkspace.coroutinesplayground.ui.theme.CoroutinesPlaygroundTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CoroutinesPlaygroundTheme {
                val coroutinesViewModel: CoroutinesViewModel = hiltViewModel()
                val scaffoldState = rememberScaffoldState()
                SnackBar(coroutinesViewModel, scaffoldState)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        content =
                        { CoroutineScreen() }
                    )
                }
            }
        }
    }
}
