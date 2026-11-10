package com.wstxda.toolkit.services.accessibility

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.wstxda.toolkit.R

@SuppressLint("AccessibilityPolicy")
class TileAccessibilityService : AccessibilityService() {

    companion object {
        const val ACTION_KEY = "com.wstxda.toolkit.ACTION_KEY"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getIntExtra(ACTION_KEY, -1) ?: -1
        if (action != -1) {
            val actionSuccessful = performGlobalAction(action)
            if (!actionSuccessful) {
                Toast.makeText(
                    applicationContext, getString(R.string.not_supported), Toast.LENGTH_LONG
                ).show()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}