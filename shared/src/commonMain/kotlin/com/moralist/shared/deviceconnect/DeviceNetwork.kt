/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 *
 * Resolve the device's own LAN IPv4 address (used to advertise the local server).
 */

package com.moralist.shared.deviceconnect

internal expect fun getLocalIpV4Addresses(): List<String>

fun getLocalIpV4Address(): String? = getLocalIpV4Addresses().firstOrNull()
