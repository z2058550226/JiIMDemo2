package com.suikajy.jiimdemo2.module.signin

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.common.UserInfo
import com.suikajy.jiimdemo2.sample.SampleUser
import kotlinx.android.synthetic.main.activity_sign_in.*

/**
 * Created by zjy on 2017/11/2.
 * 设置Activity
 */
class SignInActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initClick()
    }

    private fun initClick() {
        mBtnSignUpReimu.setOnClickListener({
            mProgressDialog = ProgressDialog.show(this@SignInActivity, "提示：", "正在注册中。。。")
            JMessageClient.register(SampleUser.REIMU.userName, SampleUser.REIMU.password, object : BasicCallback() {
                override fun gotResult(responseCode: Int, registerDesc: String?) {
                    if (responseCode == 0) {
                        mProgressDialog.dismiss()
                        Toast.makeText(applicationContext, "注册成功", Toast.LENGTH_SHORT).show()
                        Log.i(this@SignInActivity.javaClass.name, "JMessageClient.register , responseCode = $responseCode ; registerDesc = $registerDesc")
                        UserInfo.userName = SampleUser.REIMU.userName
                        UserInfo.userPsw = SampleUser.REIMU.password
                        finish()
                    } else {
                        mProgressDialog.dismiss()
                        Toast.makeText(applicationContext, "注册失败$registerDesc", Toast.LENGTH_SHORT).show()
                        Log.i(this@SignInActivity.javaClass.name, "JMessageClient.register , responseCode = $responseCode ; registerDesc = $registerDesc")
                    }
                }
            })
        })
        mBtnSignUpSuika.setOnClickListener({
            SampleUser.SUIKA.let {
                mProgressDialog = ProgressDialog.show(this@SignInActivity, "提示：", "正在注册中。。。")
                JMessageClient.register(it.userName, it.password, object : BasicCallback() {
                    override fun gotResult(responseCode: Int, registerDesc: String?) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss()
                            Toast.makeText(applicationContext, "注册成功", Toast.LENGTH_SHORT).show()
                            Log.i(this@SignInActivity.javaClass.name, "JMessageClient.register , responseCode = $responseCode ; registerDesc = $registerDesc")
                            UserInfo.userName = it.userName
                            UserInfo.userPsw = it.password
                            finish()
                        } else {
                            mProgressDialog.dismiss()
                            Toast.makeText(applicationContext, "注册失败$registerDesc", Toast.LENGTH_SHORT).show()
                            Log.i(this@SignInActivity.javaClass.name, "JMessageClient.register , responseCode = $responseCode ; registerDesc = $registerDesc")
                        }
                    }
                })
            }
        })
        mBtnSignInReimu.setOnClickListener({
            mProgressDialog = ProgressDialog.show(this@SignInActivity, "提示：", "正在登录中。。。")
            SampleUser.REIMU.let {
                JMessageClient.login(it.userName, it.password, object : BasicCallback() {
                    override fun gotResult(responseCode: Int, registerDesc: String?) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss()
                            Toast.makeText(applicationContext, "登录成功", Toast.LENGTH_SHORT).show()
                            Log.i(this@SignInActivity.javaClass.name, "JMessageClient.register , responseCode = $responseCode ; registerDesc = $registerDesc")
                            UserInfo.userName = it.userName
                            UserInfo.userPsw = it.password
                            finish()
                        } else {
                            mProgressDialog.dismiss()
                            Toast.makeText(applicationContext, "登录失败 $registerDesc", Toast.LENGTH_SHORT).show()
                            Log.i(this@SignInActivity.javaClass.name, "JMessageClient.register , responseCode = $responseCode ; registerDesc = $registerDesc")
                        }
                    }
                })
            }
        })
        mBtnSignInSuika.setOnClickListener({ view: View ->
            SampleUser.SUIKA.let {
                mProgressDialog = ProgressDialog.show(this@SignInActivity, "提示：", "正在登录中。。。")
                JMessageClient.login(it.userName, it.password, object : BasicCallback() {
                    override fun gotResult(responseCode: Int, registerDesc: String?) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss()
                            Toast.makeText(applicationContext, "登录成功", Toast.LENGTH_SHORT).show()
                            Log.i(this@SignInActivity.javaClass.name, "JMessageClient.register , responseCode = $responseCode ; registerDesc = $registerDesc")
                            UserInfo.userName = it.userName
                            UserInfo.userPsw = it.password
                            finish()
                        } else {
                            mProgressDialog.dismiss()
                            Toast.makeText(applicationContext, "登录失败$registerDesc", Toast.LENGTH_SHORT).show()
                            Log.i(this@SignInActivity.javaClass.name, "JMessageClient.register , responseCode = $responseCode ; registerDesc = $registerDesc")
                        }
                    }
                })
            }
        })
    }
}