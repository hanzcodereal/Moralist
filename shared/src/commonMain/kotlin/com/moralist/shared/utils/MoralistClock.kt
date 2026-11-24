package com.moralist.shared.utils

import kotlin.time.Clock

object MoralistClock {
    fun nowMs(): Long = Clock.System.now().toEpochMilliseconds()
}
