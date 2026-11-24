@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.process.CommandLineArgumentProvider
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.File
import java.net.URI
import java.util.Base64
import java.util.Locale
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
}

compose {
    dependencyCompatibility {
        exclude("io.coil-kt.coil3")
    }
}

val appVersion = providers.gradleProperty("appVersion").get()
val innerTubeXVersion = providers.gradleProperty("innerTubeXVersion").get()
val bundledLanguageTags =
    file("src/commonMain/composeResources")
        .listFiles()
        .orEmpty()
        .asSequence()
        .filter { it.isDirectory && it.name.startsWith("values-") && it.name != "values-xml" }
        .map { directory ->
            val qualifier = directory.name.removePrefix("values-")
            val languageTag =
                if (qualifier.startsWith("b+")) {
                    qualifier.removePrefix("b+").replace('+', '-')
                } else {
                    qualifier.replace(Regex("-r(?=[A-Z]{2}(?:$|-))"), "-")
                }
            Locale.forLanguageTag(languageTag).toLanguageTag()
        }.filter { it.isNotBlank() && it != "und" }
        .distinct()
        .sorted()
        .toList()

kotlin {
    android {
        namespace = "com.moralist.shared"
        compileSdk {
            version = release(37) { minorApiLevel = 1 }
        }
        minSdk = 24
        androidResources.enable = true
        lint { ignoreTestSources = true }
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        }
        getByName("commonMain") {
            dependencies {
                implementation("com.github.nyxiereal.glyphmind:glyphmind:0.1.0")
                api("com.github.MetrolistGroup.innertubex:innertubex:$innerTubeXVersion")
                api(libs.runtime)
                api(libs.foundation)
                api(libs.androidx.navigationevent.compose)
                api(libs.material3)
                api(libs.material3.adaptive)
                api(libs.material3.adaptive.navigation.suite)
                api(libs.components.resources)
                implementation(libs.ui.tooling.preview)
                implementation(libs.compose.reorderable)
                implementation(libs.swipe)
                api(libs.markdown.renderer)
                implementation(libs.markdown.renderer.m3)
                implementation(libs.markdown.renderer.coil3)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.encoding)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.serialization.json)

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)

                implementation(libs.datastore)

                implementation(libs.room.runtime)

                implementation(libs.koin.core)

                api(libs.androidx.navigation3.runtime)

                api(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.datastore)

                implementation(libs.materialKolor)

                api(libs.shimmer)

                implementation("io.coil-kt.coil3:coil-compose:${libs.versions.coil.get()}") {
                    exclude(group = "org.jetbrains.skiko", module = "skiko")
                }
                implementation("io.coil-kt.coil3:coil-network-core:${libs.versions.coil.get()}")
            }
        }
        getByName("androidMain") {
            dependencies {
                implementation(project(":listenTogetherProto"))
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.cio)
                implementation(libs.okhttp)
                implementation(libs.multiplatform.settings.coroutines)
                implementation(libs.androidx.core.ktx)
                implementation(libs.browser)
                api(libs.room.runtime)
                implementation(libs.coil.network.okhttp)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.server.cio)
                implementation(libs.ktor.server.websockets)
                implementation(libs.timber)

                implementation(libs.media3.exoplayer)
                implementation(libs.media3.common)
                implementation(libs.media3.datasource)
            }
        }
    }

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }
}

// Alias folder lama ("in", "iw") ke kode bahasa modern ("id", "he") untuk Compose Resources.
val prepareComposeResourceAliases =
    tasks.register<Sync>("prepareComposeResourceAliases") {
        val catalog = layout.projectDirectory.dir("src/commonMain/composeResources")
        from(catalog)
        mapOf("in" to "id", "iw" to "he").forEach { (legacy, modern) ->
            from(catalog.dir("values-$legacy")) { into("values-$modern") }
        }
        into(layout.buildDirectory.dir("generated/composeResourceAliases"))
    }

compose.resources {
    packageOfResClass = "com.moralist.shared.generated.resources"
    publicResClass = true
    customDirectory("commonMain", layout.dir(prepareComposeResourceAliases.map { it.destinationDir }))
}

