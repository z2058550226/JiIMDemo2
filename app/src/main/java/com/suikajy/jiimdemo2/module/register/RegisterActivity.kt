package com.suikajy.jiimdemo2.module.register

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import com.suikajy.jiimdemo2.R
import com.suikajy.jiimdemo2.common.UserInfo
import com.suikajy.jiimdemo2.jmessage.SuccessJCallBack
import kotlinx.android.synthetic.main.activity_register.*

/**
 *
 * @author zjy
 * @date 2017/11/3
 */
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initClick()
    }

    private fun initClick() {
        mBtnSubmit.setOnClickListener({
            val userName = mEtUserName.text.toString().trim()
            val psw = mEtPassword.text.toString().trim()
            JMessageClient.register(userName, psw, object : SuccessJCallBack() {
                override fun onSuccess() {
                    Toast.makeText(this@RegisterActivity, "注册成功", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
        })
    }
}