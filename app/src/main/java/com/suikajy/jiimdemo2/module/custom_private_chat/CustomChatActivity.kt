package com.suikajy.jiimdemo2.module.custom_private_chat

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.common.App
import com.suikajy.jiimdemo2.common.preference
import com.suikajy.jiimdemo2.utils.DensityUtil
import com.suikajy.jiimdemo2.utils.closeKeyboard
import com.suikajy.jiimdemo2.utils.isSoftInputShow
import com.suikajy.jiimdemo2.utils.openKeyboard
import com.suikajy.jiimdemo2.widget.ReSizeLayout
import kotlinx.android.synthetic.main.activity_custom_private_chat.*

/**
 *
 * @author zjy
 * @date 2017/11/24
 */
class CustomChatActivity : AppCompatActivity(), ReSizeLayout.OnSizeChangeListener {
    /**
     * 软键盘和菜单在展示时是互斥的，可以同时不展示
     */
    private var isMenuShow = false

    /**
     * 软键盘高度，通过SharedPreference缓存，如果没有就使用默认的500dp作为高度
     */
    private var keyboardHeight by preference(App.instance, "KEYBOARD_HEIGHT", DensityUtil.dip2px(App.instance, 200f))
    private var mScreenHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setContentView(R.layout.activity_custom_private_chat)
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mScreenHeight = wm.defaultDisplay.height
        rsl.setOnSizeChangeListener(this)
        initClick()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (h.toFloat() / mScreenHeight < 0.8f) {
            keyboardHeight = oldh - h
        }
        //设置EditText跟随
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        if (isSoftInputShow(this) && !isMenuShow) {
            fl_menu.layoutParams.height = 0
            fl_menu.requestLayout()
        }
    }

    private fun initClick() {
        btn_plus.setOnClickListener({
            if (isMenuShow) {
                //菜单打开状态下，继续点击弹出输入框
                isMenuShow = false
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                openKeyboard(etInput, this@CustomChatActivity)
            } else {
                isMenuShow = true
                //解除EditText跟随
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                if (isSoftInputShow(this)) {
                    //菜单未展示，输入框打开状态下，打开菜单
                    closeKeyboard(etInput, this@CustomChatActivity)
                    fl_menu.layoutParams.height = keyboardHeight
                    fl_menu.requestLayout()
                } else {
                    //菜单未展示，输入框关闭状态下，打开菜单
                    fl_menu.layoutParams.height = keyboardHeight
                    fl_menu.requestLayout()
                }
            }
        })
    }
}