package com.suikajy.jiimdemo2.common

/**
 * Created by zjy on 2017/11/2.
 */
object UserInfo {
    var userName: String by preference(App.instance, PREF_USER_NAME_KEY, "")
    var userPsw: String by preference(App.instance, PREF_USER_PSW_KEY, "")

    fun clearUserData() {
        userName = ""
        userPsw = ""
    }
}