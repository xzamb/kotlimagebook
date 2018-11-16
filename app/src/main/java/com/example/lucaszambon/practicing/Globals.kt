package com.example.lucaszambon.practicing

import android.graphics.Bitmap

class Globals{

    companion object Chosen{
        var chosenImage: Bitmap? = null

        fun returnChosenImage(): Bitmap{
            return chosenImage!!
        }
    }
}