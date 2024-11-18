import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
}

fun getLocalProperty(propertyName: String): String {
    val localPropertiesFile = project.file("../local.properties")
    val localProperties = Properties()
    localProperties.load(FileInputStream(localPropertiesFile))
    return localProperties.getProperty(propertyName)
}

android {
    namespace = "com.jnu_alarm.scimath_keypad"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jnu_alarm.scimath_keypad"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "X_APP_ID", getLocalProperty("X_APP_ID"))
        buildConfigField("String", "BASE_URL", getLocalProperty("BASE_URL"))
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}