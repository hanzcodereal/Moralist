package com.moralist.shared.utils

import android.icu.text.DateTimePatternGenerator
import java.util.Locale

internal fun historyMonthPattern(locale: Locale): String = DateTimePatternGenerator.getInstance(locale).getBestPattern("yMMMM")
