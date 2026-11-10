package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R

class DiceRollLabelProvider(private val context: Context) {

    fun getLabel(roll: Int?): CharSequence {
        return when (roll) {
            1 -> context.getString(R.string.dice_tile_1)
            2 -> context.getString(R.string.dice_tile_2)
            3 -> context.getString(R.string.dice_tile_3)
            4 -> context.getString(R.string.dice_tile_4)
            5 -> context.getString(R.string.dice_tile_5)
            6 -> context.getString(R.string.dice_tile_6)
            else -> context.getString(R.string.dice_roll_tile)
        }
    }

    fun getSubtitle(isRolling: Boolean): CharSequence {
        return if (isRolling) {
            context.getString(R.string.dice_roll_tile_rolling)
        } else {
            context.getString(R.string.dice_roll_tile_tap)
        }
    }
}