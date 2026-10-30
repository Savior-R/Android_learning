plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // 包名：sicnu.edu.cn 的反域名（cn.edu.sicnu）+ CardGame
    namespace = "cn.edu.sicnu.reproject.cardgame.rv"
    compileSdk = 36

    defaultConfig {
        applicationId = "cn.edu.sicnu.reproject.cardgame.rv"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // 单元测试与 UI 测试
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    kotlinOptions {
        jvmTarget = "17"
    }

    // 让单元测试可以引用 Android 资源 id（R.string.xxx）
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    buildFeatures {
        viewBinding = false
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // 实验五：牌桌改用 RecyclerView + Adapter
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    // 实验指导要求使用 MaterialComponents 主题，否则 Button 设置背景无效
    implementation("com.google.android.material:material:1.12.0")

    // 单元测试（JVM 上运行，测试 Model 层逻辑）
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.6.1")

    // UI 测试（设备 / 模拟器上运行）
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test:core-ktx:1.6.1")

    // 统一 Kotlin stdlib 版本，避免传递依赖中的 Duplicate class 冲突
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
