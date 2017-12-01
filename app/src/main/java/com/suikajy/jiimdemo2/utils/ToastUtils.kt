package com.suikajy.jiimdemo2.utils

import android.widget.Toast
import com.suikajy.jiimdemo2.common.App

/**
 *
 * @author zjy
 * @date 2017/11/29
 */

fun showToast(msg: String) {
    Toast.makeText(App.instance, msg, Toast.LENGTH_SHORT).show()
}