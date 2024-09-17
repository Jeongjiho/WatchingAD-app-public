package com.watchingad.watchingad.login.activity

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.watchingad.watchingad.R
import com.watchingad.watchingad.login.fragment.LoginFragment
import com.watchingad.watchingad.utils.AppUtil
import android.widget.EditText

import android.view.MotionEvent
import com.watchingad.watchingad.login.fragment.JoinAuthNumFragment
import com.watchingad.watchingad.login.fragment.JoinFragment


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(com.watchingad.watchingad.R.anim.enter_from_right, com.watchingad.watchingad.R.anim.exit_to_right, com.watchingad.watchingad.R.anim.enter_from_right, com.watchingad.watchingad.R.anim.exit_to_right)
            .add(R.id.loginFrameLayout, LoginFragment())
            //.add(R.id.loginFrameLayout, JoinFragment())
            .commit()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    AppUtil.hideKeyboard(getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager, v)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}