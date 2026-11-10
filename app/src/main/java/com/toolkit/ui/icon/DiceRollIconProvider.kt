package com.wstxda.toolkit.ui.icon

import android.content.Context
import android.graphics.drawable.Icon
import com.wstxda.toolkit.R

class DiceRollIconProvider(private val context: Context) {

    fun getIcon(roll: Int?): Icon {
        val iconRes = when (roll) {
            1 -> R.drawable.ic_dice_1
            2 -> R.drawable.ic_dice_2
            3 -> R.drawable.ic_dice_3
            4 -> R.drawable.ic_dice_4
            5 -> R.drawable.ic_dice_5
            6 -> R.drawable.ic_dice_6
            else -> R.drawable.ic_dice
        }
        return Icon.createWithResource(context, iconRes)
    }
}