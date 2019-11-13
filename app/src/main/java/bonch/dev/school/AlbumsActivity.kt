package bonch.dev.school

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.school.MainActivity.Companion.isInternet
import bonch.dev.school.adapters.AlbumsAdapter
import bonch.dev.school.models.DataAlbum
import bonch.dev.school.networking.RetrofitFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class AlbumsActivity : AppCompatActivity() {

    private val caseType = object : TypeToken<List<DataAlbum>>() {}.type
    private val APP_PREFERENCES_ALBUMS = "albums"
    private lateinit var gson: Gson
    private lateinit var pref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_albums)

        pref = getPreferences(MODE_PRIVATE)
        gson = Gson()

        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this)

        if (isInternet(this)) {
            sendRequest()
        } else {
            val list: List<DataAlbum> = getData()
            initRecyclerView(list)
        }
    }

    private fun initRecyclerView(list: List<DataAlbum>) {
        recyclerView.adapter = AlbumsAdapter(list as MutableList, this)
    }

    private fun sendRequest() {
        val service = RetrofitFactory.makeRetrofitService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getAlbums(1)
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val list: List<DataAlbum> = response.body()!!
                        saveData(list)
                        initRecyclerView(list)
                    } else {
                        Toast.makeText(applicationContext, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (err: HttpException) {
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }

    private fun getData(): MutableList<DataAlbum> {
        var list: MutableList<DataAlbum> = mutableListOf()
        if (pref.contains(APP_PREFERENCES_ALBUMS)) {
            val json: String? = pref.getString(APP_PREFERENCES_ALBUMS, "")
            list = gson.fromJson(json, caseType)
        } else {
            Toast.makeText(applicationContext, "No Internet connection!", Toast.LENGTH_SHORT).show()
        }
        return list
    }

    private fun saveData(list: List<DataAlbum>) {
        if (list.isNotEmpty()) {
            val editor = pref.edit()
            editor.putString(APP_PREFERENCES_ALBUMS, gson.toJson(list))
            editor.apply()
        }
    }

}
