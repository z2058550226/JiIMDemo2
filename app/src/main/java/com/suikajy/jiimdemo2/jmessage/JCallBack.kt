package com.suikajy.jiimdemo2.jmessage

import cn.jpush.im.api.BasicCallback

/**
 *
 * @author zjy
 * @date 2017/11/3
 */
abstract class JCallBack : BasicCallback() {
    abstract override fun gotResult(responseCode: Int, responseDesc: String?)
}