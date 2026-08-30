package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class NotificationFeedbackHelper(private val context: Context) {

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (e: Exception) {
        null
    }

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_NOTIFICATION, 45) // Subtle 45% volume
    } catch (e: Exception) {
        null
    }

    /**
     * Subtle haptic feedback & chime for significant market price updates
     */
    fun triggerMarketUpdateAlert(isBullish: Boolean, isMuted: Boolean = false) {
        try {
            // Haptic trigger
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = if (isBullish) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                } else {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(35)
            }

            // Subtle chime sound
            if (!isMuted) {
                val tone = if (isBullish) ToneGenerator.TONE_PROP_BEEP else ToneGenerator.TONE_PROP_ACK
                toneGenerator?.startTone(tone, 70)
            }
        } catch (e: Exception) {
            Log.d("NotificationFeedback", "Market alert trigger error: ${e.message}")
        }
    }

    /**
     * Prominent haptic pattern & sound chime when new Fed announcement / speech chunk arrives
     */
    fun triggerFedAnnouncementAlert(isMuted: Boolean = false) {
        try {
            // Double-pulse haptic for Fed statements
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 45, 50, 45)
                val amplitudes = intArrayOf(0, 160, 0, 200)
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 50, 50, 50), -1)
            }

            // Chime sound
            if (!isMuted) {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 110)
            }
        } catch (e: Exception) {
            Log.d("NotificationFeedback", "Fed announcement trigger error: ${e.message}")
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            // ignore
        }
    }
}
