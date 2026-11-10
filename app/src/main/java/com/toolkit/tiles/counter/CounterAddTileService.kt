package com.wstxda.toolkit.tiles.counter

import android.service.quicksettings.Tile
import com.wstxda.toolkit.base.BaseTileService
import com.wstxda.toolkit.manager.counter.CounterAction
import com.wstxda.toolkit.manager.counter.CounterModule
import com.wstxda.toolkit.ui.icon.CounterIconProvider
import com.wstxda.toolkit.ui.label.CounterLabelProvider
import kotlinx.coroutines.flow.Flow

class CounterAddTileService : BaseTileService() {

    private val counterManager by lazy { CounterModule.getInstance(applicationContext) }
    private val labelProvider by lazy { CounterLabelProvider(applicationContext) }
    private val iconProvider by lazy { CounterIconProvider(applicationContext) }

    override fun onClick() {
        counterManager.increment()
        updateTile()
    }

    override fun flowsToCollect(): List<Flow<*>> = listOf(
        counterManager.count,
        counterManager.lastAction,
    )

    override fun updateTile() {
        val count = counterManager.count.value
        val isActive = counterManager.lastAction.value == CounterAction.ADD

        setTileState(
            state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE,
            label = labelProvider.getAddLabel(isActive, count),
            subtitle = labelProvider.getAddSubtitle(isActive),
            icon = iconProvider.getAddIcon(),
        )
    }
}