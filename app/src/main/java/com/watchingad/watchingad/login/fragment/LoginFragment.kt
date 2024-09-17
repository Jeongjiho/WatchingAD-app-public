package com.watchingad.watchingad.login.fragment

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import android.widget.Button
import android.widget.EditText
import com.google.gson.Gson
import com.watchingad.watchingad.base.APIClient
import com.watchingad.watchingad.base.WatchADApplication
import com.watchingad.watchingad.login.api.LoginAPI
import com.watchingad.watchingad.main.activity.MainActivity
import com.watchingad.watchingad.message.ErrorMessage
import com.watchingad.watchingad.message.SuccessMessage
import com.watchingad.watchingad.utils.AlertDialogUtil
import com.watchingad.watchingad.utils.AppUtil.setFocusAndShowKeyboard
import com.watchingad.watchingad.utils.HttpStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    private lateinit var memberContext : Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        memberContext = this.requireContext()
        val rootView = inflater.inflate(com.watchingad.watchingad.R.layout.fragment_login, container, false)

        // 회원가입 버튼 클릭
        val btnJoin = rootView.findViewById<View>(com.watchingad.watchingad.R.id.btnJoin) as Button
        btnJoin.setOnClickListener {
            val fragmentManager = (activity as FragmentActivity).supportFragmentManager
            fragmentManager
                .beginTransaction()
                .setCustomAnimations(com.watchingad.watchingad.R.anim.enter_from_right, com.watchingad.watchingad.R.anim.exit_to_right, com.watchingad.watchingad.R.anim.enter_from_right, com.watchingad.watchingad.R.anim.exit_to_right)
                .addToBackStack("joinFragment")
                .add(com.watchingad.watchingad.R.id.loginFrameLayout, JoinAgreeFragment())
                .commit()
        }

        // 로그인 버튼 클릭
        val btnLogin = rootView.findViewById<View>(com.watchingad.watchingad.R.id.btnLogin) as Button
        btnLogin.setOnClickListener {
            if(loginValidation(rootView)) {
                val api = APIClient.getClient(LoginAPI::class.java) as LoginAPI
                val emailEditText = rootView.findViewById<View>(com.watchingad.watchingad.R.id.email) as EditText
                val passwordEditText = rootView.findViewById<View>(com.watchingad.watchingad.R.id.password) as EditText

                api.login(id = emailEditText.text.toString(), password = passwordEditText.text.toString()).enqueue(loginCallback)
            }
        }

        return rootView
    }

    // 로그인 유효성 검사
    fun loginValidation(rootView: View): Boolean {

        val emailEditText = rootView.findViewById<View>(com.watchingad.watchingad.R.id.email) as EditText
        if(emailEditText.text.isEmpty()) {
            AlertDialogUtil.showInfoAlert(context = WatchADApplication.ApplicationContext(), title =null, message ="이메일을 입력해주세요.", okCallback =null)
            emailEditText.setFocusAndShowKeyboard(WatchADApplication.ApplicationContext())
            return false
        }

        val passwordEditText = rootView.findViewById<View>(com.watchingad.watchingad.R.id.password) as EditText
        if(passwordEditText.text.isEmpty()) {
            AlertDialogUtil.showInfoAlert(context = WatchADApplication.ApplicationContext(), title =null, message ="비밀번호를 입력해주세요.", okCallback =null)
            passwordEditText.setFocusAndShowKeyboard(WatchADApplication.ApplicationContext())
            return false
        }

        return true

    }

    // Login Call Back
    private val loginCallback = (object : Callback<SuccessMessage<Object>> {
        override fun onResponse(call: Call<SuccessMessage<Object>>, response: Response<SuccessMessage<Object>>) {
            if(!response.isSuccessful) {
                val errorString = response.errorBody()?.string()
                val errorMessage = Gson().fromJson(errorString, ErrorMessage::class.java)
                if(errorMessage.message.isEmpty()) {
                    AlertDialogUtil.showInfoAlert(context = memberContext, title =null, message ="에러가 발생했습니다.", okCallback =null)
                }
                else {
                    AlertDialogUtil.showInfoAlert(context = memberContext, title =null, message =errorMessage.message, okCallback =null)
                }
            }
            else {
                val successMessage = response.body()
                if(HttpStatus.OK.code == successMessage?.code) {
                    val nextActivity = Intent(memberContext, MainActivity::class.java)
                    startActivity(nextActivity)
                }
                else {
                    AlertDialogUtil.showInfoAlert(context = WatchADApplication.ApplicationContext(), title =null, message = successMessage?.message!!, okCallback =null)
                }
            }
        }
        override fun onFailure(call: Call<SuccessMessage<Object>>, t: Throwable) {
            Log.d(ContentValues.TAG, "실패 : {$t}")
            call.cancel()
        }
    })

}