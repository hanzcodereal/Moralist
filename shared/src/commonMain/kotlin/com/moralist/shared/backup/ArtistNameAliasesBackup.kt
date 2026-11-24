package com.moralist.shared.backup

import com.moralist.shared.constants.ArtistNameAliasesJsonKey
import com.moralist.shared.settings.ArtistNameAliases
import com.moralist.shared.settings.PortableSetting
import com.moralist.shared.settings.PortableSettings
import com.moralist.shared.settings.SettingType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun PortableSettings?.withLegacyArtistNameAliases(serialized: String): PortableSettings {
    val aliases = ArtistNameAliases.deserialize(serialized)
    val settings = this ?: PortableSettings(entries = emptyList())
    return settings.copy(
        entries =
            settings.entries.filterNot { it.key == ArtistNameAliasesJsonKey } +
                PortableSetting(ArtistNameAliasesJsonKey, SettingType.STRING, Json.encodeToString(aliases)),
    )
}
