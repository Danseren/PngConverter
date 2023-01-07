package ru.geekbrains.pngconverter.view

import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface PictureView: MvpView {

    fun init()
    fun showImage(uri: Uri?)
    fun showProgress()
    fun showInfo(message: String)
    fun showError(message: String)
}