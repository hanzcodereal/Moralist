/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.db.entities

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "search_history",
    indices = [
        Index(
            value = ["query"],
            unique = true,
        ),
    ],
)
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
)
