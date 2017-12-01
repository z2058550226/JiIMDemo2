package com.suikajy.jiimdemo2.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.suikajy.jiimdemo2.utils.LogUtils

/**
 * 作为聊天室界面的最底层布局，仅仅是监听界面尺寸的改变
 *
 * @author zjy
 * @date 2017/11/7
 */
class ReSizeLayout : LinearLayout {

    private var mSizeChangeListener: OnSizeChangeListener? = null

    /**
     * 是否监听高度变化
     * 当点击加号时有两种情况：
     * 1.菜单开启时：
     *      1.软键盘关闭，但输入框位置不变，露出下方菜单。
     *      2.软键盘开启，但输入框位置不变，下方菜单被软键盘完全遮盖
     * 2.菜单关闭时：
     *      1.软键盘不动，输入框位置提到软键盘高度，露出下方菜单
     */
    private var isObserveHeight = true

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        LogUtils.e("onSizeChanged : new: $w $h old: $oldw,$oldh")
        if (mSizeChangeListener != null && isObserveHeight) {
            mSizeChangeListener!!.onSizeChanged(w, h, oldw, oldh)
        }
    }

    fun setOnSizeChangeListener(listener: OnSizeChangeListener) {
        mSizeChangeListener = listener
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        LogUtils.e("onMeasure,height : ${MeasureSpec.getSize(heightMeasureSpec)}")
//    }
//
//    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        super.onLayout(changed, l, t, r, b)
//        LogUtils.e("onLayout,bottom is $b changed is $changed")
//    }

    interface OnSizeChangeListener {
        fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    }
}
