package com.wstxda.toolkit.services.sensors

import com.wstxda.toolkit.manager.level.LevelMode

data class Orientation(
    val pitch: Float, val roll: Float, val balance: Float, val mode: LevelMode
)