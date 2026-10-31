package com.example.data.model

import com.example.R

enum class AmbientType {
    NONE,
    RAIN,
    BIRDS,
    FIRE,
    OCEAN,
    GALAXY
}

data class AmbientSound(
    val id: String,
    val name: String,
    val type: AmbientType,
    val rawResId: Int?,
    val description: String,
    val iconName: String
)

object AmbientCatalog {
    val NONE = AmbientSound(
        id = "none",
        name = "No Ambient Sound",
        type = AmbientType.NONE,
        rawResId = null,
        description = "Pure Quran recitation only",
        iconName = "VolumeMute"
    )

    val RAIN = AmbientSound(
        id = "rain",
        name = "Gentle Rain",
        type = AmbientType.RAIN,
        rawResId = R.raw.ambient_rain,
        description = "Calm rainfall on veranda with distant mist",
        iconName = "WaterDrop"
    )

    val BIRDS = AmbientSound(
        id = "birds",
        name = "Morning Birds",
        type = AmbientType.BIRDS,
        rawResId = R.raw.ambient_birds,
        description = "Peaceful morning birdsong in nature",
        iconName = "Forest"
    )

    val FIRE = AmbientSound(
        id = "fire",
        name = "Warm Hearth",
        type = AmbientType.FIRE,
        rawResId = R.raw.ambient_fire,
        description = "Cozy glowing firewood crackles and embers",
        iconName = "LocalFireDepartment"
    )

    val OCEAN = AmbientSound(
        id = "ocean",
        name = "Ocean Waves",
        type = AmbientType.OCEAN,
        rawResId = R.raw.ambient_ocean,
        description = "Soothing rhythmic coastal sea waves",
        iconName = "Waves"
    )

    val allSounds: List<AmbientSound> = listOf(
        NONE,
        RAIN,
        BIRDS,
        FIRE,
        OCEAN
    )

    fun getById(id: String): AmbientSound = allSounds.firstOrNull { it.id == id } ?: NONE
}
