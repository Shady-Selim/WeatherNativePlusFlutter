package com.weatherapp.nativeplusflutter

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

class MainActivity : AppCompatActivity() {
    private val ENGINE_ID = "weather_engine"
    private val CHANNEL = "com.weatherapp/navigation"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Flutter Engine
        val flutterEngine = FlutterEngine(this)
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache.getInstance().put(ENGINE_ID, flutterEngine)

        setContent {
            var currentScreen by remember { mutableStateOf("WELCOME") }

            // 2. Setup Method Channel for back navigation
            LaunchedEffect(Unit) {
                MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
                    if (call.method == "goBack") {
                        currentScreen = "WELCOME"
                        result.success(null)
                    } else {
                        result.notImplemented()
                    }
                }
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "WELCOME" -> WelcomeScreen(onNavigate = { currentScreen = "FLUTTER" })
                        "FLUTTER" -> FlutterEmbeddingScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onNavigate: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Native App", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onNavigate) {
            Text(text = "Test Flutter")
        }
    }
}

@Composable
fun FlutterEmbeddingScreen() {
    val context = LocalContext.current
    val fragmentActivity = context as? FragmentActivity
    val fragmentContainerId = remember { android.view.View.generateViewId() }

    AndroidView(
        factory = { ctx ->
            FragmentContainerView(ctx).apply {
                id = fragmentContainerId
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            fragmentActivity?.supportFragmentManager?.let { fragmentManager ->
                if (fragmentManager.findFragmentById(fragmentContainerId) == null) {
                    val flutterFragment = FlutterFragment.withCachedEngine("weather_engine")
                        .build<FlutterFragment>()
                    
                    fragmentManager.beginTransaction()
                        .replace(fragmentContainerId, flutterFragment)
                        .commit()
                }
            }
        }
    )
}
