package ru.geekbrains.pngconverter.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import coil.load
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.geekbrains.pngconverter.App
import ru.geekbrains.pngconverter.databinding.FragmentPictureConverterBinding
import ru.geekbrains.pngconverter.model.PictureRequest
import ru.geekbrains.pngconverter.navigation.BackPressedListner
import ru.geekbrains.pngconverter.presenter.PictureConverterPresenter

class PictureConverterFragment : MvpAppCompatFragment(), PictureView, BackPressedListner {

    private val imageID = 1
    private var launcher = registerForActivityResult(PictureRequest()) { uri ->

        val fileName = uri.toString().split("/").last()
        presenter.showImageFromGallery(uri, fileName)
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
            presenter.convertImage(
                binding.imageView.drawable.toBitmap(),
                requireActivity().contentResolver
            )
        }
    }

    override fun onBackPressed() = presenter.onBackPressed()

    override fun init() {
        //TODO("Not yet implemented")
    }

    override fun showImage(uri: Uri?) {
        if (uri != null) binding.imageView.load(uri)
        else showError("Error")
    }

    override fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun showInfo(message: String) {
        binding.progressBar.let {
            if (it.visibility != View.GONE) it.visibility = View.GONE
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun showError(message: String) {
        binding.progressBar.let {
            if (it.visibility != View.GONE) it.visibility = View.GONE
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}