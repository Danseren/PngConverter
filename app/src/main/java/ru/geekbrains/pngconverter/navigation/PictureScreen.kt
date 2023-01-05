package ru.geekbrains.pngconverter.navigation

import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.geekbrains.pngconverter.view.PictureConverterFragment

class PictureScreen : IScreens {
    override fun picture(): Screen = FragmentScreen() {
        PictureConverterFragment.getInstance()
    }
}