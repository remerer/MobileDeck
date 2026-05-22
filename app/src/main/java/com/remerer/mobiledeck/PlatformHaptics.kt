package com.remerer.mobiledeck

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

fun Context.vibrateButtonPress(level: ButtonVibrationLevel) {
    if (level == ButtonVibrationLevel.Off) return
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Vibrator::class.java)
    } ?: return
    if (!vibrator.hasVibrator()) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(level.durationMillis, level.amplitude))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(level.durationMillis)
    }
}


