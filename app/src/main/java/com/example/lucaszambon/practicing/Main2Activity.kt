package com.example.lucaszambon.practicing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Switch
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.ByteArrayOutputStream

class Main2Activity : AppCompatActivity() {

    private var selectedImage: Bitmap? = null

    private var database: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val intent = intent
        val info = intent.getStringExtra("info")

        when (info) {
            "new" -> {
                val background = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.select_image)
                imageView.setImageBitmap(background)
                button.visibility = View.VISIBLE
                remove.visibility = View.INVISIBLE
                editText.text.clear()
            }
            else -> {
                val artName = intent.getStringExtra("name")
                editText.setText(artName)
                editText.isEnabled = false
                val chosenImage = Globals.Chosen
                val artImage = chosenImage.returnChosenImage()
                imageView.setImageBitmap(artImage)
                imageView.isEnabled = false
                button.visibility = View.INVISIBLE
            }
        }

    }

    fun selectImage(view: View) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
        } else {
            startMediaActivity()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMediaActivity()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val image = data.data

            try {
                selectedImage = MediaStore.Images.Media.getBitmap(contentResolver, image)
                imageView.setImageBitmap(selectedImage)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun saveArt(view: View) {

        val name = editText.text.toString()

        //By using an outputStream and compressing the image, it makes the image lighter to save into the database
        val outputStream = ByteArrayOutputStream()
        selectedImage?.compress(Bitmap.CompressFormat.PNG, 50, outputStream)

        val imageByteArray = outputStream.toByteArray()

        try {
            //Open or create database
            database = openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null)
            //create table and columns
            database?.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, image BLOB)")
            //inset values to it

            val query = "INSERT INTO arts (name, image) VALUES(?, ?)"
            val statement = database?.compileStatement(query)

            statement?.bindString(1, name)
            statement?.bindBlob(2, imageByteArray)
            statement?.execute()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

    fun removeArt(view: View) {

        try {
            database = openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null)

            val artName = editText.text.toString()
            val deleteQuery = "DELETE FROM arts WHERE name = ?"

            val statement = database?.compileStatement(deleteQuery)
            statement?.bindString(1, artName)
            statement?.execute()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }


    private fun startMediaActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }
}
