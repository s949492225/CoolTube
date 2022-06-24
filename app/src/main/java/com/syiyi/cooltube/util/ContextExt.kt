package com.syiyi.cooltube.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast


fun Context.findActivity(): Activity = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> error("Failed to find activity: ${this.javaClass.name}")
}

inline fun Context.toast(
    text: String,
    length: Int = Toast.LENGTH_SHORT,
    builder: Toast.() -> Unit = {}
) {
    Toast.makeText(this, text, length)
        .apply(builder)
        .show()
}
