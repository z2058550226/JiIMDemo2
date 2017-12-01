package com.suikajy.jiimdemo2.module.private_chat.ViewHolder

import android.view.View
import android.widget.TextView
import com.suikajy.jiimdemo2.R
import kotterknife.bindView

/**
 *
 * @author zjy
 * @date 2017/11/29
 */
class TxtSendViewHolder(item: View) : BaseViewHolder(item) {
    val mTvMsgText: TextView by bindView(R.id.tv_msg_text)
}