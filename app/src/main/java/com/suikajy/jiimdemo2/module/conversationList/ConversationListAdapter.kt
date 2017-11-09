package com.suikajy.jiimdemo2.module.conversationList

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.UserInfo
import com.bumptech.glide.Glide
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.base.BaseRecyclerAdapter
import com.suikajy.jiimdemo2.module.private_chat.PrivateChatActivity
import com.suikajy.jiimdemo2.sample.SampleUser
import com.suikajy.jiimdemo2.utils.PATTERN_MOUNTH_DAY_HMS
import com.suikajy.jiimdemo2.utils.stamp2String
import de.hdodenhof.circleimageview.CircleImageView
import kotterknife.bindView

/**
 *
 * @author zjy
 * @date 2017/11/3
 */
class ConversationListAdapter : BaseRecyclerAdapter<ConversationListAdapter.ViewHolder, Conversation>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_conversation_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val conversation = dataSet[position]
        val targetInfo = conversation.targetInfo
        val targetUserName = if (targetInfo is UserInfo) {
            targetInfo.userName
        } else throw IllegalArgumentException("会话目标不是单个用户，可能是群组会话")
        val latestMessage = conversation.latestMessage
        val createTime = latestMessage.createTime
        val content = latestMessage.content
        holder!!.mTvTime.text = createTime.stamp2String(PATTERN_MOUNTH_DAY_HMS)
        holder.mTvDestUserName.text = targetUserName
        if (targetUserName != SampleUser.REIMU.userName) {
            Glide.with(holder.mImgAvatar).load(R.mipmap.toy_pic).into(holder.mImgAvatar)
        } else {
            Glide.with(holder.mImgAvatar).load(R.mipmap.toy_avatar).into(holder.mImgAvatar)
        }
        when (content) {
            is TextContent -> {
                holder.mTvMessageText.text = content.text
            }
            is ImageContent -> {
                holder.mTvMessageText.text = "[图片]"
            }
            else -> holder.mTvMessageText.text = "[其他类型]"
        }
        holder.itemView.setOnClickListener({
            PrivateChatActivity.start(holder.itemView.context, targetUserName)
        })
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val mImgAvatar: CircleImageView by bindView(R.id.mImgAvatar)
        val mTvDestUserName: TextView by bindView(R.id.mTvDestUserName)
        val mTvMessageText: TextView by bindView(R.id.mTvMessageText)
        val mTvTime: TextView by bindView(R.id.mTvTime)
    }
}