package com.suikajy.jiimdemo2.common

import cn.jiguang.api.JCoreInterface

/**
 * Created by zjy on 2017/11/2.
 */
object Global {
    val JAppKey = JCoreInterface.getAppKey()
    var statusBarHeight by notNullSingleValue<Int>()
    var softKeyBoardHeight by preference(App.instance, PREF_SOFT_KEY_BOARD_HEIGHT_KEY, 0)
}