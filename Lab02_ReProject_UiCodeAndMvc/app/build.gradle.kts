plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // 包名：https://www.sicnu.edu.cn/ 的反域名（去掉 www 后逐级反转 → cn.edu.sicnu）+ ReProject
    namespace = "cn.edu.sicnu.reproject"
    compileSdk = 36

    defaultConfig {
        applicationId = "cn.edu.sicnu.reproject"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        resourceConfigurations += listOf("zh")
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

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
