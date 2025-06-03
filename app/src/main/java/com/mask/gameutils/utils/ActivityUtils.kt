package com.mask.gameutils.utils

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * ActivityUtils
 *
 * Create by lishilin on 2025-05-07
 */
object ActivityUtils {

    fun startActivity(context: Context, cls: Class<*>) {
        val intent = Intent(context, cls)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}