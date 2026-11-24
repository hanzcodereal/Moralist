/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared

/**
 * Alamat server Moralist (folder `server/`). Ganti HOST dengan domain tempat server kamu di-deploy.
 * Nilai yang sama harus dipasang di `moralistServerUrl` pada gradle.properties.
 */
object MoralistServer {
    const val HOST = "moralist.up.railway.app"
    const val BASE_URL = "https://$HOST"
    const val LISTEN_TOGETHER_URL = "wss://$HOST/ws"
}
