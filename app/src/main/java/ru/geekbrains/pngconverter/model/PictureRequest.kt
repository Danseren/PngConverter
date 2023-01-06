package ru.geekbrains.pngconverter.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class PictureRequest : ActivityResultContract<Int, Uri?>() {

    override fun createIntent(context: Context, input: Int): Intent {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/png"
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK) {
            return try {
                val image = intent?.data
                image
            } catch (e: java.lang.RuntimeException) {
                e.printStackTrace()
                null
            }
        }
        return null
    }
}