tasks.matching { it.name == "generateResourceAccessorsForCommonMain" }.configureEach {
    outputs.cacheIf { true }
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

fun configValue(name: String): String =
    providers
        .gradleProperty(name)
        .filter { it.isNotEmpty() }
        .orElse(providers.environmentVariable(name).filter { it.isNotEmpty() })
        .orElse(providers.provider { localProperties.getProperty(name).orEmpty() }.filter { it.isNotEmpty() })
        .orElse(providers.provider { "" })
        .get()
        .trim()

val generateBuildConfig =
    tasks.register("generateBuildConfig") {
        val outputDir =
            layout.buildDirectory
                .dir("generated/source/config/main/kotlin")
                .get()
                .asFile

        inputs.files(localPropertiesFile)

        val lastFmKey = configValue("LASTFM_API_KEY")
        val lastFmSecret = configValue("LASTFM_SECRET")
        val lyprioBaseUrl = configValue("LYPRIO_BASE_URL")
        val lyprioHmacKey = configValue("LYPRIO_HMAC_KEY")

        val isReleaseTask =
            gradle.startParameter.taskNames.any { name ->
                name.contains("Release", ignoreCase = true) ||
                    (name.contains("package", ignoreCase = true) && !name.contains("Debug", ignoreCase = true)) ||
                    name.contains("Distributable", ignoreCase = true)
            }
        val debugValue: Boolean =
            providers
                .gradleProperty("moralist.debug")
                .orNull
                ?.toBooleanStrictOrNull() ?: !isReleaseTask

        require(lyprioBaseUrl.isBlank() == lyprioHmacKey.isBlank()) {
            "LYPRIO_BASE_URL and LYPRIO_HMAC_KEY must be configured together"
        }
        if (lyprioBaseUrl.isNotBlank()) {
            val uri = URI(lyprioBaseUrl)
            require(
                !uri.host.isNullOrBlank() &&
                    uri.rawUserInfo == null &&
                    uri.rawQuery == null &&
                    uri.rawFragment == null &&
                    (
                        (uri.scheme == "https" && (uri.port == -1 || uri.port == 443)) ||
                            (debugValue && uri.scheme == "http")
                    ),
            ) { "LYPRIO_BASE_URL must use HTTPS on port 443; HTTP is allowed only in debug builds" }
            require(Base64.getDecoder().decode(lyprioHmacKey).size == 32) {
                "LYPRIO_HMAC_KEY must be a base64-encoded 32-byte key"
            }
        }
        inputs.property("lastFmKey", lastFmKey)
        inputs.property("lastFmSecret", lastFmSecret)
        inputs.property("lyprioBaseUrl", lyprioBaseUrl)
        inputs.property("lyprioHmacKey", lyprioHmacKey)
        val sourceRevision =
            providers
                .gradleProperty("moralist.sourceRevision")
                .orElse(providers.environmentVariable("GITHUB_SHA"))
                .map(String::trim)
                .filter { it.matches(Regex("[0-9a-fA-F]{40}")) }
                .orElse("")
                .get()
        inputs.property("appVersion", appVersion)
        inputs.property("sourceRevision", sourceRevision)
        inputs.property("bundledLanguageTags", bundledLanguageTags.joinToString(","))
        inputs.property("debug", debugValue)

        outputs.dir(outputDir)
        outputs.cacheIf { false }

        doLast {
            val configFile = outputDir.resolve("com/moralist/shared/BuildConfig.kt")
            configFile.parentFile.mkdirs()
            configFile.writeText(
                """
                package com.moralist.shared

                object BuildConfig {
                    const val LASTFM_API_KEY = "${inputs.properties["lastFmKey"]}"
                    const val LASTFM_SECRET = "${inputs.properties["lastFmSecret"]}"
                    const val LYPRIO_BASE_URL = "${inputs.properties["lyprioBaseUrl"]}"
                    const val LYPRIO_HMAC_KEY = "${inputs.properties["lyprioHmacKey"]}"
                    const val APP_VERSION = "${inputs.properties["appVersion"]}"
                    const val SOURCE_REVISION = "${inputs.properties["sourceRevision"]}"
                    const val BUNDLED_LANGUAGE_TAGS = "${inputs.properties["bundledLanguageTags"]}"
                    const val DEBUG = ${inputs.properties["debug"]}
                }
                """.trimIndent(),
            )
        }
    }

kotlin {
    sourceSets {
        getByName("commonMain") {
            kotlin.srcDir(generateBuildConfig)
        }
    }
}

dependencies {
    add("androidRuntimeClasspath", libs.ui.tooling)
    add("kspAndroid", libs.room.compiler)
}

class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDirectory: File,
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> = listOf("room.schemaLocation=${schemaDirectory.path}")
}

ksp {
    arg(RoomSchemaArgProvider(layout.projectDirectory.dir("schemas").asFile))
}
