package com.suikajy.jiimdemo2

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.content.*
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.event.MessageEvent
import com.suikajy.jiimdemo2.common.UserInfo
import com.suikajy.jiimdemo2.common.startActivity
import com.suikajy.jiimdemo2.common.toast
import com.suikajy.jiimdemo2.module.conversationList.ConversationListActivity
import com.suikajy.jiimdemo2.module.createSpecConversation.CreateSpecConversationActivity
import com.suikajy.jiimdemo2.module.custom_private_chat.CustomChatActivity
import com.suikajy.jiimdemo2.module.login.LoginActivity
import com.suikajy.jiimdemo2.module.private_chat.PrivateChatActivity
import com.suikajy.jiimdemo2.module.register.RegisterActivity
import com.suikajy.jiimdemo2.module.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        JMessageClient.registerEventReceiver(this)
        initData()
        initView()
    }

    private fun initData() {
        val myInfo = JMessageClient.getMyInfo()
        if (myInfo != null) {
            Toast.makeText(this, "用户已登录${myInfo.userName}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "用户未登录！！", Toast.LENGTH_SHORT).show()
            UserInfo.clearUserData()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mBtnSignIn.setOnClickListener({ startActivity(SignInActivity::class.java) })
        mBtnChat.setOnClickListener({ PrivateChatActivity.start(this@MainActivity, "reimu") })
        mBtnSignUp.setOnClickListener({ startActivity(RegisterActivity::class.java) })
        mBtnLogin.setOnClickListener({ startActivity(LoginActivity::class.java) })
        mBtnConversationList.setOnClickListener({ startActivity(ConversationListActivity::class.java) })
        mBtnCreateChat.setOnClickListener({ startActivity(CreateSpecConversationActivity::class.java) })
        mBtnViewTest.setOnClickListener({ startActivity(TestActivity::class.java) })
        mBtnCustomChat.setOnClickListener({ startActivity(CustomChatActivity::class.java) })
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        mTvCurrentUserName.text = getString(R.string.current_user) + UserInfo.userName
        toast(UserInfo.userName)
    }

    /**
     * 极光消息接收监听
     */
    public fun onEvent(event: MessageEvent) {
        EventBus.getDefault().post(event)
        val msg = event.message

        when (msg.contentType) {
            ContentType.text -> {
                //处理文字消息
                val textContent = msg.content as TextContent
                textContent.text
            }
            ContentType.image -> {
                //处理图片消息
                val imageContent = msg.content as ImageContent
                imageContent.localPath//图片本地地址
                imageContent.localThumbnailPath//图片对应缩略图的本地地址
            }
            ContentType.voice -> {
                //处理语音消息
                val voiceContent = msg.content as VoiceContent
                voiceContent.localPath//语音文件本地地址
                voiceContent.duration//语音文件时长
            }
            ContentType.custom -> {
                //处理自定义消息
                val customContent = msg.content as CustomContent
                customContent.getNumberValue("custom_num") //获取自定义的值
                customContent.getBooleanValue("custom_boolean")
                customContent.getStringValue("custom_string")
            }
            ContentType.eventNotification -> {
                //处理事件提醒消息
                val eventNotificationContent = msg.content as EventNotificationContent
                when (eventNotificationContent.eventNotificationType) {
                    EventNotificationContent.EventNotificationType.group_member_added -> {
                    }
                    EventNotificationContent.EventNotificationType.group_member_removed -> {
                    }
                    EventNotificationContent.EventNotificationType.group_member_exit -> {
                    }
                    EventNotificationContent.EventNotificationType.group_info_updated//since 2.2.1
                    -> {
                    }
                }//群成员加群事件
                //群成员被踢事件
                //群成员退群事件
                //群信息变更事件
            }
            else -> {
            }
        }
    }

    override fun onDestroy() {
        JMessageClient.unRegisterEventReceiver(this)
        super.onDestroy()
    }
}
