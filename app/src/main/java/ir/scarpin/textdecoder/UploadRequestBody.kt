//package ir.scarpin.textdecoder
//
//import android.os.Handler
//import android.os.Looper
//import okhttp3.MediaType
//import okhttp3.RequestBody
//import okio.Buffer
//import okio.BufferedSink
//import java.io.File
//import java.io.FileInputStream
//
//class UploadRequestBody(
//    private val file: File,
//    private val contentType: String,
//    private val api_token:String,
//    private val repairID:String,
//    private val subject:String,
//    private val title:String,
//    private val message:String,
//
//    private val callback: UploadCallback,
//):RequestBody() {
//    override fun contentType()= MediaType.parse("$contentType/*")
//    override fun contentLength()= file.length()
//    override fun writeTo(sink: BufferedSink?) {
//        val length=file.length()
//        val buffer=ByteArray(DEFAULT_BUFFER_SIZE)
//        val fileInputStream=FileInputStream(file)
//        var uploaded=0L
//        fileInputStream.use {
//            inputStream ->
//            var read :Int
//            val handler= Handler(Looper.getMainLooper())
//
////            while (inputStream.read(buffer).also {
////                read=it
////                }!=-1)
////                uploaded+=read
////            sink?.write(buffer,0,read)
//            do {
//                var read :Int=inputStream.read()
//                val handler= Handler(Looper.getMainLooper())
//
//                if (read == -1) {
//                    break
//                }
//
//                uploaded += read.toLong()
//                sink?.write(buffer, 0, read)
//
//            } while (true)
//        }
//    }
//    companion object{
//        private const val DEFAULT_BUFFER_SIZE=100000
//    }
//}
//
//interface UploadCallback {
//
//}
