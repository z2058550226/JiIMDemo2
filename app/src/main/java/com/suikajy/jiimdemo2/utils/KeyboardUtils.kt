package com.suikajy.jiimdemo2.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * @author zjy
 * @date 2017/11/27
 */

/**
 * 获取根部局高度
 * @rootView 根布局
 */
public fun computeUsableHeight(rootView: View): Int {
    val r = Rect()
    rootView.getWindowVisibleDisplayFrame(r)
    // 全屏模式下：直接返回r.bottom，r.top其实是状态栏的高度
    return r.bottom - r.top
}

/**
 * 打开软键盘
 *
 * @param mEditText
 * @param mContext
 */
fun openKeyboard(mEditText: EditText, mContext: Context) {
    val imm = mContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY)
}

/**
 * 关闭软键盘
 *
 * @param mEditText 输入框
 * @param mContext 上下文
 */
fun closeKeyboard(mEditText: EditText, mContext: Context) {
    val imm = mContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
}

/**
 * 判断当前软键盘是否打开
 *
 * @param activity
 * @return
 */
fun isSoftInputShow(activity: Activity): Boolean {
    // 虚拟键盘隐藏 判断view是否为空
    val view = activity.window.peekDecorView()
    if (view != null) {
        // 隐藏虚拟键盘
        val inputmanger = activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //       inputmanger.hideSoftInputFromWindow(view.getWindowToken(),0);
        return inputmanger.isActive && activity.window.currentFocus != null
    }
    return false
}
