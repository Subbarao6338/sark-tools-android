package omni.toolbox.service

import androidx.core.net.toUri
import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService
import omni.toolbox.utils.startActivityAndCollapseCompat

class CalculatorTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "naturetools://calculator".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        unlockAndRun {
            startActivityAndCollapseCompat(intent)
        }
    }
}
