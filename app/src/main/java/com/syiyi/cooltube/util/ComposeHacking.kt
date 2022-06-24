package com.syiyi.cooltube.util

/**
 * Use reflection to modify some internal fields of the compose components.
 *
 * Compose is still in the rapid development stage, many properties are not
 * exposed for customization, so use reflection to modify internal properties
 *
 * @author RE
 * @return Modify Result
 */
fun initComposeHacking() = runCatching {
    hackMinTabWidth()
}

// https://issuetracker.google.com/issues/226665301
private fun hackMinTabWidth() {
    val clazz = Class.forName("androidx.compose.material3.TabRowKt")
    val field = clazz.getDeclaredField("ScrollableTabRowMinimumTabWidth")
    field.isAccessible = true
    field.set(null, 0.0f) // set min width to zero (fit content width)
}