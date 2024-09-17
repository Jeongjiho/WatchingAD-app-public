package com.watchingad.watchingad.login.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.watchingad.watchingad.R
import com.watchingad.watchingad.base.APIClient
import com.watchingad.watchingad.login.api.LoginAPI
import com.watchingad.watchingad.login.data.SignUpConfigData
import com.watchingad.watchingad.message.ErrorMessage
import com.watchingad.watchingad.message.SuccessMessage
import com.watchingad.watchingad.utils.AlertDialogUtil
import com.watchingad.watchingad.utils.AppUtil
import com.watchingad.watchingad.utils.CloseOnClickListenerUtil

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinAgreeFragment : Fragment() {

    private lateinit var rootView : View
    private lateinit var memberContext : Context

    private val getSignUpConfigCallback = (object : Callback<SuccessMessage<SignUpConfigData>> {
        override fun onResponse(call: Call<SuccessMessage<SignUpConfigData>>, response: Response<SuccessMessage<SignUpConfigData>>) {

            if(response.isSuccessful) {
                val signUpConfigData = response.body()?.data

                val agreeContents1 = rootView.findViewById<View>(com.watchingad.watchingad.R.id.agreeContents1) as TextView
                agreeContents1.text = signUpConfigData?.agreeContent1

                val agreeContents2 = rootView.findViewById<View>(com.watchingad.watchingad.R.id.agreeContents2) as TextView
                agreeContents2.text = signUpConfigData?.agreeContent2
            }
            else {
                //Log.d(TAG, "에러로그 : {${response.raw()}}")
                val errorString = response.errorBody()?.string()
                val errorMessage = Gson().fromJson(errorString, ErrorMessage::class.java)
                when (response.code()) {
                    404 -> memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =AlertDialogUtil.CONST_ERROR_STR, message ="서버에 접속 중 에러가 발생했습니다.", okCallback =null) }
                    500 -> memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =AlertDialogUtil.CONST_ERROR_STR, message =errorMessage.message, okCallback =null) }
                }
            }

        }
        override fun onFailure(call: Call<SuccessMessage<SignUpConfigData>>, t: Throwable) {
            Log.d(TAG, "실패 : {$t}")
            call.cancel()
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_join_agree, container, false)
        memberContext = this.requireContext()

        AppUtil.setScroll(rootView, com.watchingad.watchingad.R.id.agreeContents1)
        AppUtil.setScroll(rootView, com.watchingad.watchingad.R.id.agreeContents2)

        val closeButton = rootView.findViewById<View>(com.watchingad.watchingad.R.id.closeButton) as ImageButton
        closeButton.setOnClickListener(CloseOnClickListenerUtil((activity as FragmentActivity).supportFragmentManager, JoinAgreeFragment(), "joinFragment"))

        val api = APIClient.getClient(LoginAPI::class.java) as LoginAPI
        api.getLatestConfig().enqueue(getSignUpConfigCallback)

        val btnNextStep = rootView.findViewById<View>(com.watchingad.watchingad.R.id.btnNextStep) as Button

        btnNextStep.setOnClickListener {
            if(validationNextStep()) {
                val fragmentManager = (activity as FragmentActivity).supportFragmentManager
                fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(com.watchingad.watchingad.R.anim.enter_from_right, com.watchingad.watchingad.R.anim.exit_to_right, com.watchingad.watchingad.R.anim.enter_from_right, com.watchingad.watchingad.R.anim.exit_to_right)
                    .addToBackStack(null)
                    .replace(com.watchingad.watchingad.R.id.loginFrameLayout, JoinAuthFragment())
                    .commit()
            }
        }

        return rootView
    }

    private fun validationNextStep(): Boolean {

        val agreeCheck1 = rootView.findViewById<View>(com.watchingad.watchingad.R.id.agreeCheck1) as CheckBox
        if(!agreeCheck1.isChecked) {
            memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="개인정보 수집 및 이용에 대한 안내 동의를 체크해주세요.", okCallback =null) }
            return false
        }

        val agreeCheck2 = rootView.findViewById<View>(com.watchingad.watchingad.R.id.agreeCheck2) as CheckBox
        if(!agreeCheck2.isChecked) {
            memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="개인정보 수집 및 이용에 대한 안내 동의를 체크해주세요.", okCallback =null) }
            return false
        }

        return true

    }

}