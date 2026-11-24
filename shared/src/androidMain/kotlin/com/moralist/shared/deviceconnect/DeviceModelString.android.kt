/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.deviceconnect

import android.os.Build

actual fun getDeviceModelString(): String = "${Build.MANUFACTURER} ${Build.MODEL}"

internal actual fun getDefaultDeviceName(): String = getDeviceModelString()
