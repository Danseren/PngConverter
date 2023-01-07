package ru.geekbrains.pngconverter.presenter

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import ru.geekbrains.pngconverter.App
import ru.geekbrains.pngconverter.utils.COMPRESS_QUALITY
import ru.geekbrains.pngconverter.utils.ERROR_MESSAGE
import ru.geekbrains.pngconverter.utils.FILE_NAME
import ru.geekbrains.pngconverter.utils.SUCCESS_MESSAGE
import ru.geekbrains.pngconverter.view.PictureView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class PictureConverterPresenter(
    private val router: Router
) : MvpPresenter<PictureView>() {

    private lateinit var disposableImageFromGallery: Disposable
    private lateinit var disposableConvert: Disposable
    private var fileName = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun showImageFromGallery(uri: Uri?, name: String) {
        disposableImageFromGallery = Completable.create { emitter ->
            try {
                fileName = name
                emitter.onComplete()
            } catch (e: IOException) {
                emitter.onError(Throwable(ERROR_MESSAGE))
            }
        }.subscribeOn(Schedulers.io()).subscribe(
            {
                viewState.showImage(uri)
            },
            {
                viewState.showError(ERROR_MESSAGE)
            }
        )
    }

    fun convertImage(bitmap: Bitmap, contentResolver: ContentResolver) {
        viewState.showProgress()
        disposableConvert = Completable.create { emitter ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val res = convertImageAfterQVersion(bitmap, contentResolver)
                if (res) emitter.onComplete()
                else emitter.onError(Throwable(ERROR_MESSAGE))
            } else {
                val res = convertImageBeforeQVersion(bitmap)
                if (res) emitter.onComplete()
                else emitter.onError(Throwable(ERROR_MESSAGE))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    viewState.showInfo(SUCCESS_MESSAGE)
                },
                {
                    viewState.showError(ERROR_MESSAGE)
                }
            )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun convertImageAfterQVersion(
        bitmap: Bitmap,
        contentResolver: ContentResolver
    ): Boolean {
        val name = "$fileName.png"
        val outputStream: OutputStream?
        var uri: Uri?

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }

            contentResolver.also { resolver ->
                uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                outputStream = uri?.let {
                    resolver.openOutputStream(it)
                }
            }

            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, it)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
            contentResolver.update(uri!!, contentValues, null, null)
            return true

        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return false
        } catch (e: NullPointerException) {
            e.printStackTrace()
            return false
        }
    }

    private fun convertImageBeforeQVersion(bitmap: Bitmap): Boolean {
        try {
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(directory, FILE_NAME)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, outputStream)
            outputStream.flush()
            outputStream.close()
            MediaScannerConnection.scanFile(
                App.instance,
                arrayOf(file.absolutePath),
                null,
                null
            )
            val uri = FileProvider.getUriForFile(
                App.instance,
                "${App.instance.packageName}.provider",
                file
            )
            return uri != null
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return false
        } catch (e: NullPointerException) {
            e.printStackTrace()
            return false
        }
    }

    fun onBackPressed(): Boolean {
        router.exit()
        return true
    }
}