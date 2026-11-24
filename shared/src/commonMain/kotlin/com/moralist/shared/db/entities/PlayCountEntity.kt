/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.db.entities

import androidx.room3.Entity

@Entity(
    tableName = "playCount",
    primaryKeys = ["song", "year", "month"],
)
data class PlayCountEntity(
    val song: String,
    val year: Int = -1,
    val month: Int = -1,
    val count: Int = -1,
)
