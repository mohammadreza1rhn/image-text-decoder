package ir.scarpin.textdecoder

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.Menu

import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), UploadCallback {
    private lateinit var inputImageBtn: MaterialButton
    private lateinit var recognizeTextBtn: MaterialButton
    private lateinit var imageIv: ShapeableImageView
    private lateinit var recognizedTextEt: EditText

    private lateinit var uploadBtn:MaterialButton

    private companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101
    }

    private var imageUri: Uri? = null

    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>


    private lateinit var progressDialog: ProgressDialog

    private lateinit var textRecognizer: TextRecognizer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputImageBtn = findViewById(R.id.inputImageBtn)
        recognizeTextBtn = findViewById(R.id.recognizeTextBtn)
        imageIv = findViewById(R.id.imageIv)
        recognizedTextEt = findViewById(R.id.recognizedTextEt)
        uploadBtn=findViewById(R.id.btn_upload)

        cameraPermissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        inputImageBtn.setOnClickListener {
            showInputImageDialog()
        }

        recognizeTextBtn.setOnClickListener {
            if (imageUri == null) {
                showToast("pick image fisrt")
            } else {
                recognizeTextFromImage()
            }
        }

        uploadBtn.setOnClickListener{
            uploadImage()
        }

    }

    private fun uploadImage() {
        if(imageUri==null){
            showToast("img uri is null...")
            return
        }
        val parcelFileDescriptor=contentResolver.openFileDescriptor(imageUri!!,"r",null)?:return
        val inputStream=FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file= File(cacheDir,contentResolver.getFileName(imageUri!!))
        val outputStream=FileOutputStream(file)
        inputStream.copyTo(outputStream)
        val body=UploadRequestBody(file,"image",this)

        MyApi().uploadImage(MultipartBody.Part.createFormData(
            "image",
            file.name,
            body
        ), RequestBody.create(MediaType.parse("multipart/form-data"),"json")
        ).enqueue(object :Callback<UploadResponse>{
            override fun onResponse(
                call: Call<UploadResponse>?,
                response: Response<UploadResponse>?
            ) {
                TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<UploadResponse>?, t: Throwable?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun recognizeTextFromImage() {
        progressDialog.setMessage("preapring image ...")
        progressDialog.show()

        try {
            val inputImage = InputImage.fromFilePath(this, imageUri!!)
            progressDialog.setMessage("recognizing text ...")

            val textTaskResult = textRecognizer.process(inputImage)
                .addOnSuccessListener { text ->
                    progressDialog.dismiss()
                    val recognizedText = text.text
                    recognizedTextEt.setText(recognizedText)
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    showToast("failed to recognize ${e.message}")
                }
        } catch (e: Exception) {
            showToast("Faild to prepare image due to ${e.message}")
        }
    }

    private fun showInputImageDialog() {
        val popUpMenu = PopupMenu(this, inputImageBtn)
        popUpMenu.menu.add(Menu.NONE, 1, 2, "CAMERA")
        popUpMenu.menu.add(Menu.NONE, 1, 2, "GALLERY")

        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 1) {
                if (checkCameraPermission()) {
                    pickImageCamera()
                } else {
                    requestCameraPermission()
                }
            } else if (id == 2) {
                if (checkStoragePermission()) {
                    pickImageGallery()
                } else {
                    requestStoragePermission()
                }
            }
            return@setOnMenuItemClickListener true

        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data!!.data
                imageIv.setImageURI(imageUri)
            } else {
                showToast("canceled...")
            }

        }

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "sample")
        values.put(MediaStore.Images.Media.DESCRIPTION, "sample DESC")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageIv.setImageURI(imageUri)
            } else {
                showToast("canceled...")
            }

        }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermission(): Boolean {
        val cameraResult = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val storageResult = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return cameraResult && storageResult
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted) {
                        pickImageCamera()
                    } else {
                        showToast("camera and storage permission in required...")
                    }

                }
            }

            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (storageAccepted) {
                        pickImageGallery()
                    } else {
                        showToast("storage permission is required....")
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}



private fun ContentResolver.getFileName(imageUri: Uri): String {
    var name=""
    val returnCursor= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.query(imageUri,null,null,null)
    } else {
        TODO("VERSION.SDK_INT < O")
    }
    if(returnCursor!=null){
        val nameIndex=returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name=returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}
