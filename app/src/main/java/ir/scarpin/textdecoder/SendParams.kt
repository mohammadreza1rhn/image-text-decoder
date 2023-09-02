package ir.scarpin.textdecoder

import java.io.File

data class SendParams(
    val repairID:String,
    val api_token:String,
    val subject:String,
    val title:String,
    val message:String,
)
