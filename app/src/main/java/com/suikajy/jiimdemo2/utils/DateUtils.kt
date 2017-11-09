package com.suikajy.jiimdemo2.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author zjy
 * @date 2017/11/3
 */
val format by lazy { SimpleDateFormat() }
val PATTERN_MOUNTH_DAY_HMS = "MM-dd HH:mm:ss"

fun Long.stamp2String(pattern: String): String {
    format.applyPattern(pattern)
    return format.format(Date(this))
}