package com.watchingad.watchingad.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.watchingad.watchingad.message.SuccessMessage
import retrofit2.Callback

object AlertDialogUtil {

    public const val CONST_INFO_STR = "안내"
    public const val CONST_OK_STR = "확인"
    public const val CONST_ERROR_STR = "에러"

    fun showInfoAlert(context: Context, title:String?, message:String, okCallback: DialogInterface.OnClickListener?) {
        AlertDialog.Builder(context)
            .setTitle(title?:CONST_INFO_STR)
            .setMessage(message)
            .setPositiveButton(CONST_OK_STR, okCallback)
            .show()
    }

}