package com.suikajy.jiimdemo2.module.private_chat.ViewHolder

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.suikajy.jiimdemo2.R
import kotterknife.bindView

/**
 *
 * @author zjy
 * @date 2017/11/29
 */
class ImgSendViewHolder(item: View) : BaseViewHolder(item) {
    val mIvMsgImage: ImageView by bindView(R.id.iv_msg_image)
    val mTvProgress: TextView by bindView(R.id.tv_progress)
    val mTempMsgFl: FrameLayout by bindView(R.id.temp_msg_fl)
    val mTvReceipt: TextView by bindView(R.id.tv_receipt)
    val mIvSending: ImageView by bindView(R.id.iv_sending)
    val mIbFailResend: ImageButton by bindView(R.id.ib_fail_resend)
}