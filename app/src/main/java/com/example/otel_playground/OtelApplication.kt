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
import timber.log.Timber
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class OtelApplication : Application() {


    @OptIn(Incubating::class)
    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "Initializing the opentelemetry-android-agent")

        // 10.0.2.2 is a special binding to the host running the emulator
        try {
            rum = OpenTelemetryRumInitializer.initialize(
                context = this@OtelApplication,
                configuration = {
                    httpExport {
                        baseUrl = "http://10.0.2.2:4318"
                    }
                    globalAttributes {
                        Attributes.of(stringKey("toolkit"), "jetpack compose")
                    }
                    session {
                        backgroundInactivityTimeout = 0.5.minutes
                        maxLifetime = 1.minutes
                    }

                    instrumentations {
                        // ANR (Application Not Responding) detection
                        anrReporter {
                           enabled(true)
                        }

                        // Crash reporting
                        crashReporter {
                            enabled(true)
                        }

                        // Network state monitoring
                        networkMonitoring {
                            enabled(true)
                        }

                        // Slow rendering detection
                        slowRenderingReporter {
                            enabled(true)
                        }

                        // Activity lifecycle
                        activity {
                            enabled(true)
                        }

                        // Fragment lifecycle
                        fragment {
                            enabled(true)
                        }

                        // Screen orientation changes
                        screenOrientation {
                            enabled(true)
                        }


                    }

                }
            )
            Log.d(TAG, "RUM session started: " + rum?.getRumSessionId())
            setupTimber()
            Timber.d("Timber initialize successfully and see in telemetry logs file")
        } catch (e: Exception) {
            Log.e(TAG, "Oh no!", e)
        }

    }


    private fun setupTimber() {
        // Plant debug tree for logcat
        Timber.plant(Timber.DebugTree())

        // Plant OTel tree to export logs
        Timber.plant(object : Timber.Tree() {
            val logger = rum?.openTelemetry?.logsBridge?.get("example-logger")

            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                logger?.logRecordBuilder()?.setBody(message)?.emit()
            }
        })

        Log.d(TAG, "Timber configured with OpenTelemetry")
    }

    companion object {
        var rum: OpenTelemetryRum? = null

        fun tracer(name: String): Tracer? {
            Log.d("tracer: ",name)
            return rum?.openTelemetry?.tracerProvider?.get(name)
        }

        fun counter(name: String): LongCounter? {
            Log.d("counter: ",name)
            return rum?.openTelemetry?.meterProvider?.get("demo.app")?.counterBuilder(name)
                ?.build()
        }

        fun eventBuilder(scopeName: String, eventName: String): LogRecordBuilder {
            Log.d(scopeName,eventName)
            if (rum == null) {
                return LoggerProvider.noop().get("noop").logRecordBuilder()
            }
            val logger = rum!!.openTelemetry.logsBridge.loggerBuilder(scopeName).build()

            return logger.logRecordBuilder().setEventName(eventName).setBody("This is for testing purposes")
        }
    }
}
