package com.suikajy.imgpicker.common

import android.content.Context
import com.suikajy.imgpicker.utils.ScreenUtil
import com.suikajy.imgpicker.utils.StorageUtil

/**
 * 对ImgPicker这个library的常用接口封装
 * @author zjy
 * @date 2017/11/30
 */
object ImgPicker {

    /**
     * 在Application的onCreate方法中需要调用此方法，来初始化ImgPicker的静态变量
     * @context : Application的实例
     */
    fun init(context: Context) {
        ScreenUtil.init(context)
        StorageUtil.init(context, null)
    }
}