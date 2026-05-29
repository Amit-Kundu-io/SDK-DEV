package com.amit_kundu_io.sdkdev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.amit_kundu_io.ai_sdk.presentation.MyAi
import com.amit_kundu_io.sdk.core.AiResult
import com.amit_kundu_io.sdk.presentation.SDK
import com.amit_kundu_io.sdkdev.ui.theme.SDKDevTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val model = SDK.ai


            SDKDevTheme {
                var name by remember { mutableStateOf("Ai Stop") }

                val cc = rememberCoroutineScope()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = name,
                        modifier = Modifier.padding(innerPadding),
                        onClick = {
                            cc.launch {
                                name = "Loading...."
                                val result = model.generate("Explain Clean Architecture")
                                when(result){
                                    is AiResult.Error ->{}
                                    is AiResult.Success-> {
                                        name = result.data
                                    }
                                }

                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(name)
        Button(
            onClick = onClick
        ) {
            Text("Ai Run")
        }
    }
}

