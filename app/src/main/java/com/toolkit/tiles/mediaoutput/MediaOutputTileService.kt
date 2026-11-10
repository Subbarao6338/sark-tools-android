package com.wstxda.toolkit.tiles.mediaoutput

import android.service.quicksettings.Tile
import com.wstxda.toolkit.activity.MediaOutputActivity
import com.wstxda.toolkit.base.BaseTileService
import com.wstxda.toolkit.ui.icon.MediaOutputIconProvider
import com.wstxda.toolkit.ui.label.MediaOutputLabelProvider

class MediaOutputTileService : BaseTileService() {

    private val labelProvider by lazy { MediaOutputLabelProvider(applicationContext) }
    private val iconProvider by lazy { MediaOutputIconProvider(applicationContext) }

    override fun onClick() {
        startActivityAndCollapse(MediaOutputActivity::class.java)
    }

    override fun updateTile() {
        setTileState(
            state = Tile.STATE_INACTIVE,
            label = labelProvider.getLabel(),
            subtitle = labelProvider.getSubtitle(),
            icon = iconProvider.getIcon(),
        )
    }
}