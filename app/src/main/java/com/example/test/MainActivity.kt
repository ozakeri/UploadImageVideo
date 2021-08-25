package com.example.test

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MainActivity : AppCompatActivity() {


    private val progressDialog: ProgressDialog? = null
    private val mediaColumns = arrayOf(MediaStore.Video.Media._ID)
    private var mediaPath: String = ""
    private var mediaPath1: String = ""
    private var imgView: ImageView? = null
    private var str1: TextView? = null
    private var str2: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnUpload = findViewById<Button>(R.id.upload)
        val btnPickVideo = findViewById<Button>(R.id.pick_vdo)
        imgView = findViewById(R.id.preview)
        str1 = findViewById(R.id.filename1)
        str2 = findViewById(R.id.filename2)

        progressDialog?.setMessage("Uploading...")

        btnUpload.setOnClickListener {
            uploadFile();
        }

        findViewById<Button>(R.id.uploadMultiple).setOnClickListener {
            uploadMultipleFiles();
        }

        findViewById<Button>(R.id.pick_img).setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, 0)
        }

        btnPickVideo.setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, 0)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
                val selectedImage: Uri = data.getData()!!
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? =
                    getContentResolver().query(selectedImage, filePathColumn, null, null, null)!!
                cursor?.moveToFirst()
                val columnIndex: Int? = cursor?.getColumnIndex(filePathColumn[0])
                mediaPath = cursor?.getString(columnIndex!!).toString()
                str1?.setText(mediaPath)
                imgView?.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                cursor?.close();
            } else if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
                val selectedVideo: Uri = data.getData()!!
                val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
                val cursor: Cursor? =
                    getContentResolver().query(selectedVideo, filePathColumn, null, null, null)!!
                cursor?.moveToFirst()
                val columnIndex: Int? = cursor?.getColumnIndex(filePathColumn[0])
                mediaPath1 = cursor?.getString(columnIndex!!).toString()
                str2?.setText(mediaPath1)
                imgView?.setImageBitmap(getThumbnailPathForLocalFile(this, selectedVideo));
                cursor?.close();
            } else {
                Toast.makeText(this, "You haven't picked Image/Video", Toast.LENGTH_LONG).show()

            }
        } catch (e: Exception) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        }


    }

    fun getThumbnailPathForLocalFile(context: Activity, fileUri: Uri): Bitmap {
        val fileId: Long = getFileId(context, fileUri);
        return MediaStore.Video.Thumbnails.getThumbnail(
            context.getContentResolver(),
            fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null
        );
    }

    private fun getFileId(context: Activity, fileUri: Uri): Long {
        val cursor: Cursor = context.managedQuery(fileUri, mediaColumns, null, null, null);
        if (cursor.moveToFirst()) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            return cursor.getInt(columnIndex).toLong();
        }
        return 0
    }


    private fun uploadMultipleFiles() {
        progressDialog?.show();
        val file = File(mediaPath)
        val file1 = File(mediaPath1)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val requestBody1: RequestBody = RequestBody.create(MediaType.parse("*/*"), file1)
        val fileToUpload: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.getName(), requestBody)
        val fileToUpload1: MultipartBody.Part =
            MultipartBody.Part.createFormData("file1", file1.getName(), requestBody1)
        val getResponse = AppConfig().getRetrofit().create(ApiConfig::class.java)
        val call = getResponse.uploadMulFile(fileToUpload, fileToUpload1)
        call.enqueue(object : Callback<ServerResponse> {
            override fun onResponse(
                call: Call<ServerResponse>?,
                response: Response<ServerResponse>?
            ) {
                val serverResponse = response?.body()
                if (serverResponse != null) {
                    if (serverResponse.success) {
                        Toast.makeText(
                            getApplicationContext(),
                            serverResponse.message,
                            Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            serverResponse.message,
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                } else {
                    assert(false)
                    Log.v("Response", serverResponse.toString())
                }
                progressDialog?.dismiss();
            }

            override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun uploadFile() {
        progressDialog?.show();

        val file = File(mediaPath)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val fileToUpload: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.getName(), requestBody)
        val filename: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), file.getName());

        val getResponse: ApiConfig = AppConfig().getRetrofit().create(ApiConfig::class.java)
        val call = getResponse.uploadFile(fileToUpload, filename)
        call.enqueue(object : Callback<ServerResponse> {
            override fun onResponse(
                call: Call<ServerResponse>?,
                response: Response<ServerResponse>?
            ) {
                val serverResponse = response?.body()
                if (serverResponse != null) {
                    if (serverResponse.success) {
                        Toast.makeText(
                            getApplicationContext(),
                            serverResponse.message,
                            Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            serverResponse.message,
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                } else {
                    assert(false)
                    Log.v("Response", serverResponse.toString())
                }
                progressDialog?.dismiss();
            }

            override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {
                TODO("Not yet implemented")
            }

        })

    }

}