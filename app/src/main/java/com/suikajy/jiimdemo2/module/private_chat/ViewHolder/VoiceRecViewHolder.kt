package com.suikajy.jiimdemo2.module.private_chat.ViewHolder

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.suikajy.jiimdemo2.R
import kotterknife.bindView

/**
 *
 * @author zjy
 * @date 2017/11/30
 */
class VoiceRecViewHolder(item: View) : BaseViewHolder(item) {
    val mTvLengthHolder: TextView by bindView(R.id.tv_length_holder)
    val mIvVoice: ImageView by bindView(R.id.iv_voice)
    val mTvVoiceLength: TextView by bindView(R.id.tv_voice_length)
    val mTempMsgFl: FrameLayout by bindView(R.id.temp_msg_fl)
    val mIvReadStatus: ImageView by bindView(R.id.iv_read_status)
}