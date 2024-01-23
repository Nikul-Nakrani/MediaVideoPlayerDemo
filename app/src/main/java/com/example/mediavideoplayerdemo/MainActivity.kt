package com.example.mediavideoplayerdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.mediavideoplayerdemo.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var videoUri: Uri? = null
    private val storagePermission = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        clickListener()

        //set Video Path for raw file and folder
        val vPathRaw = "android.resource://$packageName/raw/heartbeat1"
        //set Video Path for Internet
        val vPathInternet =
            "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1"


        // Initialize the AssetManager
        val assetManager = assets
        val fileName = "heartbeat1.mp4"

        //set the Asset Folder
        try {
            val inputStream: InputStream = assetManager.open(fileName)
            val tempVideoFile = File(cacheDir, "temp_video.mp4")

            // Copy the data from the InputStream to the temporary file
            val outputStream = FileOutputStream(tempVideoFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.close()
            inputStream.close()

            // Set the temporary file's path as the VideoView's data source
            binding.videoView.setVideoPath(tempVideoFile.path)

            // Start playing the video
            binding.videoView.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        /* videoView.setOnPreparedListener {
             videoView.start()
         }*/

        //Set VideoUri
        // videoUri = Uri.parse(vPathRaw)
        //videoUri = Uri.parse(vPathInternet)
        // videoUri = Uri.parse(vPathAsset)

        //setVideoPath On VideoView
        //videoView.setVideoPath(vPath)

        //setVideoUri
        //videoView.setVideoURI(videoUri)
        //Start video through This Method
        //videoView.start()

        //set play,stop  Button Through MediaController
        val mediasController = MediaController(this)
        binding.videoView.setMediaController(mediasController)
        mediasController.setAnchorView(binding.videoView)
    }


    private fun clickListener() {
        binding.btnSelectVideo.setOnClickListener {
            storagePermissions()
        }
    }

    private fun storagePermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                storagePermission
            )
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        startActivityForResult(intent, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the selected video's URI
            videoUri = data?.data
            binding.videoView.setVideoURI(videoUri)
        }
    }
}