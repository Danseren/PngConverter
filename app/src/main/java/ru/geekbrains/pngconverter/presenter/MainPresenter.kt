package ru.geekbrains.pngconverter.presenter

import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.geekbrains.pngconverter.navigation.PictureScreen
import ru.geekbrains.pngconverter.view.MainView

class MainPresenter(
    private val router: Router
) : MvpPresenter<MainView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        router.replaceScreen(PictureScreen().picture())
    }

    fun onBackPressed() {
        router.exit()
    }
}