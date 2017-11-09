package com.suikajy.jiimdemo2.module.conversationList

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.model.Conversation
import com.suikajy.jiimdemo2.R
import kotlinx.android.synthetic.main.activity_conversation_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * @author zjy
 * @date 2017/11/3
 */
class ConversationListActivity : AppCompatActivity() {

    val mLayoutManager by lazy { LinearLayoutManager(this) }
    val mAdapter by lazy { ConversationListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_list)
        EventBus.getDefault().register(this)
        initView()
        initClick()
    }

    private fun initView() {
        mRvConversationList.layoutManager = mLayoutManager
        mRvConversationList.adapter = mAdapter
        refreshConversationList()
    }

    private fun initClick() {

    }

    private fun refreshConversationList() {
        val conversationList = JMessageClient.getConversationList() ?: emptyList<Conversation>()
        mAdapter.refreshData(conversationList)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEventMainThread(message: MessageEvent) {
        refreshConversationList()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}