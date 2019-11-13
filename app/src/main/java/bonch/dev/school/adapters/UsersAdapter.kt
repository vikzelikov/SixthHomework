package bonch.dev.school.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.school.R
import bonch.dev.school.models.DataUser

class UsersAdapter (val list : List<DataUser>, val context : Context) :
        RecyclerView.Adapter<UsersAdapter.ItemPostHolder> () {

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
        val user = list[position]
        holder.bind(user)
    }

    class ItemPostHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val bodyPostTextView = itemView.findViewById<TextView>(R.id.item_body)
        fun bind (post : DataUser) {
            bodyPostTextView.text = "\n${post.id} ${post.name}\n ${post.email}\n"
        }
    }
}