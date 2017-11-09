package com.suikajy.jiimdemo2.module.private_chat.inputFragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suikajy.jiimdemo2.R

/**
 *
 * @author zjy
 * @date 2017/11/6
 */
class VoiceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_voice_input, container, false)
    }

}