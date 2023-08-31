//package ir.scarpin.textdecoder
//
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object ApiCallService {
//    private val BASE_URL="https://test.scarpin.ir/api/v1/"
//    val okHttp2Client=OkHttpClient.Builder()
//    init{
//        val logging=HttpLoggingInterceptor()
//        logging.level=HttpLoggingInterceptor.Level.BODY
//        okHttp2Client.addInterceptor(logging)
//    }
//    private val api=Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(okHttp2Client.build())
//        .build()
//        .create(ApiCall::class.java)
//
//    fun call()=
//        api.uploadImage(SendParams("2222222222","pQgFAGM7YWeX\$D79aV26S6WqzXM6CDa%6Zj!@BKv!Mn8W3?Wgr","kotlin","kotlin","message"))
//}
