plugins {
    id("com.android.application")
}

android {
    // 包名：https://www.sicnu.edu.cn/ 的反域名 cn.edu.sicnu + ReProject + multi
    // （multi 用于与实验二工程 cn.edu.sicnu.reproject 区分，两个 APK 可同时安装）
    namespace = "cn.edu.sicnu.reproject.multi"
    compileSdk = 36

    defaultConfig {
        applicationId = "cn.edu.sicnu.reproject.multi"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // 默认（中文）+ 英文
        resourceConfigurations += listOf("zh", "en")
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
    // 界面使用 LinearLayout / ScrollView / ImageView 等基础控件
    // androidx.core 由 appcompat 传递引入，供 ContextCompat / ActivityCompat 使用
    implementation("androidx.appcompat:appcompat:1.7.0")

    // 统一 kotlin-stdlib 主包版本，避免传递依赖中版本不一
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24") {
            because("appcompat 传递依赖引入了不同版本的 kotlin-stdlib")
        }
    }
}

// Kotlin 1.8 起 jdk7/jdk8 扩展包的内容已并入 kotlin-stdlib 主包，
// 而 appcompat 传递依赖仍会带入 1.6.21 的旧版 jdk7/jdk8，
// 二者包含同名类，编译期报 Duplicate class 错误，这里统一排除。
configurations.configureEach {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
}
