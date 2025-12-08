// C:/Users/LEGION/AndroidStudioProjects/App/build.gradle.kts (New)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // Keep your google-services plugin if needed, consider moving its version to the TOML file as well
    id("com.google.gms.google-services") version "4.4.4" apply false
}
