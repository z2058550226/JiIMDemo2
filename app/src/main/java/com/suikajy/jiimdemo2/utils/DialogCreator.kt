package com.suikajy.jiimdemo2.utils

import android.app.AlertDialog
import android.content.Context

/**
 *
 * @author zjy
 * @date 2017/11/29
 */
fun createResendDialog(context: Context, listener: OnEnsureListener): AlertDialog =
        AlertDialog.Builder(context)
                .setMessage("是否重新发送？")
                .setNegativeButton("取消") { dialog, _ -> dialog!!.dismiss() }
                .setPositiveButton("确定") { dialog, _ ->
                    dialog!!.dismiss()
                    listener.onEnsure()
                }
                .create()


interface OnEnsureListener {
    fun onEnsure()
}