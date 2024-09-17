package com.watchingad.watchingad.login.fragment

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.watchingad.watchingad.R
import com.watchingad.watchingad.base.APIClient
import com.watchingad.watchingad.login.api.LoginAPI
import com.watchingad.watchingad.message.ErrorMessage
import com.watchingad.watchingad.message.SuccessMessage
import com.watchingad.watchingad.utils.AlertDialogUtil
import com.watchingad.watchingad.utils.CloseOnClickListenerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class JoinAuthFragment : Fragment() {

    private lateinit var rootView : View
    private lateinit var memberContext : Context
    private lateinit var memberFragmentManager : FragmentManager

    private val callSendEmailCallback = (object : Callback<SuccessMessage<Object>> {
        override fun onResponse(call: Call<SuccessMessage<Object>>, response: Response<SuccessMessage<Object>>) {
            if(!response.isSuccessful) {
                val errorString = response.errorBody()?.string()
                val errorMessage = Gson().fromJson(errorString, ErrorMessage::class.java)
                if(errorMessage.message.isEmpty()) {
                    context?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="이메일 발송에 실패했습니다.", okCallback =null) }
                }
                else {
                    context?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message =errorMessage.message, okCallback =null) }
                }
                memberFragmentManager.popBackStack()
            }
        }
        override fun onFailure(call: Call<SuccessMessage<Object>>, t: Throwable) {
            Log.d(ContentValues.TAG, "실패 : {$t}")
            call.cancel()
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(R.layout.fragment_join_auth, container, false)
        memberContext = this.requireContext()
        memberFragmentManager = (activity as FragmentActivity).supportFragmentManager

        val closeButton = rootView.findViewById<View>(com.watchingad.watchingad.R.id.closeButton) as ImageButton
        closeButton.setOnClickListener(CloseOnClickListenerUtil(memberFragmentManager, JoinAuthFragment(), "joinFragment"))

        val btnNextStep = rootView.findViewById<View>(com.watchingad.watchingad.R.id.btnNextStep) as Button

        btnNextStep.setOnClickListener {

            val sendToEdit = rootView.findViewById<View>(com.watchingad.watchingad.R.id.authEmail) as EditText
            val sendTo = sendToEdit.text.toString()

            if(validationEmail(sendTo)) {
                val api = APIClient.getClient(LoginAPI::class.java) as LoginAPI
                api.sendAuthMail(sendTo).enqueue(callSendEmailCallback)

                val bundle = Bundle()
                bundle.putString("email", sendTo)

                val joinAuthNumFragment = JoinAuthNumFragment()
                joinAuthNumFragment.arguments = bundle
                memberFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(com.watchingad.watchingad.R.anim.enter_from_right, com.watchingad.watchingad.R.anim.exit_to_right, com.watchingad.watchingad.R.anim.enter_from_right, com.watchingad.watchingad.R.anim.exit_to_right)
                    .addToBackStack(null)
                    .replace(com.watchingad.watchingad.R.id.loginFrameLayout, joinAuthNumFragment)
                    .commit()
            }

        }

        return rootView
    }

    private fun validationEmail(sendTo: String): Boolean {

        if(sendTo.trim().isEmpty()) {
            context?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="이메일을 입력해주세요.", okCallback =null) }
            return false
        }

        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        if(!pattern.matcher(sendTo).matches()){
            context?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="정확한 이메일 형식을 입력해주세요.", okCallback =null) }
            return false
        }

        return true

    }

}