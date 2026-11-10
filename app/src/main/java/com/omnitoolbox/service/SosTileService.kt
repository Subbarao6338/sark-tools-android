package omni.toolbox.service

import androidx.core.net.toUri
import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService
import omni.toolbox.utils.startActivityAndCollapseCompat

class SosTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "naturetools://sos".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivityAndCollapseCompat(intent)
    }
}
