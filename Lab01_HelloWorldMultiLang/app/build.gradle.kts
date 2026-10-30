plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.helloworldmultilang"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.helloworldmultilang"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // 支持中、英、第三语言（意大利语），默认（兜底）放在 res/values
        resourceConfigurations += listOf("zh", "en", "it")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")

    // 统一 Kotlin stdlib 版本，避免 kotlin-stdlib 与 kotlin-stdlib-jdk7/jdk8
    // 同时存在于传递依赖中导致的 Duplicate class 冲突
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24") {
            because("appcompat 传递依赖引入了不同版本的 kotlin-stdlib")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.24") {
            because("统一 jdk7 扩展包版本")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24") {
            because("统一 jdk8 扩展包版本")
        }
    }
}
