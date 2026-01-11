pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

    }
}

//includeBuild("..") {
//    dependencySubstitution {
//        substitute(module("io.opentelemetry.android:android-agent"))
//            .using(project(":android-agent"))
//        substitute(module("io.opentelemetry.android.instrumentation:compose-click"))
//            .using(project(":instrumentation:compose:click"))
//        substitute(module("io.opentelemetry.android.instrumentation:sessions"))
//            .using(project(":instrumentation:sessions"))
//    }
//}
rootProject.name = "otel-playground"
include(":app")
 