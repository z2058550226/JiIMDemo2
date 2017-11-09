package com.suikajy.jiimdemo2.module.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.common.UserInfo
import com.suikajy.jiimdemo2.jmessage.SuccessJCallBack
import kotlinx.android.synthetic.main.activity_login.*

/**
 *
 * @author zjy
 * @date 2017/11/3
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initClick()
    }

    private fun initClick() {
        mBtnSubmit.setOnClickListener({
            val userName = mEtUserName.text.toString().trim()
            val psw = mEtPassword.text.toString().trim()
            JMessageClient.login(userName, psw, object : SuccessJCallBack() {
                override fun onSuccess() {
                    UserInfo.userName = userName
                    UserInfo.userPsw = psw
                    Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
        })
    }
}