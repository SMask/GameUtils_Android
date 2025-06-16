package com.mask.gameutils.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.mask.gameutils.App

/**
 * ToastUtils
 *
 * Create by lishilin on 2025-04-30
 */
object ToastUtils {

    fun show(msg: String) {
        show(App.context, msg)
    }

    fun show(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun show(@StringRes msgResId: Int) {
        show(App.context, msgResId)
    }

    fun show(context: Context, @StringRes msgResId: Int) {
        Toast.makeText(context, msgResId, Toast.LENGTH_SHORT).show()
    }

}