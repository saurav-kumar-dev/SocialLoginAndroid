package com.codingwithsaurav

import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.codingwithsaurav.sociallogin.R
import java.io.File


class ImagePickerActivity : AppCompatActivity() {

    private var biding: ImagePickerActivityBinding? = null
    private var providerFile: Uri? = null
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) openCamera()
    }
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        providerFile?.let {
            biding?.setSelectedImage?.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biding = DataBindingUtil.setContentView(this, R.layout.activity_image_picker)

        biding?.selectImageButton?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }

    }

    private fun openCamera() {
        //val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //val filePhoto1 = File(filesDir, "photo")
        val filePhoto = getPhotoFile("codingwithsaurav")
        providerFile = FileProvider.getUriForFile(this, "com.example.androidcamera.fileprovider", filePhoto)
        takePictureLauncher.launch(providerFile)
    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }

}