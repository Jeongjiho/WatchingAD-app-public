package com.watchingad.watchingad.message

import java.time.LocalDateTime
import java.util.*

data class SuccessMessage<T>(
    var code: Int,
    var httpStatus: String,
    var dateTime: String,
    var message: String,
    var data: T
)