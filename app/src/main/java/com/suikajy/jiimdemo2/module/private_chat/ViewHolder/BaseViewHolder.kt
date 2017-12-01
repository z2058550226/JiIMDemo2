package com.suikajy.jiimdemo2.module.private_chat.ViewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.suikajy.jiimdemo2.R
import de.hdodenhof.circleimageview.CircleImageView
import kotterknife.bindView

/**
 *
 * @author zjy
 * @date 2017/11/29
 */
abstract class BaseViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    //公有信息：人物头像，对话时间
    val mTvChatMsgTime: TextView by bindView(R.id.tv_chat_msg_time)
    val mIvAvatar: CircleImageView by bindView(R.id.iv_avatar)
}