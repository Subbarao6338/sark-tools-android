package omni.toolbox.service

import androidx.core.net.toUri
import android.content.Intent
import android.service.quicksettings.TileService
import omni.browser.MainActivity
import omni.toolbox.utils.startActivityAndCollapseCompat

class StopwatchTileService : TileService() {
    override fun onClick() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = "naturetools://stopwatch".toUri()
        }
        startActivityAndCollapseCompat(intent)
    }
}
