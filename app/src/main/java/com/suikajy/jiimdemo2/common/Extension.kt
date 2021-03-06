package com.suikajy.jiimdemo2.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * 扩展函数集合类
 * @author zjy
 * @date 2017/11/3
 */

fun Activity.startActivity(clazz: Class<*>) {
    this.startActivity(Intent(this, clazz))
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun toast(msg: String) {
    Toast.makeText(App.instance, msg, Toast.LENGTH_SHORT).show()
}

fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = this.resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun ViewGroup.inflate(layoutId: Int): View {
    return LayoutInflater.from(this.context).inflate(layoutId, this, false)
}