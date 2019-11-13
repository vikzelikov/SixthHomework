package bonch.dev.school.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.school.R
import bonch.dev.school.realm.Photo
import com.bumptech.glide.Glide

class PhotosAdapter(val list: List<Photo>, val context: Context) : RecyclerView.Adapter<PhotosAdapter.PhotosHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosHolder {
        return PhotosHolder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PhotosHolder, position: Int) {
        val photo = list[position]
        holder.bind(photo, position)
    }

    inner class PhotosHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.item_photo)
        private val photosTextView = itemView.findViewById<TextView>(R.id.item_id)
        fun bind(photos: Photo, position: Int) {
            photosTextView.text = "image #${position + 1}"
            Glide.with(context).load(photos.url).into(imageView)
        }
    }
}