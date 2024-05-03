plugins {
    id("com.android.library")
    id("com.vanniktech.maven.publish") version "0.28.0"
}

android {
    namespace = "com.offer18.sdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        aarMetadata {
            minCompileSdk = 23
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isJniDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isJniDebuggable = true
        }
    }
}

mavenPublishing {
    coordinates("io.github.ganesh-o18", "android-sdk", "0.0.7")
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    pom {
        name.set("android-sdk")
        description.set(" Android SDK")
        inceptionYear.set("2024")
        url.set("https://gitlab.com/ganesh-o18/sdk")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("ganesh")
                name.set("ganesh")
                url.set("https://github.io/ganesh-o18")
            }
        }
    }
    signAllPublications()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}