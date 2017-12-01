package com.suikajy.jiimdemo2.module.private_chat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.Message
import com.suikajy.imgpicker.PickImageActivity
import com.suikajy.imgpicker.utils.*
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.common.*
import com.suikajy.jiimdemo2.jmessage.SuccessJCallBack
import com.suikajy.jiimdemo2.utils.DensityUtil
import com.suikajy.jiimdemo2.utils.closeKeyboard
import com.suikajy.jiimdemo2.utils.isSoftInputShow
import com.suikajy.jiimdemo2.utils.openKeyboard
import com.suikajy.jiimdemo2.widget.ReSizeLayout
import kotlinx.android.synthetic.main.activity_private_chat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * @author zjy
 * @date 2017/11/2
 */
class PrivateChatActivity : AppCompatActivity(), ReSizeLayout.OnSizeChangeListener {

    private val JPG = ".jpg"
    private var mChatAdapter: PrivateChatAdapter? = null
    private var mConv: Conversation? = null
    private val mRecyclerLayoutManager by lazy { LinearLayoutManager(this) }
    private var targetUserName by notNullSingleValue<String>()
    /**
     * 软键盘和菜单在展示时是互斥的，可以同时不展示
     */
    private var isMenuShow = false

    /**
     * 软键盘高度，通过SharedPreference缓存，如果没有就使用默认的200dp作为高度
     */
    private var keyboardHeight by preference(App.instance, "KEYBOARD_HEIGHT", DensityUtil.dip2px(App.instance, 200f))
    private var mScreenHeight: Int = 0

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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setContentView(R.layout.activity_private_chat)
        targetUserName = intent.getStringExtra(TARGET_USER_NAME_KEY)
        Log.e("****** TAG ******", "target user name is $targetUserName")
        mConv = JMessageClient.getSingleConversation(getChatDestUserName()) ?: Conversation.createSingleConversation(getChatDestUserName())
        initView()
        initClick()
    }

    private fun initView() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mScreenHeight = wm.defaultDisplay.height
        rsl.setOnSizeChangeListener(this)
        EventBus.getDefault().register(this)
        mRecyclerLayoutManager.stackFromEnd = true//设置默认展示RecyclerView底部
        mRecyclerView.layoutManager = mRecyclerLayoutManager
        mChatAdapter = PrivateChatAdapter(this, mConv!!, object : PrivateChatAdapter.ContentLongClickListener() {
            override fun onContentLongClick(position: Int, view: View) {
                toast("第${position}个item被长按")
            }
        })
        mRecyclerView.adapter = mChatAdapter
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
        mBtnPlus.setOnClickListener({
            if (isMenuShow) {
                //菜单打开状态下，继续点击弹出输入框
                isMenuShow = false
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                openKeyboard(mEtChatInput, this)
            } else {
                isMenuShow = true
                //解除EditText跟随
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                if (isSoftInputShow(this)) {
                    //菜单未展示，输入框打开状态下，打开菜单
                    closeKeyboard(mEtChatInput, this)
                    layout_input_menu.layoutParams.height = keyboardHeight
                    layout_input_menu.requestLayout()
                } else {
                    //菜单未展示，输入框关闭状态下，打开菜单
                    layout_input_menu.layoutParams.height = keyboardHeight
                    layout_input_menu.requestLayout()
                }
            }
        })
        mRecyclerView.setOnTouchListener { _, _ ->
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            isMenuShow = false
            layout_input_menu.layoutParams.height = 0
            layout_input_menu.requestLayout()
            closeKeyboard(mEtChatInput, this)
            false
        }
        btn_select_image.setOnClickListener({
            val from = PickImageActivity.FROM_LOCAL
            val requestCode = RequestCode.PICK_IMAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show()
            } else {
                PickImageActivity.start(this, requestCode, from, tempFile(), true, 9,
                        true, false, 0, 0)
            }
        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (h.toFloat() / mScreenHeight < 0.8f) {
            keyboardHeight = oldh - h
        }
        //设置EditText跟随
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        if (isSoftInputShow(this) && !isMenuShow) {
            layout_input_menu.layoutParams.height = 0
            layout_input_menu.requestLayout()
        }
    }

    private fun getChatDestUserName(): String {
        return targetUserName
    }

    private fun insertMessage(newMessage: Message) {
        mChatAdapter!!.insertOneMessage(newMessage)
        mRecyclerView.scrollToPosition(mChatAdapter!!.itemCount - 1)
    }

    /**
     * 刷新ui
     */
    private fun refreshMessageList() {
        if (mConv != null) {
            val allMessage = mConv!!.allMessage
            mChatAdapter!!.refreshData(allMessage)
            mRecyclerView.scrollToPosition(mChatAdapter!!.itemCount - 1)
        } else {

        }
    }

    /**
     * 图片选取回调
     */
    private fun onPickImageActivityResult(requestCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        val local = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false)
        if (local) {
            // 本地相册
            sendImageAfterSelfImagePicker(data)
        }
    }

    /**
     * 发送图片
     */
    private fun sendImageAfterSelfImagePicker(data: Intent) {
        SendImageHelper.sendImageAfterSelfImagePicker(this, data) { file, _ ->
            //所有图片都在这里拿到
            ImageContent.createImageContentAsync(file, object : ImageContent.CreateImageContentCallback() {
                override fun gotResult(responseCode: Int, responseMessage: String, imageContent: ImageContent) {
                    if (responseCode == 0) {
                        val msg = mConv!!.createSendMessage(imageContent)
                        handleSendMsg(msg.id)
                    }
                }
            })
        }
    }

    /**
     * 处理发送图片，刷新界面
     *
     * @param data intent
     */
    private fun handleSendMsg(data: Int) {
        mChatAdapter!!.setSendMsgs(data)
        mRecyclerView.scrollToPosition(mChatAdapter!!.itemCount - 1)
    }

    /**
     * 生成临时文件unionid，并作为文件名
     */
    private fun tempFile(): String {
        val filename = StringUtil.get32UUID() + JPG
        return StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEventMainThread(event: MessageEvent) {
        val message = event.message
        //toast("from ${message.fromUser}  to $targetUserName ${(message.content as TextContent).text} ")
        if (message.fromUser.userName == targetUserName) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.PICK_IMAGE//4
            -> onPickImageActivityResult(requestCode, data)
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}