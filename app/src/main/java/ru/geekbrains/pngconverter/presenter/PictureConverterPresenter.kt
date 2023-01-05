package ru.geekbrains.pngconverter.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.geekbrains.pngconverter.view.PictureView

class PictureConverterPresenter(
    private val router: Router
) : MvpPresenter<PictureView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun onBackPressed(): Boolean {
        router.exit()
        return true
    }
}