plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.droiddesign"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.droiddesign"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }
    buildFeatures {
        viewBinding = true
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.credentials:credentials:1.3.0-alpha01")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.material:material:1.11.0")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-firestore:24.11.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")

    implementation("com.google.android.gms:play-services-maps:18.2.0")

    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("androidx.activity:activity:1.8.2")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")


    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation ("androidx.multidex:multidex:2.0.1")
    implementation ("com.google.android.gms:play-services-base:18.3.0")


    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.11.2")

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.1") // or a later version
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.1") // or a later version
    testImplementation ("org.mockito:mockito-core:3.11.2") // or a later version
    testImplementation ("org.mockito:mockito-junit-jupiter:3.6.0") // or a later version

    testImplementation("com.google.firebase:firebase-firestore:24.11.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation ("com.github.Drjacky:ImagePicker:2.3.22")
    implementation ("org.greenrobot:eventbus:3.1.1")

    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.squareup.okhttp3:okhttp:")
    implementation ("com.google.firebase:firebase-messaging-directboot:23.4.1")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))



}