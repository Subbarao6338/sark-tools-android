package omni.toolbox.utils

object BpmUtils {
    /**
     * Calculates Beats Per Minute (BPM) based on a sequence of tap timestamps in milliseconds.
     * Requires at least 2 timestamps and positive overall duration.
     */
    fun calculateBpm(timestamps: List<Long>): Int {
        if (timestamps.size < 2) return 0
        val duration = timestamps.last() - timestamps.first()
        if (duration <= 0L) return 0
        return ((timestamps.size - 1) * 60000.0 / duration).toInt()
    }
}
