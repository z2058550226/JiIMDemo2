package com.suikajy.jiimdemo2.jmessage

import android.widget.Toast
import com.suikajy.jiimdemo2.common.App

/**
 *
 * @author zjy
 * @date 2017/11/3
 */
abstract class SimpleJCallBack : JCallBack() {
    override fun gotResult(responseCode: Int, responseDesc: String?) {
        if (responseCode == 0) {
            //success
            Toast.makeText(App.instance, "code is $responseCode desc is $responseDesc", Toast.LENGTH_SHORT).show()
        } else {
            //fail
            Toast.makeText(App.instance, "code is $responseCode desc is $responseDesc", Toast.LENGTH_SHORT).show()
        }
    }
}