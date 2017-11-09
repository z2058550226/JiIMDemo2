package com.suikajy.jiimdemo2.common

import android.app.Application
import cn.jpush.im.android.api.JMessageClient
import com.zhy.autolayout.config.AutoLayoutConifg

/**
 *
 * @author zjy
 * @date 2017/11/1
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        JMessageClient.setDebugMode(true)
        JMessageClient.init(this)
        AutoLayoutConifg.getInstance().init(this)
        Global.statusBarHeight = getStatusBarHeight()
        Global.softKeyBoardHeight = 0
    }

    companion object {
        var instance: App by notNullSingleValue()
    }

}