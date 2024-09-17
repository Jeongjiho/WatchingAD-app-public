package com.watchingad.watchingad.utils

import android.app.Application
import android.content.Context
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.watchingad.watchingad.base.WatchADApplication
import com.watchingad.watchingad.login.fragment.JoinAgreeFragment

object AppUtil {

    /**
     * 뷰에 스크롤을 셋팅해줍니다.
     */
    fun setScroll(rootView:View, id:Int) {
        val view = rootView.findViewById<View>(id)
        if(view is TextView) {
            view.movementMethod = ScrollingMovementMethod()
        }
    }

    /**
     * 키보드 숨기는 공통 함수
     * TODO 추후 활성화 되어있는 Activity를 찾아서 키보드를 내릴 수 있는 개선 방법을 찾아야함.
     */
    fun hideKeyboard(imm: InputMethodManager, v: View) {
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }

    /**
     * EditText에 포커싱 및 키보드를 올려줍니다.
     */
    fun EditText.setFocusAndShowKeyboard(context: Context) {
        this.requestFocus()
        setSelection(this.text.length)
        this.postDelayed({
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        }, 100)
    }

    /**
     * SSAID(Android ID)를 가져옵니다.
     */
    fun getSSAID(): String {
        return Settings.Secure.getString(WatchADApplication.ApplicationContext().contentResolver, Settings.Secure.ANDROID_ID)
    }

    /**
     * Os Version을 가져옵니다.
     */
    fun getOsVersion(): String {
        return WatchADApplication.ApplicationContext().applicationContext.packageManager.getPackageInfo(WatchADApplication.ApplicationContext().packageName, 0).versionName
    }

}

/**
 * 닫기 버튼 클릭 시 이벤트 공통 유틸클래스
 * @author jeongjiho
 * @since 2021-09-26
 */
class CloseOnClickListenerUtil(fragmentManager: FragmentManager, fragment: Fragment, name: String) : View.OnClickListener {
    private val fragmentManager: FragmentManager = fragmentManager
    private val fragment = fragment
    private val name = name

    override fun onClick(v: View?) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(fragment).commit()
        fragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}
