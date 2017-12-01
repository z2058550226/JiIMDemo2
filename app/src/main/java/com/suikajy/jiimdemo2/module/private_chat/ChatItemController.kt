package com.suikajy.jiimdemo2.module.private_chat

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.DownloadCompletionCallback
import cn.jpush.im.android.api.callback.ProgressUpdateCallback
import cn.jpush.im.android.api.content.CustomContent
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.content.VoiceContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.enums.MessageStatus
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.android.api.options.MessageSendingOptions
import cn.jpush.im.api.BasicCallback
import com.bumptech.glide.Glide
import com.suikajy.imgpicker.utils.ToastUtil
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.module.private_chat.ViewHolder.*
import com.suikajy.jiimdemo2.utils.HandleResponseCode
import com.suikajy.jiimdemo2.utils.showToast
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 *
 * @author zjy
 * @date 2017/11/29
 */

class ChatItemController {
    //region Field member
    private val mAdapter: PrivateChatAdapter
    private val mContext: Activity
    private val mConv: Conversation
    private var mUserInfo: UserInfo? = null
    private val mMsgList: MutableList<Message>
    private val mDensity: Float
    val mSendingAnim: Animation
    private val mp = MediaPlayer()
    private val mLongClickListener: PrivateChatAdapter.ContentLongClickListener
    private val mMsgQueue = LinkedList<Message>()
    private var mSendMsgId: Int = 0
    private val mIndexList = ArrayList<Int>()//语音索引
    private var nextPlayPosition = 0
    private var autoPlay = false
    private var mPosition = -1// 和mSetData一起组成判断播放哪条录音的依据
    private var mSetData = false
    private var mVoiceAnimation: AnimationDrawable? = null
    private var mFIS: FileInputStream? = null
    private var mFD: FileDescriptor? = null
    private var mIsEarPhoneOn: Boolean = false
    //endregion

    constructor(adapter: PrivateChatAdapter, context: Activity, conv: Conversation, msgList: MutableList<Message>,
                density: Float, longClickListener: PrivateChatAdapter.ContentLongClickListener) {
        this.mAdapter = adapter
        this.mContext = context
        this.mConv = conv
        if (mConv.type == ConversationType.single) {
            mUserInfo = mConv.targetInfo as UserInfo
        }
        this.mMsgList = msgList
        this.mDensity = density
        this.mLongClickListener = longClickListener
        mSendingAnim = AnimationUtils.loadAnimation(mContext, R.anim.sending_rotate)
        mSendingAnim.interpolator = LinearInterpolator()

        val audioManager = mContext
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_NORMAL
        audioManager.isSpeakerphoneOn = audioManager.isSpeakerphoneOn
        mp.setAudioStreamType(AudioManager.STREAM_RING)
        mp.setOnErrorListener({ _, _, _ -> false })
    }

    //处理接收的语音
    fun handleVoiceMsg(msg: Message, holder: VoiceRecViewHolder, position: Int) {
        val content = msg.content as VoiceContent
        val length = content.duration
        val lengthStr = length.toString() + "\""
        holder.mTvVoiceLength.text = lengthStr
        //控制语音长度显示，长度增幅随语音长度逐渐缩小
        val width = (-0.04 * length.toDouble() * length.toDouble() + 4.526 * length + 75.214).toInt()
        holder.mTvLengthHolder.width = (width * mDensity).toInt()
        //要设置这个position
        holder.mTvLengthHolder.setOnLongClickListener(mLongClickListener)
        when (msg.status) {
            MessageStatus.receive_success -> {
                holder.mIvVoice.setImageResource(R.drawable.jmui_receive_3)
                // 收到语音，设置未读
                if (msg.content.getBooleanExtra("isRead") == null || !msg.content.getBooleanExtra("isRead")) {
                    mConv.updateMessageExtra(msg, "isRead", false)
                    holder.mIvReadStatus.visibility = View.VISIBLE
                    if (mIndexList.size > 0) {
                        if (!mIndexList.contains(position)) {
                            addToListAndSort(position)
                        }
                    } else {
                        addToListAndSort(position)
                    }
                    if (nextPlayPosition == position && autoPlay) {
                        playVoice(position, holder, false)
                    }
                } else if (msg.content.getBooleanExtra("isRead")!!) {
                    holder.mIvReadStatus.visibility = View.GONE
                }
            }
            MessageStatus.receive_fail -> {
                holder.mIvVoice.setImageResource(R.drawable.jmui_receive_3)
                // 接收失败，从服务器上下载
                content.downloadVoiceFile(msg,
                        object : DownloadCompletionCallback() {
                            override fun onComplete(status: Int, desc: String, file: File) {

                            }
                        })
            }
            MessageStatus.receive_going -> {
            }
            else -> {
            }
        }
        holder.mTvLengthHolder.setOnClickListener(BtnOrTxtListener(position, holder))
    }

