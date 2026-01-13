package com.example.otel_playground

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import com.example.otel_playground.ui.compose_ui.MainOtelButton
import com.example.otel_playground.ui.theme.OtelplaygroundTheme
import com.example.otel_playground.xml.XmlButtonFragment
import io.opentelemetry.api.trace.SpanKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // FEATURE 1 (MANUAL): OpenTelemetry LOG EVENT
        // This creates a LogRecord using the OpenTelemetry Logs API.
        // It is NOT android-log instrumentation.
        // This is useful for structured domain events (e.g. vehicle state changes).

        // FEATURE 5 (AUTOMATIC): SESSIONS
        // The session ID is automatically created and managed by the OTel Android SDK.
        Log.i(
            "OTEL_DEMO",
            "Current Session ID: ${OtelApplication.rum?.getRumSessionId()}"
        )

        setContent {
            OtelplaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DemoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // FEATURE 1 (AUTOMATIC): ANDROID-LOG
        // If android-log instrumentation is enabled,
        // this Log.i() is transformed into an OTel LogRecord.
        Log.i("OTEL_DEMO", "Activity onStart")
    }

    override fun onResume() {
        super.onResume()

        // FEATURE 5: SESSIONS
        // App moved to foreground.
        // Session is resumed or continued.
        Log.i("OTEL_DEMO", "Activity onResume")
    }

    override fun onPause() {
        super.onPause()

        // FEATURE 5: SESSIONS
        // App moved to background.
        // Session may pause or expire depending on configuration.
        Log.i("OTEL_DEMO", "Activity onPause")
    }
}


@Composable
fun DemoScreen(modifier: Modifier = Modifier) {
    var networkResponse by remember { mutableStateOf("No request made yet") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val responseScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "OpenTelemetry Android Demo",
            style = MaterialTheme.typography.headlineSmall
        )

        Divider()

        // FEATURE 4 (AUTOMATIC): COMPOSE-CLICK
        // Clicks are captured automatically via compose-click instrumentation
        Text("Compose Click (Image Button)", style = MaterialTheme.typography.titleMedium)
        MainOtelButton(icon = painterResource(id = R.drawable.mercedes_logo))

        Divider()

        // FEATURE 3 (AUTOMATIC): VIEW-CLICK (XML)
        // Clicks inside traditional Android Views are captured
        // by view-click instrumentation.
        Text("XML View Click (Fragment)", style = MaterialTheme.typography.titleMedium)
        XmlFragmentContainer()

        Divider()

        // FEATURE 4 (AUTOMATIC) + FEATURE 1 (MANUAL)
        Text("Compose Button + Manual Event", style = MaterialTheme.typography.titleMedium)
        Button(
            onClick = {
                Log.i("OTEL_DEMO", "Compose button clicked")

                // Manual domain event
//                OtelApplication
//                    .eventBuilder("demo.app", "compose_button.clicked")
//                    .setAttribute("screen", "DemoScreen")
//                    .emit()
            },
            modifier = Modifier.semantics {
                onClick("ComposeDemoButton") { true }
            }
        ) {
            Text("Tracked Compose Button")
        }

        Divider()

        // FEATURE 2 (AUTOMATIC): HTTPURLCONNECTION
        Text("HTTP Tracing", style = MaterialTheme.typography.titleMedium)
        Button(
            enabled = !isLoading,
            onClick = {
                Log.i("OTEL_DEMO", "Starting HTTP request")
                isLoading = true

                coroutineScope.launch {
                    networkResponse = makeNetworkRequest()
                    isLoading = false
                }
            }
        ) {
            Text(if (isLoading) "Loading..." else "Make HTTP Request")
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .verticalScroll(responseScrollState)
        ) {
            Text(
                text = networkResponse,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Divider()

        // FEATURE 5: SESSIONS
        Text("Session Information", style = MaterialTheme.typography.titleMedium)
        SessionInfo()
    }
}

@Composable
fun XmlFragmentContainer() {
    val scope = "otel.demo.app.xml.activity.start"
//    val tracer = tracer(scope)
//    val span =
//        tracer
//            ?.spanBuilder("XML fragment connect to compose UI")
//            ?.setSpanKind(SpanKind.INTERNAL)
//            ?.startSpan()
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(CircleShape)
        ,
        factory = { context ->
            FragmentContainerView(context).apply {
                id = View.generateViewId()

                val activity = context as FragmentActivity
                activity.supportFragmentManager
                    .beginTransaction()
                    .replace(id, XmlButtonFragment())
                    .commit()
            }
        }
    )

//    span?.end()
}

@Composable
fun SessionInfo() {
    val sessionId = OtelApplication.rum?.getRumSessionId() ?: "Not available"

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Current Session ID:",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                sessionId,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "app in background for 30s to test session timeout!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }

    Button(
        onClick = {
            // FEATURE 1: ANDROID-LOG
            Log.i("OTEL_DEMO", "Session info requested")

            // Emit session info event
//            OtelApplication
//                .eventBuilder("demo.app", "session_info.requested")
//                .setAttribute("session_id", sessionId)
//                .emit()
        },
        modifier = Modifier.semantics {
            onClick("SessionInfoButton") { true }
        }
    ) {
        Text("Log Session Info")
    }
}

// FEATURE 2: HTTPURLCONNECTION
// This function demonstrates automatic HTTP tracing
suspend fun makeNetworkRequest(): String = withContext(Dispatchers.IO) {
    try {
        // FEATURE 2: OTel automatically instruments HttpURLConnection!
        // Creates a span with request/response details
        val url = URL("https://jsonplaceholder.typicode.com/posts/1")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            // OTel captures:
            // - Request start time
            // - HTTP method, URL
            // - Response status code
            // - Response time
            // - Any errors

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                "Success! Status: $responseCode\n\n${response.take(200)}..."
            } else {
                "Error: HTTP $responseCode"
            }
        } finally {
            connection.disconnect()
        }
    } catch (e: Exception) {
        // FEATURE 1: ANDROID-LOG
        // Errors are automatically captured
        Log.e("OTEL_DEMO", "Network request failed", e)
        "Error: ${e.message}"
    }
}