package ir.scarpin.textdecoder

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call
import retrofit2.http.Headers

interface MyApi {

    @Multipart
    @Headers("Content-Type: multipart/form-data")
    @POST("ticket/repair/register")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("desc") desc: RequestBody,

    ): Call<UploadResponse>


    companion object {
        operator fun invoke(): MyApi {
            return Retrofit.Builder().baseUrl("https://test.scarpin.ir/api/v1/")
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(MyApi::class.java)
        }
    }

}