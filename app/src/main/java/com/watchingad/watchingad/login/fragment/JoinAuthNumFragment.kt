package com.watchingad.watchingad.login.fragment

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.watchingad.watchingad.R
import com.watchingad.watchingad.base.APIClient
import com.watchingad.watchingad.login.api.LoginAPI
import com.watchingad.watchingad.login.data.SignUpEmailAuthData
import com.watchingad.watchingad.message.ErrorMessage
import com.watchingad.watchingad.message.SuccessMessage
import com.watchingad.watchingad.utils.AlertDialogUtil
import com.watchingad.watchingad.utils.CloseOnClickListenerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinAuthNumFragment : Fragment() {

    private lateinit var rootView : View
    private lateinit var memberContext : Context
    private lateinit var memberFragmentManager : FragmentManager
    private lateinit var sendTo : String
    private var isLimitTime = false

    private val callCheckAuthNumCallback = (object : Callback<SuccessMessage<SignUpEmailAuthData>> {
        override fun onResponse(call: Call<SuccessMessage<SignUpEmailAuthData>>, response: Response<SuccessMessage<SignUpEmailAuthData>>) {
            if(!response.isSuccessful) {
                val errorString = response.errorBody()?.string()
                val errorMessage = Gson().fromJson(errorString, ErrorMessage::class.java)
                if(errorMessage.message.isEmpty()) {
                    context?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="에러가 발생했습니다.", okCallback =null) }
                }
                else {
                    context?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message =errorMessage.message, okCallback =null) }
                }
            }
            else {
                val signUpEmailAuthData = response.body()?.data
                val authNumEdit = rootView.findViewById<View>(com.watchingad.watchingad.R.id.authNum) as EditText
                val authNum = authNumEdit.text.toString()

                if(signUpEmailAuthData?.idx == 0 || signUpEmailAuthData?.authNum != authNum.toInt()) {
                    context?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="인증번호가 틀렸습니다.", okCallback =null) }
                }
                else {
                    val bundle = Bundle()
                    bundle.putString("email", sendTo)

                    val joinFragment = JoinFragment()
                    joinFragment.arguments = bundle

                    memberFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(com.watchingad.watchingad.R.anim.enter_from_right, com.watchingad.watchingad.R.anim.exit_to_right, com.watchingad.watchingad.R.anim.enter_from_right, com.watchingad.watchingad.R.anim.exit_to_right)
                        .addToBackStack(null)
                        .replace(com.watchingad.watchingad.R.id.loginFrameLayout, joinFragment)
                        .commit()
                }

            }
        }
        override fun onFailure(call: Call<SuccessMessage<SignUpEmailAuthData>>, t: Throwable) {
            Log.d(ContentValues.TAG, "실패 : {$t}")
            call.cancel()
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_join_auth_num, container, false)
        memberContext = this.requireContext()
        memberFragmentManager = (activity as FragmentActivity).supportFragmentManager
        sendTo = arguments?.getString("email")!!

        val closeButton = rootView.findViewById<View>(com.watchingad.watchingad.R.id.closeButton) as ImageButton
        closeButton.setOnClickListener(CloseOnClickListenerUtil(memberFragmentManager, JoinAuthNumFragment(), "joinFragment"))

        val btnNextStep = rootView.findViewById<View>(com.watchingad.watchingad.R.id.btnNextStep) as Button

        btnNextStep.setOnClickListener {
            if(!isLimitTime) {
                val authNumEdit = rootView.findViewById<View>(com.watchingad.watchingad.R.id.authNum) as EditText
                val authNum = authNumEdit.text.toString()
                val api = APIClient.getClient(LoginAPI::class.java) as LoginAPI
                api.checkAuthNum(sendTo, authNum).enqueue(callCheckAuthNumCallback)
            }
            else {
                memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="인증 시간이 만료되었습니다.", okCallback =null) }
            }
        }

        val timerText = rootView.findViewById<View>(com.watchingad.watchingad.R.id.timerText) as TextView
        val countDownTimer = object : CountDownTimer(1000 * 60 * 5, 1000) {
            override fun onTick(p0: Long) {
                // countDownInterval 마다 호출 (여기선 1000ms)
                val m = (p0/(1000 * 60))
                val s = (p0 - (m * 1000 * 60))/1000
                var mText = ""
                var sText = ""

                if(m < 10)
                    mText = "0" + m.toString()
                else
                    mText = m.toString()

                if(s < 10)
                    sText = "0" + s.toString()
                else
                    sText = s.toString()


                timerText.text = mText + ":" + sText
            }
            override fun onFinish() {
                // 타이머가 종료되면 호출
                isLimitTime = true
            }
        }.start()

        return rootView
    }
}