    //处理发送的语音
    fun handleVoiceMsg(msg: Message, holder: VoiceSendViewHolder, position: Int) {
        val content = msg.content as VoiceContent
        val length = content.duration
        val lengthStr = length.toString() + "\""
        holder.mTvVoiceLength.text = lengthStr
        //控制语音长度显示，长度增幅随语音长度逐渐缩小
        val width = (-0.04 * length.toDouble() * length.toDouble() + 4.526 * length + 75.214).toInt()
        holder.mTvLengthHolder.width = (width * mDensity).toInt()
        //要设置这个position
        holder.mTvLengthHolder.setOnLongClickListener(mLongClickListener)
        holder.mIvVoice.setImageResource(R.drawable.send_3)
        when (msg.status) {
            MessageStatus.created -> {
                holder.mIvSending.visibility = View.VISIBLE
                holder.mIbFailResend.visibility = View.GONE
                holder.mTvReceipt.visibility = View.GONE
            }
            MessageStatus.send_success -> {
                holder.mIvSending.clearAnimation()
                holder.mIvSending.visibility = View.GONE
                holder.mIbFailResend.visibility = View.GONE
                holder.mTvReceipt.visibility = View.VISIBLE
            }
            MessageStatus.send_fail -> {
                holder.mIvSending.clearAnimation()
                holder.mIvSending.visibility = View.GONE
                holder.mTvReceipt.visibility = View.GONE
                holder.mIbFailResend.visibility = View.VISIBLE
            }
            MessageStatus.send_going -> sendingTextOrVoice(holder, msg)
            else -> {
            }
        }

        holder.mIbFailResend.setOnClickListener({
            if (msg.content != null) {
                mAdapter.showResendDialog(holder, msg)
            } else {
                Toast.makeText(mContext, R.string.jmui_sdcard_not_exist_toast, Toast.LENGTH_SHORT).show()
            }
        })
        holder.mTvLengthHolder.setOnClickListener(BtnOrTxtListener(position, holder))
    }

    // 处理发送的图片
    fun handleImgMsg(msg: Message, holder: ImgSendViewHolder, position: Int) {
        val imgContent = msg.content as ImageContent
        val jiguang = imgContent.getStringExtra("jiguang")
        // 先拿本地缩略图
        val path = imgContent.localThumbnailPath
        if (path == null) {
            //从服务器上拿缩略图
            imgContent.downloadThumbnailImage(msg, object : DownloadCompletionCallback() {
                override fun onComplete(status: Int, desc: String, file: File) {
                    if (status == 0) {
                        val imageView = setPictureScale(jiguang, msg, file.path, holder.mIvMsgImage)
                        Glide.with(mContext).load(file).into(imageView)
                    }
                }
            })
        } else {
            val imageView = setPictureScale(jiguang, msg, path, holder.mIvMsgImage)
            Glide.with(mContext).load(File(path)).into(imageView)
        }
        //检查状态
        when (msg.status) {
            MessageStatus.created -> {
                holder.mIvMsgImage.isEnabled = false
                holder.mIbFailResend.isEnabled = false
                holder.mTvReceipt.visibility = View.GONE
                holder.mIvSending.visibility = View.VISIBLE
                holder.mIbFailResend.visibility = View.GONE
                holder.mTvProgress.text = "0%"
            }
            MessageStatus.send_success -> {
                holder.mIvMsgImage.isEnabled = true
                holder.mIvSending.clearAnimation()
                holder.mTvReceipt.visibility = View.VISIBLE
                holder.mIvSending.visibility = View.GONE
                holder.mIvMsgImage.alpha = 1.0f
                holder.mTvProgress.visibility = View.GONE
                holder.mIbFailResend.visibility = View.GONE
            }
            MessageStatus.send_fail -> {
                holder.mIbFailResend.isEnabled = true
                holder.mIvMsgImage.isEnabled = true
                holder.mIvSending.clearAnimation()
                holder.mIvSending.visibility = View.GONE
                holder.mTvReceipt.visibility = View.GONE
                holder.mIvMsgImage.alpha = 1.0f
                holder.mTvProgress.visibility = View.GONE
                holder.mIbFailResend.visibility = View.VISIBLE
            }
            MessageStatus.send_going -> {
                holder.mIvMsgImage.isEnabled = false
                holder.mIbFailResend.isEnabled = false
                holder.mTvReceipt.visibility = View.GONE
                holder.mIbFailResend.visibility = View.GONE
                sendingImage(msg, holder)
            }
            else -> {
                holder.mIvMsgImage.alpha = 0.75f
                holder.mIvSending.visibility = View.VISIBLE
                holder.mIvSending.startAnimation(mSendingAnim)
                holder.mTvProgress.visibility = View.VISIBLE
                holder.mTvProgress.text = "0%"
                holder.mIbFailResend.visibility = View.GONE
                //从别的界面返回聊天界面，继续发送
                if (!mMsgQueue.isEmpty()) {
                    val message = mMsgQueue.element()
                    if (message.id == msg.id) {
                        val options = MessageSendingOptions()
                        options.isNeedReadReceipt = true
                        JMessageClient.sendMessage(message, options)
                        mSendMsgId = message.id
                        sendingImage(message, holder)
                    }
                }
            }
        }
        // 点击预览图片
        holder.mIvMsgImage.setOnClickListener(BtnOrTxtListener(position, holder))
        holder.mIvMsgImage.setOnLongClickListener(mLongClickListener)

        if (msg.direct == MessageDirect.send) {
            holder.mIbFailResend.setOnClickListener({ mAdapter.showResendDialog(holder, msg) })
        }
    }

