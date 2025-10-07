plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("maven-publish")
}

version = "1.0.0"

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
                
                groupId = "com.github.rutherfn"
                artifactId = "core"
                version = project.version.toString()
                
                pom {
                    name.set("Voice Flow")
                    description.set("A voice command library for Android")
                    url.set("https://github.com/rutherfn/voice-flow")
                    
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("rutherfn")
                            name.set("Nicholas Rutherford")
                        }
                    }
                    
                    scm {
                        connection.set("scm:git:git://github.com/rutherfn/voice-flow.git")
                        developerConnection.set("scm:git:ssh://github.com:rutherfn/voice-flow.git")
                        url.set("https://github.com/rutherfn/voice-flow")
                    }
                }
            }
        }
    }
}