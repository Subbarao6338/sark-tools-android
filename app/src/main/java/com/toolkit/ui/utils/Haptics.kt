@file:Suppress("unused")

package com.wstxda.toolkit.ui.utils

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.wstxda.toolkit.data.HapticLevel

class Haptics(context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(VibratorManager::class.java)
        vibratorManager?.defaultVibrator ?: context.getSystemService(Vibrator::class.java)!!
    } else {
        context.getSystemService(Vibrator::class.java)!!
    }

    fun low() = perform(HapticLevel.LOW)
    fun medium() = perform(HapticLevel.MEDIUM)
    fun high() = perform(HapticLevel.HIGH)
    fun veryHigh() = perform(HapticLevel.VERY_HIGH)

    fun vibrate(duration: Long, level: HapticLevel = HapticLevel.MEDIUM) {
        if (!hasVibrator() || duration <= 0) return

        val amplitude = when (level) {
            HapticLevel.LOW -> 50
            HapticLevel.MEDIUM -> 120
            HapticLevel.HIGH -> 200
            HapticLevel.VERY_HIGH -> 255
        }
        vibrateCompat(VibrationEffect.createOneShot(duration, amplitude))
    }

    fun cancel() {
        if (hasVibrator()) vibrator.cancel()
    }

    private fun hasVibrator(): Boolean = vibrator.hasVibrator()

    private fun perform(level: HapticLevel) {
        if (!hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effectId = when (level) {
                HapticLevel.LOW -> VibrationEffect.EFFECT_TICK
                HapticLevel.MEDIUM -> VibrationEffect.EFFECT_CLICK
                HapticLevel.HIGH -> VibrationEffect.EFFECT_HEAVY_CLICK
                HapticLevel.VERY_HIGH -> VibrationEffect.EFFECT_DOUBLE_CLICK
            }
            vibrateCompat(VibrationEffect.createPredefined(effectId))
        } else {
            val duration = when (level) {
                HapticLevel.LOW -> 15L
                HapticLevel.MEDIUM -> 30L
                HapticLevel.HIGH -> 50L
                HapticLevel.VERY_HIGH -> 75L
            }
            val amplitude = when (level) {
                HapticLevel.LOW -> 50
                HapticLevel.MEDIUM -> 120
                HapticLevel.HIGH -> 200
                HapticLevel.VERY_HIGH -> 255
            }
            vibrateCompat(VibrationEffect.createOneShot(duration, amplitude))
        }
    }

    private fun vibrateCompat(effect: VibrationEffect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val attributes =
                VibrationAttributes.Builder().setUsage(VibrationAttributes.USAGE_HARDWARE_FEEDBACK)
                    .build()
            vibrator.vibrate(effect, attributes)
        } else {
            val attributes =
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build()

            @Suppress("DEPRECATION") vibrator.vibrate(effect, attributes)
        }
    }
}