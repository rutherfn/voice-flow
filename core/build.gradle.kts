import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("maven-publish")
}

val coreProperties = Properties()
val corePropertiesFile = file("gradle.properties")
if (corePropertiesFile.exists()) {
    coreProperties.load(FileInputStream(corePropertiesFile))
}

val githubUsername = coreProperties.getProperty("GITHUB_USERNAME") ?: project.findProperty("GITHUB_USERNAME") as String? ?: System.getenv("GITHUB_USERNAME")
val githubToken = coreProperties.getProperty("GITHUB_TOKEN") ?: project.findProperty("GITHUB_TOKEN") as String? ?: System.getenv("GITHUB_TOKEN")
val libraryVersion = coreProperties.getProperty("LIBRARY_VERSION") ?: project.findProperty("LIBRARY_VERSION") as String? ?: System.getenv("LIBRARY_VERSION")

android {
    namespace = "com.nicholas.rutherford.voice.flow.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.process)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                
                groupId = "com.nicholas.rutherford"
                artifactId = "voice-flow"
                version = libraryVersion
            }
        }
        
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/rutherfn/voice-flow")
                credentials {
                    username = githubUsername
                    password = githubToken
                }
            }
        }
    }
}