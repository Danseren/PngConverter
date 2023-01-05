package ru.geekbrains.pngconverter.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.geekbrains.pngconverter.App
import ru.geekbrains.pngconverter.databinding.FragmentPictureConverterBinding
import ru.geekbrains.pngconverter.navigation.BackPressedListner
import ru.geekbrains.pngconverter.presenter.PictureConverterPresenter

class PictureConverterFragment : MvpAppCompatFragment(), PictureView, BackPressedListner {

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