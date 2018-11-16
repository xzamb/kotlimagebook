package com.example.lucaszambon.practicing

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var artNames: ArrayList<String>? = null
    private var artImages: ArrayList<Bitmap>? = null

    private var artsAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        artNames = ArrayList()
        artImages = ArrayList()

        artsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, artNames)
        artListView.adapter = artsAdapter

        try {

            setArtList(createArtListDatabaseCursor())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        artListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            val intent = Intent(applicationContext, Main2Activity::class.java)
            intent.putExtra("name", artNames!![position])
            intent.putExtra("info", "old")

            val chosen = Globals.Chosen
            chosen.chosenImage = artImages!![position]

            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater

        menuInflater.inflate(R.menu.add_art, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.ad_art) {
            val intent = Intent(applicationContext, Main2Activity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun createArtListDatabaseCursor(): Cursor {
        val database = openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null)
        database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, image BLOB)")

        val query = "SELECT * FROM arts"
        return database.rawQuery(query, null)
    }

    private fun setArtList(cursor: Cursor) {
        if (cursor.count <= 0) return

        val name = cursor.getColumnIndex("name")
        val image = cursor.getColumnIndex("image")

        cursor.moveToFirst()

        var index = 0
        while (index <= cursor.count) {

            artNames?.add(cursor.getString(name))

            //decode from byteArray before add it to an array
            val imageByteArray = cursor.getBlob(image)
            val imageToAdd = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)

            artImages?.add(imageToAdd)

            cursor.moveToNext()

            artsAdapter?.notifyDataSetChanged()

            index++
        }

        cursor.close()
    }
}
