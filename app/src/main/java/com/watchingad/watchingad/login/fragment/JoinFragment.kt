package com.watchingad.watchingad.login.fragment

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.watchingad.watchingad.R
import com.watchingad.watchingad.base.APIClient
import com.watchingad.watchingad.login.api.LoginAPI
import com.watchingad.watchingad.login.data.UserData
import com.watchingad.watchingad.message.ErrorMessage
import com.watchingad.watchingad.message.SuccessMessage
import com.watchingad.watchingad.utils.AlertDialogUtil
import com.watchingad.watchingad.utils.AppUtil.setFocusAndShowKeyboard
import com.watchingad.watchingad.utils.CloseOnClickListenerUtil
import com.watchingad.watchingad.utils.HttpStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class JoinFragment : Fragment() {

    private lateinit var rootView : View
    private lateinit var memberContext : Context
    private lateinit var memberFragmentManager : FragmentManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_join, container, false)
        memberContext = this.requireContext()
        memberFragmentManager = (activity as FragmentActivity).supportFragmentManager

        val closeButton = rootView.findViewById<View>(com.watchingad.watchingad.R.id.closeButton) as ImageButton
        closeButton.setOnClickListener(CloseOnClickListenerUtil(memberFragmentManager, JoinFragment(), "joinFragment"))

        val email = arguments?.getString("email")!!
        //val email = "claraster@nate.com"
        val inputId = rootView.findViewById<View>(com.watchingad.watchingad.R.id.inputId) as EditText
        inputId.setText(email)

        val btnNextStep = rootView.findViewById<View>(com.watchingad.watchingad.R.id.btnNextStep) as Button
        btnNextStep.setOnClickListener {
            if(validation()) {
                val api = APIClient.getClient(LoginAPI::class.java) as LoginAPI
                api.userJoin(getUserJoinParam()).enqueue(callJoinCallback)
            }
        }

        return rootView
    }

    private val callJoinCallback = (object : Callback<SuccessMessage<Object>> {
        override fun onResponse(call: Call<SuccessMessage<Object>>, response: Response<SuccessMessage<Object>>) {
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
                val successMessage = response.body()
                if(HttpStatus.OK.code == successMessage?.code) {
                    AlertDialogUtil.showInfoAlert(context = memberContext, title =null, message = "가입되었습니다.", okCallback = DialogInterface.OnClickListener { dialog, id ->
                        memberFragmentManager?.beginTransaction()?.remove(JoinFragment())?.commit()
                        memberFragmentManager?.popBackStack("joinFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    })
                }
                else {
                    AlertDialogUtil.showInfoAlert(context = memberContext, title =null, message = successMessage?.message!!, okCallback =null)
                }
            }
        }
        override fun onFailure(call: Call<SuccessMessage<Object>>, t: Throwable) {
            Log.d(ContentValues.TAG, "실패 : {$t}")
            call.cancel()
        }
    })

    fun getUserJoinParam(): UserData {

        val inputId = rootView.findViewById<View>(com.watchingad.watchingad.R.id.inputId) as EditText
        val inputPassword = rootView.findViewById<View>(com.watchingad.watchingad.R.id.inputPassword) as EditText
        val inputName = rootView.findViewById<View>(com.watchingad.watchingad.R.id.inputName) as EditText
        val inputPhone = rootView.findViewById<View>(com.watchingad.watchingad.R.id.inputPhone) as EditText

        return UserData(
            id = inputId.text.toString(),
            password = inputPassword.text.toString(),
            name = inputName.text.toString(),
            phone = inputPhone.text.toString()
        )

    }

    fun validation(): Boolean {

        val inputId = rootView.findViewById<View>(com.watchingad.watchingad.R.id.inputId) as EditText
        if(inputId.text.isEmpty()) {
            memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="아이디를 입력해주세요.", okCallback =null) }
            inputId.setFocusAndShowKeyboard(memberContext)
            return false
        }

        val inputPassword = rootView.findViewById<View>(com.watchingad.watchingad.R.id.inputPassword) as EditText
        if(inputPassword.text.isEmpty()) {
            memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="비밀번호를 입력해주세요.", okCallback =null) }
            inputPassword.setFocusAndShowKeyboard(memberContext)
            return false
        }

        if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{9,12}$", inputPassword.text)) {
            memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="비밀번호는 영문(대소문자 구분), 숫자, 특수문자 조합, 9~12자리를 입력해주세요.", okCallback =null) }
            inputPassword.setFocusAndShowKeyboard(memberContext)
            return false
        }

        val inputPasswordConfirm = rootView.findViewById<View>(com.watchingad.watchingad.R.id.inputPasswordConfirm) as EditText
        if(inputPasswordConfirm.text.isEmpty()) {
            memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="비밀번호 확인을 입력해주세요.", okCallback =null) }
            inputPasswordConfirm.setFocusAndShowKeyboard(memberContext)
            return false
        }

        if(inputPassword.text.toString() != inputPasswordConfirm.text.toString()) {
            memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="비밀번호와 비밀번호 확인이 다릅니다.", okCallback =null) }
            inputPasswordConfirm.setFocusAndShowKeyboard(memberContext)
            return false
        }

        val inputName = rootView.findViewById<View>(com.watchingad.watchingad.R.id.inputName) as EditText
        if(inputName.text.isEmpty()) {
            memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="이름을 입력해주세요.", okCallback =null) }
            inputName.setFocusAndShowKeyboard(memberContext)
            return false
        }

        val inputPhone = rootView.findViewById<View>(com.watchingad.watchingad.R.id.inputPhone) as EditText
        if(inputPhone.text.isEmpty()) {
            memberContext?.let { AlertDialogUtil.showInfoAlert(context = it, title =null, message ="핸드폰번호를 입력해주세요.", okCallback =null) }
            inputPhone.setFocusAndShowKeyboard(memberContext)
            return false
        }

        return true
    }
}