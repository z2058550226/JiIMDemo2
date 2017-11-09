package com.suikajy.jiimdemo2.module.private_chat

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.Message
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.common.App
import com.suikajy.jiimdemo2.common.Global
import com.suikajy.jiimdemo2.common.notNullSingleValue
import com.suikajy.jiimdemo2.jmessage.SuccessJCallBack
import com.suikajy.jiimdemo2.module.private_chat.inputFragments.VoiceFragment
import kotlinx.android.synthetic.main.activity_private_chat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.suikajy.jiimdemo2.utils.DensityUtil
import com.suikajy.jiimdemo2.utils.KeybordS

/**
 *
 * @author zjy
 * @date 2017/11/2
 */
class PrivateChatActivity : AppCompatActivity() {

    private val mAdapter = PrivateChatAdapter()
    private val mConversation by lazy {
        JMessageClient.getSingleConversation(getChatDestUserName()) ?: Conversation.createSingleConversation(getChatDestUserName())
    }
    private val mRecyclerLayoutManager by lazy { LinearLayoutManager(this) }
    private var targetUserName by notNullSingleValue<String>()

    companion object {
        private val TARGET_USER_NAME_KEY = "TARGET_USER_NAME_KEY"
        fun start(context: Context, descName: String) {
            val intent = Intent(context, PrivateChatActivity::class.java)
            intent.putExtra(TARGET_USER_NAME_KEY, descName)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_chat)
        targetUserName = intent.getStringExtra(TARGET_USER_NAME_KEY)
        Log.e("****** TAG ******", "target user name is $targetUserName")
        EventBus.getDefault().register(this)
        mRecyclerLayoutManager.stackFromEnd = true//设置默认展示RecyclerView底部
        mRecyclerView.layoutManager = mRecyclerLayoutManager
        mRecyclerView.adapter = mAdapter
        initView()
        initClick()
    }

    private fun initView() {
        refreshMessageList()
    }

    private fun initClick() {
        mBtnSubmit.setOnClickListener({
            val messageText = mEtChatInput.text.toString().trim()
            mEtChatInput.setText("")
            val textMessage = JMessageClient.createSingleTextMessage(getChatDestUserName(), Global.JAppKey, messageText)
            textMessage.setOnSendCompleteCallback(object : SuccessJCallBack() {
                override fun onSuccess() {
                    Log.e("****** TAG ******", "onSuccess")
                    insertMessage(textMessage)
                }
            })
            JMessageClient.sendMessage(textMessage)
        })
    }

    private fun getChatDestUserName(): String {
        return targetUserName
    }

    private fun insertMessage(newMessage: Message) {
        mAdapter.insertOneMessage(newMessage)
        mRecyclerView.scrollToPosition(mAdapter.itemCount - 1)
    }

    /**
     * 刷新ui
     */
    private fun refreshMessageList() {
        if (mConversation != null) {
            val allMessage = mConversation.allMessage
            mAdapter.refreshData(allMessage)
            mRecyclerView.scrollToPosition(mAdapter.itemCount - 1)
        } else {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEventMainThread(event: MessageEvent) {
        val message = event.message
        //toast("from ${message.fromUser}  to $targetUserName ${(message.content as TextContent).text} ")
        if (message.fromUser.userName.equals(targetUserName)) {
            when (message.contentType) {
                ContentType.text -> {
                    insertMessage(message)
                }
                ContentType.image -> {
                    insertMessage(message)
                }
                else -> insertMessage(message)
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}