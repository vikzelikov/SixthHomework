package bonch.dev.school

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.school.MainActivity.Companion.isInternet
import bonch.dev.school.adapters.PhotosAdapter
import bonch.dev.school.networking.RetrofitFactory
import bonch.dev.school.realm.Photo
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_photos.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class PhotosActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this)

//инициализация
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .name("photo.realm")
                .build()
        realm = Realm.getInstance(config)

        if (isInternet(this)) {
            sendRequest()
        } else {
            val list: List<Photo> = getData()
            initRecyclerView(list)
        }
    }

    private fun initRecyclerView(list: List<Photo>) {
        recyclerView.adapter = PhotosAdapter(list, this)
    }

    private fun sendRequest() {
        val service = RetrofitFactory.makeRetrofitService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getPhotos()
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val list: List<Photo> = response.body()!!
                        saveData(list)
                        initRecyclerView(list)
                    } else {
                        Toast.makeText(applicationContext, "${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (err: HttpException) {
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }

    private fun getData(): List<Photo> {
        var list: ArrayList<Photo> = arrayListOf()
        val realmResult = realm.where(Photo::class.java).findAll()
        if (!realmResult.isEmpty()) {
            for(i in realmResult.indices){
                val tempDataPhoto = Photo(realmResult[i]!!.id, realmResult[i]!!.albumId!!, realmResult[i]!!.url!!)
                list.add(tempDataPhoto)
            }
        } else {
            Toast.makeText(applicationContext, "No Internet connection!", Toast.LENGTH_SHORT).show()
        }
        return list
    }

    private fun saveData(list: List<Photo>) {
        val arrList: ArrayList<Photo> = arrayListOf()
        if (list.isNotEmpty()) {
            //сначала все упакуем в один массив, а потом одной транзакцией отправим в БД
            list.forEach {
                val photo = Photo()
                photo.id = it.id
                photo.albumId = it.albumId
                photo.url = it.url
                arrList.add(photo)
            }
            //одним траншем пишем в базу
            realm.executeTransactionAsync({ bgRealm ->
                bgRealm.insertOrUpdate(arrList) //сохраняем первый раз или обновляем уже имеющееся
            }, {
                Toast.makeText(applicationContext, "Success write", Toast.LENGTH_SHORT).show()
            }, {
                Toast.makeText(applicationContext, "Fail write", Toast.LENGTH_SHORT).show()
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
