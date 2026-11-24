/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 *
 * Local WebSocket server that accepts incoming DeviceConnect links from peers on
 * the LAN. The platform actual hosts it (Ktor CIO on Android).
 */

package com.moralist.shared.deviceconnect

expect class LocalDeviceServer() {
    /** Port 0 selects a free port; returns the actual listening port. */
    fun start(
        port: Int,
        onFailure: (Throwable) -> Unit,
        onChannel: suspend (DeviceChannel) -> Unit,
    ): Int

    fun stop()
}
