package com.moralist.shared.utils

import com.moralist.shared.BuildConfig

fun isCrashReportingAvailable(appVersion: String = BuildConfig.APP_VERSION): Boolean = '+' !in appVersion
