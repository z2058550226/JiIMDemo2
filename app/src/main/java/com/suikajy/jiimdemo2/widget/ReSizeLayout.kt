package com.suikajy.jiimdemo2.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout
import com.suikajy.jiimdemo2.common.Global

/**
 *
 * @author zjy
 * @date 2017/11/7
 */
class ReSizeLayout : RelativeLayout {

    private var mSizeChangeListener: OnSizeChangeListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e("****** TAG ******", "onSizeChanged : new: $w $h old: $oldw,$oldh   key board height is ${Global.softKeyBoardHeight}")
        if (mSizeChangeListener != null) {
            mSizeChangeListener!!.onSizeChanged(w, h, oldw, oldh)
        }
    }

    public fun setOnSizeChangeListener(listener: OnSizeChangeListener) {
        mSizeChangeListener = listener
    }

    interface OnSizeChangeListener {
        fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("****** TAG ******", "onMeasure,height : ${MeasureSpec.getSize(heightMeasureSpec)}")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        Log.e("****** TAG ******", "onLayout,bottom is $b changed is $changed")
    }
}