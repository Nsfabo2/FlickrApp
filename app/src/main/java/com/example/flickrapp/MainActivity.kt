package com.example.flickrapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

/*
Create a Flickr application that does the following:
- Makes us of Flickr API to fetch photos based on a search
- Makes use of Glide to display photos
- Displays photo thumbnails in a Recycler View along with their title
- Allows users to view large versions of the thumbnails by tapping on them

Bonus:
- Give users the option to select how many images are retrieved
- Display all images in a grid
- Allow users to use multiple tags when searching for photos (keep in mind that tags need to be separated by commas)
- Create another Activity that holds saved galleries
- Think of some additional features that would make the app better
 */

class MainActivity : AppCompatActivity() {

     lateinit var images: ArrayList<Images>
     lateinit var RV: RecyclerView
     lateinit var RVAdapter: RecyclerViewAdapter
     lateinit var LLO: LinearLayout
     lateinit var ET: EditText
     lateinit var Btn: Button
     lateinit var ImgView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        images = arrayListOf()
        RV = findViewById(R.id.RV)
        LLO = findViewById(R.id.LLO)
        ET = findViewById(R.id.ET)
        Btn = findViewById(R.id.Btn)
        ImgView = findViewById(R.id.ImgView)
        RVAdapter = RecyclerViewAdapter(this, images)
        RV.adapter = RVAdapter
        RV.layoutManager = LinearLayoutManager(this)
        Btn.setOnClickListener {
            if(ET.text.isNotEmpty()){
                GetAPI()
            }else{
                Toast.makeText(this, "The field is empty!", Toast.LENGTH_LONG).show()
            }
        }

        ImgView.setOnClickListener {
            CLOSE() }
    }//end oncreate

    private fun GetAPI(){
        CoroutineScope(IO).launch {
            val data = async { GetPhotos() }.await()
            if(data.isNotEmpty()){
                println(data)
                ShowPhotos(data)
            }else{
                Toast.makeText(this@MainActivity, "No Images Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun GetPhotos(): String{
        var response = ""
        try{
            response = URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&per_page=10&api_key=cb0cbca5c50568f7e3189b08d8e6a89b&tags=${ET.text}&format=json&nojsoncallback=1")
                .readText(Charsets.UTF_8)
        }catch(e: Exception){
            println("Error: $e")
        }
        return response
    }

    private suspend fun ShowPhotos(data: String){
        withContext(Main){
            val jsonObj = JSONObject(data)
            val photos = jsonObj.getJSONObject("photos").getJSONArray("photo")
            println("photos")
            println(photos.getJSONObject(0))
            println(photos.getJSONObject(0).getString("farm"))
            for(i in 0 until photos.length()){
                val title = photos.getJSONObject(i).getString("title")
                val farmID = photos.getJSONObject(i).getString("farm")
                val serverID = photos.getJSONObject(i).getString("server")
                val id = photos.getJSONObject(i).getString("id")
                val secret = photos.getJSONObject(i).getString("secret")
                val photoLink = "https://farm$farmID.staticflickr.com/$serverID/${id}_$secret.jpg"
                images.add(Images(title, photoLink))
            }
            RVAdapter.notifyDataSetChanged()
        }
    }

    fun OPEN(link: String){
        Glide.with(this).load(link).into(ImgView)
        ImgView.isVisible = true
        RV.isVisible = false
        LLO.isVisible = false
    }

    private fun CLOSE(){
        ImgView.isVisible = false
        RV.isVisible = true
        LLO.isVisible = true
    }
}