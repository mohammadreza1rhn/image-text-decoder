package ir.scarpin.textdecoder

import org.json.JSONObject

data class ApiCallResponse(
    val Mode:String,
    val Code:Int,
    val Data:String,
    val Error:Boolean,
    val Message:String

)
