package org.hackillinois.android.model.leaderboard

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@TypeConverters(Converters::class)
data class Leaderboard(
    @PrimaryKey var profiles: List<LeaderboardProfile>
)
