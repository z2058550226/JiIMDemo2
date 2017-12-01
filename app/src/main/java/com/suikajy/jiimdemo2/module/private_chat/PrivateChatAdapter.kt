package com.suikajy.jiimdemo2.module.private_chat

import android.app.Activity
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.callback.ProgressUpdateCallback
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.enums.MessageStatus
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.android.api.options.MessageSendingOptions
import cn.jpush.im.api.BasicCallback
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.base.BaseRecyclerAdapter
import com.suikajy.jiimdemo2.common.inflate
import com.suikajy.jiimdemo2.module.private_chat.ViewHolder.*
import com.suikajy.jiimdemo2.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import kotterknife.bindView
import java.util.*


/**
 *
 * @author zjy
 * @date 2017/11/2
 */
class PrivateChatAdapter : BaseRecyclerAdapter<BaseViewHolder, Message> {


    //region 常量值
    //每页消息数，也是最多不显示时间的信息条数
    private val PAGE_MESSAGE_COUNT = 18
    //文本
    private val TYPE_SEND_TXT = 0
    private val TYPE_RECEIVE_TXT = 1

    // 图片
    private val TYPE_SEND_IMAGE = 2
    private val TYPE_RECEIVER_IMAGE = 3

    //文件
    private val TYPE_SEND_FILE = 4
    private val TYPE_RECEIVE_FILE = 5
    // 语音
    private val TYPE_SEND_VOICE = 6
    private val TYPE_RECEIVER_VOICE = 7
    // 位置
    private val TYPE_SEND_LOCATION = 8
    private val TYPE_RECEIVER_LOCATION = 9
    //群成员变动
    private val TYPE_GROUP_CHANGE = 10
    //视频
    private val TYPE_SEND_VIDEO = 11
    private val TYPE_RECEIVE_VIDEO = 12
    //自定义消息
    private val TYPE_CUSTOM_TXT = 13
    //endregion
    private val mActivity: Activity
    private var mOffset: Int
    private val mController: ChatItemController
    private val mConv: Conversation
    private val mWidth: Int
    //发送图片消息的队列
    private val mMsgQueue = LinkedList<Message>()


