package com.hasanaydin.travelbook

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.hasanaydin.travelbook.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val placesArray = ArrayList<Place>()


    // OptionsMenu

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_place,menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_place_option){

            val intent = Intent(applicationContext,MapsActivity::class.java)
            startActivity(intent)

        }

        return super.onOptionsItemSelected(item)
    }

    // End OptionsMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        try {

            val database = openOrCreateDatabase("Places", Context.MODE_PRIVATE,null)
            val cursor = database.rawQuery("SELECT * FROM places",null)

            val addressIx = cursor.getColumnIndex("address")
            val latitudeIx = cursor.getColumnIndex("latitude")
            val longitudeIx = cursor.getColumnIndex("longitude")

            while (cursor.moveToNext()){

                val addressFromDatabase = cursor.getString(addressIx)
                val latitdeFromDatabase = cursor.getDouble(latitudeIx)
                val longitudeFromDatabase = cursor.getDouble(longitudeIx)

                val myPlace = Place(addressFromDatabase,latitdeFromDatabase,longitudeFromDatabase)

                placesArray.add(myPlace)

            }

            cursor.close()

        }catch (e:Exception){
            e.printStackTrace()
        }

        val customAdapter = CustomAdapter(placesArray,this)
        binding.listView.adapter = customAdapter

    }
}