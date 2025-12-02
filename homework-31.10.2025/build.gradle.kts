// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

buildscript {
    val VERSION_MAJOR: Int = 1
    val VERSION_MINOR: Int = 0
    val PATCH: Int = 0

    val VERSION_CODE: Int = VERSION_MAJOR * 100_000 + VERSION_MINOR * 1000 + PATCH * 10
    val VERSION_NAME = "$VERSION_MAJOR.$VERSION_MINOR.$PATCH" // 1.0.0
}