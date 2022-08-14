package br.com.vieirateam.tcc.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object UriUtil {

    fun getUri(imageView: ImageView): Uri? {

        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        return try {
            val file = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "image_" + System.currentTimeMillis() + ".jpg")
            file.parentFile.mkdirs()
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            Uri.fromFile(file)
        } catch (ex: IOException) {
            Log.d("tcc", ex.toString())
            null
        }
    }

    fun getUri(context: Context, bitmap: Bitmap): Uri? {

        return try {
            val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "", null)
            Uri.parse(path)
        } catch (ex: NullPointerException) {
            Log.d("tcc", ex.toString())
            null
        }
    }
}