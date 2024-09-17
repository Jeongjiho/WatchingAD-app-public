package com.watchingad.watchingad.message

data class ErrorMessage(
    var message: String,
    var httpStatus: String,
    var httpStatusCode: Int,
    var dateTime: String
)