    // 处理接收的图片
    fun handleImgMsg(msg: Message, holder: ImgRecViewHolder, position: Int) {
        val imgContent = msg.content as ImageContent
        val jiguang = imgContent.getStringExtra("jiguang")
        // 先拿本地缩略图
        val path = imgContent.localThumbnailPath
        if (path == null) {
            //从服务器上拿缩略图
            imgContent.downloadThumbnailImage(msg, object : DownloadCompletionCallback() {
                override fun onComplete(status: Int, desc: String, file: File) {
                    if (status == 0) {
                        val imageView = setPictureScale(jiguang, msg, file.path, holder.mIvMsgImage)
                        Glide.with(mContext).load(file).into(imageView)
                    }
                }
            })
        } else {
            val imageView = setPictureScale(jiguang, msg, path, holder.mIvMsgImage)
            Glide.with(mContext).load(File(path)).into(imageView)
        }
        if (msg.status == MessageStatus.receive_fail) {
            holder.mIvMsgImage.setImageResource(R.drawable.jmui_fetch_failed)
            holder.mIbFailResend.visibility = View.VISIBLE
            holder.mIbFailResend.setOnClickListener({
                imgContent.downloadOriginImage(msg, object : DownloadCompletionCallback() {
                    override fun onComplete(i: Int, s: String, file: File) {
                        if (i == 0) {
                            showToast("下载成功")
                            holder.mIvSending.visibility = View.GONE
                            mAdapter.notifyDataSetChanged()
                        } else {
                            showToast("下载失败" + s)
                        }
                    }
                })
            })
        }

        // 点击预览图片
        holder.mIvMsgImage.setOnClickListener(BtnOrTxtListener(position, holder))
        holder.mIvMsgImage.setOnLongClickListener(mLongClickListener)
    }

