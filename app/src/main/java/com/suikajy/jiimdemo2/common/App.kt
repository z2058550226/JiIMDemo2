package com.suikajy.jiimdemo2.common

import android.app.Application
import cn.jpush.im.android.api.JMessageClient
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.suikajy.imgpicker.common.ImgPicker
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
        ImgPicker.init(this)
        JMessageClient.setDebugMode(true)
        JMessageClient.init(this)
        AutoLayoutConifg.getInstance().init(this)
        Global.statusBarHeight = getStatusBarHeight()
        Global.softKeyBoardHeight = 0
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    companion object {
        var instance: App by notNullSingleValue()
    }

}