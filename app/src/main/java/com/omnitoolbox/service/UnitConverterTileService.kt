package omni.toolbox.service

import androidx.core.net.toUri
import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService
import omni.toolbox.utils.startActivityAndCollapseCompat

class UnitConverterTileService : TileService() {
    override fun onClick() {
        val intent = Intent(Intent.ACTION_VIEW, "naturetools://hub".toUri()).apply {
            // naturetools://hub is currently used for Hub, but we can navigate to converter in naturetools://host
            // In a real app, we'd have naturetools://converter.
            // Let's use a generic launcher if naturetools doesn't have a direct host for converter yet.
            data = "naturetools://hub".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivityAndCollapseCompat(intent)
    }
}
