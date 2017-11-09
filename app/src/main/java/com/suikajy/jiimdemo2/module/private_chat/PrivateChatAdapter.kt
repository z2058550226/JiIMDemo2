package com.suikajy.jiimdemo2.module.private_chat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.model.Message
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.base.BaseRecyclerAdapter
import com.suikajy.jiimdemo2.utils.PATTERN_MOUNTH_DAY_HMS
import com.suikajy.jiimdemo2.utils.stamp2String

/**
 * Created by zjy on 2017/11/2.
 */
class PrivateChatAdapter : BaseRecyclerAdapter<PrivateChatAdapter.ViewHolder, Message>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_private_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val message = dataSet[position]
        val createTime = message.createTime.stamp2String(PATTERN_MOUNTH_DAY_HMS)
        holder!!.mTvTime.text = createTime
        when (message.contentType) {
            ContentType.text -> {
                val textContent = message.content as TextContent
                holder.mTvMessage.text = textContent.text
            }
            else -> {
                holder.mTvMessage.text = "[非文本类型]"
            }
        }
    }

    fun insertOneMessage(message: Message) {
        val size = dataSet.size
        dataSet.add(message)
        notifyItemInserted(size)
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val mTvMessage = item.findViewById<TextView>(R.id.mTvMessage)
        val mTvTime = item.findViewById<TextView>(R.id.mTvTime)
    }
}