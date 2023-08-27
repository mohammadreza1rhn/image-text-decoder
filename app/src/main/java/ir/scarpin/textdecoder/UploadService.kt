package ir.scarpin.textdecoder

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadService {
    @Multipart
    @POST("ticket/repair/register")
    suspend fun uploadImage(
        @Part image:MultipartBody.Part
    ):ResponseBody
}