package com.suikajy.jiimdemo2.jmessage

import android.util.Log

/**
 *
 * @author zjy
 * @date 2017/11/3
 */
abstract class SuccessJCallBack : JCallBack() {
    override fun gotResult(responseCode: Int, responseDesc: String?) {
        if (responseCode == 0) {
            //success
            Log.e("* SuccessJCallBack *", "success code is $responseCode desc is $responseDesc")
            onSuccess()
        } else {
            //fail
            Log.e("* SuccessJCallBack *", "failed code is $responseCode desc is $responseDesc")
        }
    }

    abstract fun onSuccess()
}