package omni.toolbox.service

import androidx.core.net.toUri
import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService
import omni.toolbox.utils.startActivityAndCollapseCompat

class QrScannerTileService : TileService() {
    override fun onClick() {
        val intent = Intent(Intent.ACTION_VIEW, "naturetools://qr_scanner".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivityAndCollapseCompat(intent)
    }
}
