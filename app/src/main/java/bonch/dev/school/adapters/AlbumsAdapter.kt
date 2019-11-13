package bonch.dev.school.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.school.R
import bonch.dev.school.models.DataAlbum
import bonch.dev.school.networking.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class AlbumsAdapter (val list : MutableList<DataAlbum>, val context : Context) : RecyclerView.Adapter<AlbumsAdapter.ItemPostHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder (
                LayoutInflater.from(context)
                        .inflate(R.layout.item_post, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val album = list[position]
        holder.bind(album)

        holder.itemView.setOnClickListener {
            requestDeleteAlbum(list[position].id, position)
        }
    }

    inner class ItemPostHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val bodyPostTextView = itemView.findViewById<TextView>(R.id.item_body)
        fun bind (post : DataAlbum) {
            bodyPostTextView.text = "\n${post.id} ${post.title}\n"
        }
    }

    private fun requestDeleteAlbum(id: Int, position: Int){
        val service = RetrofitFactory.makeRetrofitService()
        Toast.makeText(context, "Запрос отправлен", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            val call: Call<Void> = service.deleteAlbum(id)
            try {
                withContext(Dispatchers.Main) {
                    call.enqueue(DeleteAlbum(position))
                }
            } catch (err : HttpException) {
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }

    inner class DeleteAlbum(private val position: Int) : Callback<Void> {
        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "Ответ получен (FALSE)", Toast.LENGTH_SHORT).show()
        }
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if(response.code() == 200){
                Toast.makeText(context, "Ответ получен (TRUE)", Toast.LENGTH_SHORT).show()
                removeItem(list[position])
            }
        }
    }

    private fun removeItem(obj: DataAlbum) {
        list.remove(obj)
        notifyDataSetChanged()
    }
}