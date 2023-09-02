package ir.scarpin.textdecoder

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiCall {

    //    @FormUrlEncoded
    @Multipart
    @Headers("Content-Type: application/json")
    @POST("ticket/repair/register")
    fun uploadImage(
        @Part("repairID") repairID: RequestBody,
        @Part("api_token") api_token: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("title") title: RequestBody,
        @Part("message") message: RequestBody,
        @Part file: MultipartBody.Part,
    ): Call<ApiCallResponse>
}