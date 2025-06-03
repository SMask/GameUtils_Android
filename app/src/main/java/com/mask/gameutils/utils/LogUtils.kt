package com.mask.gameutils.utils

import android.util.Log

/**
 * LogUtils
 *
 * Create by lishilin on 2025/2/18
 */
object LogUtils {

    private const val TAG = "Mask"

    fun i(msg: String) {
        i(TAG, msg)
    }

    fun i(tag: String?, msg: String) {
        Log.i(tag, msg)
    }
}