package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R
import com.wstxda.toolkit.manager.coinflip.CoinFlipSide

class CoinFlipLabelProvider(private val context: Context) {

    fun getLabel(lastFlip: CoinFlipSide?): CharSequence {
        return when (lastFlip) {
            CoinFlipSide.HEADS -> context.getString(R.string.coin_tile_heads)
            CoinFlipSide.TAILS -> context.getString(R.string.coin_tile_tails)
            null -> context.getString(R.string.coin_flip_tile)
        }
    }

    fun getSubtitle(lastFlip: CoinFlipSide?, heads: Int, tails: Int): CharSequence {
        return if (lastFlip == null) {
            context.getString(R.string.coin_flip_tile_tap)
        } else {
            context.getString(R.string.coin_flip_tile_count, heads, tails)
        }
    }
}