    constructor(mActivity: Activity, conv: Conversation, longClickListener: ContentLongClickListener) : super() {
        this.mActivity = mActivity
        this.mOffset = PAGE_MESSAGE_COUNT
        this.mConv = conv
        this.dataSet.addAll(mConv.getMessagesFromNewest(0, mOffset))
        val dm = DisplayMetrics()
        mActivity.windowManager.defaultDisplay.getMetrics(dm)
        mWidth = dm.widthPixels
        this.mController = ChatItemController(this, mActivity, conv, dataSet, dm.density, longClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        if (parent != null) {
            return when (viewType) {
                TYPE_RECEIVE_TXT -> TxtRecViewHolder(parent.inflate(R.layout.item_private_chat_text_receive))
                TYPE_SEND_TXT -> TxtSendViewHolder(parent.inflate(R.layout.item_private_chat_text_send))
                TYPE_RECEIVER_IMAGE -> ImgRecViewHolder(parent.inflate(R.layout.item_private_chat_image_receive))
                TYPE_SEND_IMAGE -> ImgSendViewHolder(parent.inflate(R.layout.item_private_chat_image_send))
                TYPE_RECEIVER_VOICE -> VoiceRecViewHolder(parent.inflate(R.layout.item_private_chat_voice_receive))
                TYPE_SEND_VOICE -> VoiceSendViewHolder(parent.inflate(R.layout.item_private_chat_voice_send))
                else -> throw IllegalArgumentException("view type is wrong")
            }
        } else throw IllegalArgumentException("parent is null")
    }

    override fun onBindViewHolder(holder: BaseViewHolder?, position: Int) {
        if (holder != null) {
            val message = dataSet[position]
            val userInfo = message.fromUser
            //region 获取用户头像
            userInfo.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                override fun gotResult(status: Int, desc: String, bitmap: Bitmap) {
                    if (status == 0) {
                        holder.mIvAvatar.setImageBitmap(bitmap)
                    } else {
                        holder.mIvAvatar.setImageResource(R.drawable.ic_launcher)
                    }
                }
            })
            //endregion
            //region 通过计算判断每条信息上的时间的显示
            val nowDate = message.createTime
            if (mOffset == 18) {
                if (position == 0 || position % 18 == 0) {
                    val timeFormat = TimeFormat(mActivity, nowDate)
                    holder.mTvChatMsgTime.text = timeFormat.detailTime
                    holder.mTvChatMsgTime.visibility = View.VISIBLE
                } else {
                    val lastDate = dataSet[position - 1].createTime
                    // 如果两条消息之间的间隔超过五分钟则显示时间
                    if (nowDate - lastDate > 300000) {
                        val timeFormat = TimeFormat(mActivity, nowDate)
                        holder.mTvChatMsgTime.text = timeFormat.detailTime
                        holder.mTvChatMsgTime.visibility = View.VISIBLE
                    } else {
                        holder.mTvChatMsgTime.visibility = View.GONE
                    }
                }
            } else {
                if (position == 0 || position == mOffset
                        || (position - mOffset) % 18 == 0) {
                    val timeFormat = TimeFormat(mActivity, nowDate)

                    holder.mTvChatMsgTime.text = timeFormat.detailTime
                    holder.mTvChatMsgTime.visibility = View.VISIBLE
                } else {
                    val lastDate = dataSet[position - 1].createTime
                    // 如果两条消息之间的间隔超过五分钟则显示时间
                    if (nowDate - lastDate > 300000) {
                        val timeFormat = TimeFormat(mActivity, nowDate)
                        holder.mTvChatMsgTime.text = timeFormat.detailTime
                        holder.mTvChatMsgTime.visibility = View.VISIBLE
                    } else {
                        holder.mTvChatMsgTime.visibility = View.GONE
                    }
                }
            }
            //endregion
            when (message.contentType) {
                ContentType.text -> {
                    val textContent = message.content as TextContent
                    if (message.direct == MessageDirect.send) (holder as TxtSendViewHolder).mTvMsgText.text = textContent.text
                    else (holder as TxtRecViewHolder).mTvMsgText.text = textContent.text
                }
                ContentType.image -> {
                    if (message.direct == MessageDirect.send) mController.handleImgMsg(message, holder as ImgSendViewHolder, position)
                    else mController.handleImgMsg(message, holder as ImgRecViewHolder, position)
                }
                else -> {
                    if (message.direct == MessageDirect.send) mController.handleVoiceMsg(message, holder as VoiceSendViewHolder, position)
                    else mController.handleVoiceMsg(message, holder as VoiceRecViewHolder, position)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val msg = dataSet[position]
        //是文字类型或者自定义类型（用来显示群成员变化消息）
        when (msg.contentType) {
            ContentType.text -> return if (msg.direct == MessageDirect.send) TYPE_SEND_TXT else TYPE_RECEIVE_TXT
            ContentType.image -> return if (msg.direct == MessageDirect.send) TYPE_SEND_IMAGE else TYPE_RECEIVER_IMAGE
            ContentType.file -> {
                val extra = msg.content.getStringExtra("video")
                return if (!TextUtils.isEmpty(extra)) {
                    if (msg.direct == MessageDirect.send) TYPE_SEND_VIDEO else TYPE_RECEIVE_VIDEO
                } else {
                    if (msg.direct == MessageDirect.send) TYPE_SEND_FILE else TYPE_RECEIVE_FILE
                }
            }
            ContentType.voice -> return if (msg.direct == MessageDirect.send) TYPE_SEND_VOICE else TYPE_RECEIVER_VOICE
            ContentType.location -> return if (msg.direct == MessageDirect.send) TYPE_SEND_LOCATION else TYPE_RECEIVER_LOCATION
            ContentType.eventNotification, ContentType.prompt -> return TYPE_GROUP_CHANGE
            else -> return TYPE_CUSTOM_TXT
        }
    }

    fun insertOneMessage(message: Message) {
        val size = dataSet.size
        dataSet.add(message)
        notifyItemInserted(size)
    }

    //重发对话框
    fun showResendDialog(holder: BaseViewHolder, msg: Message) {
        createResendDialog(mActivity, object : OnEnsureListener {
            override fun onEnsure() {
                when (msg.contentType) {
//                    ContentType.text, ContentType.voice -> resendTextOrVoice(holder, msg)
                    ContentType.image -> resendImage(holder as ImgSendViewHolder, msg)
//                    ContentType.file -> resendFile(holder, msg)
                    else -> {
                        showToast("该类型message重发未处理")
                    }
                }
            }
        }).show()
    }

    //region 发送图片相关方法
    /**
     * 点击重发图片
     */
    private fun resendImage(holder: ImgSendViewHolder, msg: Message) {
        holder.mIvSending.visibility = View.VISIBLE
        holder.mIvSending.startAnimation(mController.mSendingAnim)
        holder.mIvMsgImage.alpha = 0.75f
        holder.mIbFailResend.visibility = View.GONE
        holder.mTvProgress.visibility = View.VISIBLE
        try {
            // 显示上传进度
            msg.setOnContentUploadProgressCallback(object : ProgressUpdateCallback() {
                override fun onProgressUpdate(progress: Double) {
                    mActivity.runOnUiThread {
                        val progressStr = (progress * 100).toInt().toString() + "%"
                        holder.mTvProgress.text = progressStr
                    }
                }
            })
            if (!msg.isSendCompleteCallbackExists) {
                msg.setOnSendCompleteCallback(object : BasicCallback() {
                    override fun gotResult(status: Int, desc: String) {
                        holder.mIvSending.clearAnimation()
                        holder.mIvSending.visibility = View.GONE
                        holder.mTvProgress.visibility = View.GONE
                        holder.mIvMsgImage.alpha = 1.0f
                        if (status != 0) {
                            HandleResponseCode.onHandle(mActivity, status, false)
                            holder.mIbFailResend.visibility = View.VISIBLE
                        }
                    }
                })
            }
            val options = MessageSendingOptions()
            options.isNeedReadReceipt = true
            JMessageClient.sendMessage(msg, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setSendMsgs(msgIds: Int) {
        val msg = mConv.getMessage(msgIds)
        if (msg != null) {
            dataSet.add(msg)
            mMsgQueue.offer(msg)
        }

        if (mMsgQueue.size > 0) {
            val message = mMsgQueue.element()
            if (mConv.type == ConversationType.single) {
                val userInfo = message.targetInfo as UserInfo
                sendNextImgMsg(message)
            } else {
                sendNextImgMsg(message)
            }

            notifyDataSetChanged()
        }
    }

    /**
     * 检查图片是否处于创建状态，如果是，则加入发送队列
     */
    private fun checkSendingImgMsg() {
        //此处写法类似RxJava
        dataSet
                .filter { it.status == MessageStatus.created && it.contentType == ContentType.image }
                .forEach { mMsgQueue.offer(it) }

        if (mMsgQueue.size > 0) {
            val message = mMsgQueue.element()
            if (mConv.type == ConversationType.single) {
                sendNextImgMsg(message)
            } else {
                sendNextImgMsg(message)
            }

            notifyDataSetChanged()
        }
    }

    /**
     * 从发送队列中出列，并发送图片
     *
     * @param msg 图片消息
     */
    private fun sendNextImgMsg(msg: Message) {
        val options = MessageSendingOptions()
        options.isNeedReadReceipt = true
        JMessageClient.sendMessage(msg, options)
        msg.setOnSendCompleteCallback(object : BasicCallback() {
            override fun gotResult(i: Int, s: String) {
                //出列
                mMsgQueue.poll()
                //如果队列不为空，则继续发送下一张
                if (!mMsgQueue.isEmpty()) {
                    sendNextImgMsg(mMsgQueue.element())
                }
                notifyDataSetChanged()
            }
        })
    }
    //endregion

    /**
     * 长按监听
     */
    abstract class ContentLongClickListener : View.OnLongClickListener {

        override fun onLongClick(v: View): Boolean {
            onContentLongClick(v.tag as Int, v)
            return true
        }

        abstract fun onContentLongClick(position: Int, view: View)
    }


}