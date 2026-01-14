package com.example.otel_playground

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import io.opentelemetry.android.Incubating
import io.opentelemetry.android.OpenTelemetryRum
import io.opentelemetry.android.agent.OpenTelemetryRumInitializer
import io.opentelemetry.api.common.AttributeKey.stringKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.logs.LogRecordBuilder
import io.opentelemetry.api.logs.LoggerProvider
import io.opentelemetry.api.metrics.LongCounter
import io.opentelemetry.api.trace.Tracer
import kotlin.time.Duration.Companion.hours

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class OtelApplication : Application() {

    @OptIn(Incubating::class)
    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "Initializing the opentelemetry-android-agent")

        try {
            rum = OpenTelemetryRumInitializer.initialize(
                context = this,
                configuration = {
                    httpExport {
                        baseUrl = "http://10.0.2.2:4318"
                    }

                    globalAttributes {
                        Attributes.of(stringKey("toolkit"), "jetpack compose")
                    }

                    session {
                        backgroundInactivityTimeout = 15.minutes
                        maxLifetime = 4.hours
                    }

                    instrumentations {
                        anrReporter { enabled(true) }
                        crashReporter { enabled(true) }
                        networkMonitoring { enabled(true) }
                        slowRenderingReporter { enabled(true) }
                        activity { enabled(true) }
                        fragment { enabled(true) }
                        screenOrientation { enabled(true) }

                    }
                }
            )

            Log.i(TAG, "RUM session started: ${rum?.getRumSessionId()}")
            Log.d(TAG, "It is generating")

        } catch (e: Exception) {
            Log.e(TAG, "OTel initialization failed", e)
        }
    }

    companion object {
        var rum: OpenTelemetryRum? = null
    }
}
