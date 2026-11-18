plugins { id("com.android.application") }
val buildNumber = providers.gradleProperty("veyraBuildNumber").orNull?.toIntOrNull() ?: 1
android {
 namespace = "com.veyra.app"
 compileSdk = 35
 defaultConfig {
  applicationId = "com.veyra.app"
  minSdk = 26
  targetSdk = 35
  versionCode = buildNumber
  versionName = "0.1.$buildNumber"
 }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
