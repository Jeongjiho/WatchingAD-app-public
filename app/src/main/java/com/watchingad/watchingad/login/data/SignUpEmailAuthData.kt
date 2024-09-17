package com.watchingad.watchingad.login.data

import java.time.LocalDateTime
import java.util.*

data class SignUpEmailAuthData(
    var idx: Int,
    var authNum: Int,
    var email: String,
    var sessionId: String,
    var timeLimit: Date,
    var useAuthYn: String,
    var createDatetime: Date
)