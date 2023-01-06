package ru.geekbrains.pngconverter.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.content.contentValuesOf
import androidx.core.graphics.drawable.toBitmap
import coil.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.geekbrains.pngconverter.App
import ru.geekbrains.pngconverter.databinding.FragmentPictureConverterBinding
import ru.geekbrains.pngconverter.model.PictureRequest
import ru.geekbrains.pngconverter.navigation.BackPressedListner
import ru.geekbrains.pngconverter.presenter.PictureConverterPresenter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class PictureConverterFragment : MvpAppCompatFragment(), PictureView, BackPressedListner {

    private val imageID = 1
    private var fileName = ""
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var launcher = registerForActivityResult(PictureRequest()) { uri ->
        fileName = uri.toString().split("/").last()
        binding.imageView.load(uri)
    }

    companion object {
        fun getInstance(): PictureConverterFragment {
            return PictureConverterFragment()
        }
    }

    private var _binding: FragmentPictureConverterBinding? = null
    private val binding: FragmentPictureConverterBinding
        get() {
            return _binding!!
        }

    private val presenter: PictureConverterPresenter by moxyPresenter {
        PictureConverterPresenter(App.instance.router)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPictureConverterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBtnGallery()
        initBtnConvert()
    }

    private fun initBtnGallery() {
        binding.btnGallery.setOnClickListener {
            launcher.launch(imageID)
        }
    }

    private fun initBtnConvert() {
        binding.btnConvert.setOnClickListener {
            coroutineScope.launch {
                val imageBitMap = binding.imageView.drawable.toBitmap()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveImageAfterQVersion(imageBitMap)
                } else saveImageBeforeQVersion(imageBitMap)
            }
        }
    }

    private fun saveImageBeforeQVersion(bitmap: Bitmap): Uri {
        val directory = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)
        val file = File(directory, "$fileName.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        MediaScannerConnection.scanFile(
            requireContext(),
            arrayOf(file.absolutePath),
            null,
            null
        )
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageAfterQVersion(bitmap: Bitmap): Uri {
        val name = "$fileName.png"
        var outputStream: OutputStream?
        var uri: Uri?
        val contentValues = contentValuesOf().apply {
            put(DISPLAY_NAME, name)
            put(MIME_TYPE, "image/png")
            put(RELATIVE_PATH, DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val contentResolver = requireActivity().contentResolver
        contentResolver.also { resolver ->
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = uri?.let {
                resolver.openOutputStream(it)
            }
        }
        outputStream?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(uri!!, contentValues, null, null)
        return uri!!
    }

    override fun onBackPressed() = presenter.onBackPressed()

    override fun init() {
        //TODO("Not yet implemented")
    }

    override fun update() {
        //TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}