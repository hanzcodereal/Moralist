import org.gradle.jvm.tasks.Jar

plugins {
    `java-library`
    alias(libs.plugins.protobuf)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

dependencies {
    api(libs.protobuf.javalite)
}

sourceSets {
    named("main") {
        proto {
            srcDir(rootProject.file("metroproto"))
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobufJava.get()}"
    }
    generateProtoTasks {
        all().configureEach {
            builtins {
                named("java") { option("lite") }
            }
        }
    }
}

tasks.named<Jar>("jar") {
    exclude("**/*.proto")
}
