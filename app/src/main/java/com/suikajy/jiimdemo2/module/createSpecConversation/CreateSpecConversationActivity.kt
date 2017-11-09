package com.suikajy.jiimdemo2.module.createSpecConversation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.module.private_chat.PrivateChatActivity
import kotlinx.android.synthetic.main.activity_create_spec_conv.*

/**
 *
 * @author zjy
 * @date 2017/11/6
 */
class CreateSpecConversationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_spec_conv)
        initClick()
    }

    private fun initClick() {
        mBtnStartPrivateChat.setOnClickListener({
            PrivateChatActivity.start(this@CreateSpecConversationActivity, mEtTargetUserName.text.toString().trim())
        })
    }
}