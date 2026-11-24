/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 *
 * Dedicated WebSocket HTTP client for DeviceConnect (with the WebSockets plugin
 * installed). Mirrors the existing [com.moralist.shared.network.createHttpClient].
 */

package com.moralist.shared.deviceconnect

import io.ktor.client.HttpClient

expect fun createDeviceConnectHttpClient(): HttpClient