    fun playVoice(position: Int, holder: VoiceRecViewHolder, isSender: Boolean) {
        // 记录播放录音的位置
        mPosition = position
        val msg = mMsgList[position]
        if (autoPlay) {
            mConv.updateMessageExtra(msg, "isRead", true)
            holder.mIvReadStatus.visibility = View.GONE
            if (mVoiceAnimation != null) {
                mVoiceAnimation!!.stop()
                mVoiceAnimation = null
            }
            holder.mIvVoice.setImageResource(R.drawable.jmui_voice_receive)
            mVoiceAnimation = holder.mIvVoice.drawable as AnimationDrawable
        }
        try {
            mp.reset()
            val vc = msg.content as VoiceContent
            mFIS = FileInputStream(vc.localPath)
            mFD = mFIS!!.fd
            mp.setDataSource(mFD)
            if (mIsEarPhoneOn) {
                mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL)
            } else {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
            }
            mp.prepare()
            mp.setOnPreparedListener { mp ->
                mVoiceAnimation!!.start()
                mp.start()
            }
            mp.setOnCompletionListener { mp ->
                mVoiceAnimation!!.stop()
                mp.reset()
                mSetData = false
                if (isSender) {
                    holder.mIvVoice.setImageResource(R.drawable.send_3)
                } else {
                    holder.mIvVoice.setImageResource(R.drawable.jmui_receive_3)
                }
                if (autoPlay) {
                    val curCount = mIndexList.indexOf(position)
                    if (curCount + 1 >= mIndexList.size) {
                        nextPlayPosition = -1
                        autoPlay = false
                    } else {
                        nextPlayPosition = mIndexList[curCount + 1]
                        mAdapter.notifyDataSetChanged()
                    }
                    mIndexList.removeAt(curCount)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(mContext, "音频文件未找到",
                    Toast.LENGTH_SHORT).show()
            val vc = msg.content as VoiceContent
            vc.downloadVoiceFile(msg, object : DownloadCompletionCallback() {
                override fun onComplete(status: Int, desc: String, file: File) {
                    if (status == 0) {
                        Toast.makeText(mContext, R.string.download_completed_toast,
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(mContext, R.string.file_fetch_failed,
                                Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } finally {
            try {
                if (mFIS != null) {
                    mFIS!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    //正在发送文字或语音
    private fun sendingTextOrVoice(holder: VoiceSendViewHolder, msg: Message) {
        holder.mTvReceipt.visibility = View.GONE
        holder.mIbFailResend.visibility = View.GONE
        holder.mIvSending.visibility = View.VISIBLE
        holder.mIvSending.startAnimation(mSendingAnim)
        //消息正在发送，重新注册一个监听消息发送完成的Callback
        if (!msg.isSendCompleteCallbackExists) {
            msg.setOnSendCompleteCallback(object : BasicCallback() {
                override fun gotResult(status: Int, desc: String) {
                    holder.mIvSending.visibility = View.GONE
                    holder.mIvSending.clearAnimation()
                    when {
                        status == 803008 -> {
                            val customContent = CustomContent()
                            customContent.setBooleanValue("blackList", true)
                            val customMsg = mConv.createSendMessage(customContent)
                            mAdapter.insertOneMessage(customMsg)
                        }
                        status == 803005 -> {
                            holder.mIbFailResend.visibility = View.VISIBLE
                            ToastUtil.shortToast(mContext, "发送失败, 你不在该群组中")
                        }
                        status != 0 -> {
                            holder.mIbFailResend.visibility = View.VISIBLE
                            HandleResponseCode.onHandle(mContext, status, false)
                        }
                    }
                }
            })
        }
    }

    /**
     * 将语音消息在mMsgList中的索引单独存放到一个集合中，以便将音频连续播放
     */
    private fun addToListAndSort(position: Int) {
        mIndexList.add(position)
        Collections.sort(mIndexList)
    }

    private fun sendingImage(msg: Message, holder: ImgSendViewHolder) {
        holder.mIvMsgImage.alpha = 0.75f
        holder.mIvSending.visibility = View.VISIBLE
        holder.mIvSending.startAnimation(mSendingAnim)
        holder.mTvProgress.visibility = View.VISIBLE
        holder.mTvProgress.text = "0%"
        holder.mIbFailResend.visibility = View.GONE
        //如果图片正在发送，重新注册上传进度Callback
        if (!msg.isContentUploadProgressCallbackExists) {
            msg.setOnContentUploadProgressCallback(object : ProgressUpdateCallback() {
                override fun onProgressUpdate(v: Double) {
                    val progressStr = (v * 100).toInt().toString() + "%"
                    holder.mTvProgress.text = progressStr
                }
            })
        }
        if (!msg.isSendCompleteCallbackExists) {
            msg.setOnSendCompleteCallback(object : BasicCallback() {
                override fun gotResult(status: Int, desc: String) {
                    if (!mMsgQueue.isEmpty() && mMsgQueue.element().id == mSendMsgId) {
                        mMsgQueue.poll()
                        if (!mMsgQueue.isEmpty()) {
                            val nextMsg = mMsgQueue.element()
                            val options = MessageSendingOptions()
                            options.isNeedReadReceipt = true
                            JMessageClient.sendMessage(nextMsg, options)
                            mSendMsgId = nextMsg.id
                        }
                    }
                    holder.mIvMsgImage.alpha = 1.0f
                    holder.mIvSending.clearAnimation()
                    holder.mIvSending.visibility = View.GONE
                    holder.mTvProgress.visibility = View.GONE
                    if (status == 803008) {
                        val customContent = CustomContent()
                        customContent.setBooleanValue("blackList", true)
                        val customMsg = mConv.createSendMessage(customContent)
                        mAdapter.insertOneMessage(customMsg)
                    } else if (status != 0) {
                        holder.mIbFailResend.visibility = View.VISIBLE
                    }

                    val message = mConv.getMessage(msg.id)
                    mMsgList[mMsgList.indexOf(msg)] = message
                    //                    notifyDataSetChanged();
                }
            })
        }
    }

    /**
     * 设置图片最小宽高
     * todo:这里图片测宽高计算是错的
     *
     * @param path      图片路径
     * @param imageView 显示图片的View
     */
    private fun setPictureScale(extra: String?, message: Message, path: String, imageView: ImageView): ImageView {

        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, opts)


        //计算图片缩放比例
        val imageWidth = opts.outWidth.toDouble()
        val imageHeight = opts.outHeight.toDouble()
        return setDensity(extra, message, imageWidth, imageHeight, imageView)
    }

    private fun setDensity(extra: String?, message: Message, imageWidth: Double, imageHeight: Double, imageView: ImageView): ImageView {
        var imgWidth = imageWidth
        var imgHeight = imageHeight
        if (extra != null) {
            imgWidth = 200.0
            imgHeight = 200.0
        } else {
            if (imgWidth > 350) {
                imgWidth = 550.0
                imgHeight = 250.0
            } else if (imgHeight > 450) {
                imgWidth = 300.0
                imgHeight = 450.0
            } else if (imgWidth < 50 && imgWidth > 20 || imgHeight < 50 && imgHeight > 20) {
                imgWidth = 200.0
                imgHeight = 300.0
            } else if (imgWidth < 20 || imgHeight < 20) {
                imgWidth = 100.0
                imgHeight = 150.0
            } else {
                imgWidth = 300.0
                imgHeight = 450.0
            }
        }
        val params = imageView.layoutParams
        params.width = imgWidth.toInt()
        params.height = imgHeight.toInt()
        imageView.layoutParams = params
        return imageView
    }

    /**
     * TODO:可能需要注册一个广播来监听耳机是否插上
     */
    fun setAudioPlayByEarPhone(state: Int) {
        val audioManager = mContext
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        audioManager.mode = AudioManager.MODE_IN_CALL
        if (state == 0) {
            mIsEarPhoneOn = false
            audioManager.isSpeakerphoneOn = true
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL)
        } else {
            mIsEarPhoneOn = true
            audioManager.isSpeakerphoneOn = false
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                    AudioManager.STREAM_VOICE_CALL)
        }
    }

    inner class BtnOrTxtListener(private val position: Int, private val holder: BaseViewHolder) : View.OnClickListener {

        override fun onClick(v: View) {
            val msg = mMsgList[position]
            val msgDirect = msg.direct
            when (msg.contentType) {
                ContentType.voice -> {
//                    if (!FileHelper.isSdCardExist()) {
//                        Toast.makeText(mContext, R.string.jmui_sdcard_not_exist_toast,
//                                Toast.LENGTH_SHORT).show()
//                        return
//                    }
//                    // 如果之前存在播放动画，无论这次点击触发的是暂停还是播放，停止上次播放的动画
//                    if (mVoiceAnimation != null) {
//                        mVoiceAnimation.stop()
//                    }
//                    // 播放中点击了正在播放的Item 则暂停播放
//                    if (mp.isPlaying && mPosition == position) {
//                        if (msgDirect == MessageDirect.send) {
//                            holder.voice.setImageResource(R.drawable.jmui_voice_send)
//                        } else {
//                            holder.voice.setImageResource(R.drawable.jmui_voice_receive)
//                        }
//                        mVoiceAnimation = holder.voice.getDrawable() as AnimationDrawable
//                        pauseVoice(msgDirect, holder.voice)
//                        // 开始播放录音
//                    } else if (msgDirect == MessageDirect.send) {
//                        holder.voice.setImageResource(R.drawable.jmui_voice_send)
//                        mVoiceAnimation = holder.voice.getDrawable() as AnimationDrawable
//
//                        // 继续播放之前暂停的录音
//                        if (mSetData && mPosition == position) {
//                            mVoiceAnimation.start()
//                            mp.start()
//                            // 否则重新播放该录音或者其他录音
//                        } else {
//                            playVoice(position, holder, true)
//                        }
//                        // 语音接收方特殊处理，自动连续播放未读语音
//                    } else {
//                        try {
//                            // 继续播放之前暂停的录音
//                            if (mSetData && mPosition == position) {
//                                if (mVoiceAnimation != null) {
//                                    mVoiceAnimation.start()
//                                }
//                                mp.start()
//                                // 否则开始播放另一条录音
//                            } else {
//                                // 选中的录音是否已经播放过，如果未播放，自动连续播放这条语音之后未播放的语音
//                                if (msg.content.getBooleanExtra("isRead") == null || !msg.content.getBooleanExtra("isRead")) {
//                                    autoPlay = true
//                                    playVoice(position, holder, false)
//                                    // 否则直接播放选中的语音
//                                } else {
//                                    holder.voice.setImageResource(R.drawable.jmui_voice_receive)
//                                    mVoiceAnimation = holder.voice.getDrawable() as AnimationDrawable
//                                    playVoice(position, holder, false)
//                                }
//                            }
//                        } catch (e: IllegalArgumentException) {
//                            e.printStackTrace()
//                        } catch (e: SecurityException) {
//                            e.printStackTrace()
//                        } catch (e: IllegalStateException) {
//                            e.printStackTrace()
//                        }
//
//                    }
                }
                ContentType.image -> {
                    showToast("点击图片")
//                    if (holder.picture != null && v.id == holder.picture.getId()) {
//                        val intent = Intent()
//                        intent.putExtra(JGApplication.TARGET_ID, mConv.targetId)
//                        intent.putExtra("msgId", msg.id)
//                        if (mConv.type == ConversationType.group) {
//                            val groupInfo = mConv.targetInfo as GroupInfo
//                            intent.putExtra(JGApplication.GROUP_ID, groupInfo.groupID)
//                        }
//                        intent.putExtra(JGApplication.TARGET_APP_KEY, mConv.targetAppKey)
//                        intent.putExtra("msgCount", mMsgList.size)
//                        intent.putIntegerArrayListExtra(JGApplication.MsgIDs, getImgMsgIDList())
//                        intent.putExtra("fromChatActivity", true)
//                        intent.setClass(mContext, BrowserViewPagerActivity::class.java!!)
//                        mContext.startActivity(intent)
//                    }
                }
                ContentType.location -> {
                    showToast("点击定位")
//                    if (holder.picture != null && v.id == holder.picture.getId()) {
//                        val intent = Intent(mContext, MapPickerActivity::class.java)
//                        val locationContent = msg.content as LocationContent
//                        intent.putExtra("latitude", locationContent.latitude.toDouble())
//                        intent.putExtra("longitude", locationContent.longitude.toDouble())
//                        intent.putExtra("locDesc", locationContent.address)
//                        intent.putExtra("sendLocation", false)
//                        mContext.startActivity(intent)
//                    }
                }
                ContentType.file -> {
//                    val content = msg.content as FileContent
//                    var fileName = content.fileName
//                    val extra = content.getStringExtra("video")
//                    if (extra != null) {
//                        fileName = msg.serverMessageId.toString() + "." + extra
//                    }
//                    val path = content.localPath
//                    if (path != null && File(path).exists()) {
//                        val newPath = JGApplication.FILE_DIR + fileName
//                        val file = File(newPath)
//                        if (file.exists() && file.isFile()) {
//                            browseDocument(fileName, newPath)
//                        } else {
//                            val finalFileName = fileName
//                            FileHelper.getInstance().copyFile(fileName, path, mContext,
//                                    object : FileHelper.CopyFileCallback() {
//                                        fun copyCallback(uri: Uri) {
//                                            browseDocument(finalFileName, newPath)
//                                        }
//                                    })
//                        }
//                    } else {
//                        org.greenrobot.eventbus.EventBus.getDefault().postSticky(msg)
//                        val intent = Intent(mContext, DownLoadActivity::class.java)
//                        mContext.startActivity(intent)
//                    }
                }
            }

        }
    }
}
