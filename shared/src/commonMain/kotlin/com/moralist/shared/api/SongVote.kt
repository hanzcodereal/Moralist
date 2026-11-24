package com.moralist.shared.api

import kotlinx.serialization.Serializable

@Serializable
internal enum class SongVote { LIKE, DISLIKE, INDIFFERENT }
