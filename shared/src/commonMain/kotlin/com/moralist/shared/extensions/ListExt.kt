/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.extensions

fun <T> List<T>.reversed(reversed: Boolean) = if (reversed) asReversed() else this
