package com.wstxda.toolkit.ui.icon

import android.content.Context
import android.graphics.drawable.Icon
import com.wstxda.toolkit.R
import com.wstxda.toolkit.manager.coinflip.CoinFlipSide

class CoinFlipIconProvider(private val context: Context) {

    fun getIcon(lastFlip: CoinFlipSide?): Icon {
        val iconRes = when (lastFlip) {
            CoinFlipSide.HEADS -> R.drawable.ic_coin_heads
            CoinFlipSide.TAILS -> R.drawable.ic_coin_tails
            null -> R.drawable.ic_coin
        }
        return Icon.createWithResource(context, iconRes)
    }
}