package tn.esprit.front.Views.Activities.AudioBooks

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.AudioBook
import tn.esprit.front.viewmodels.AudioBookAPi


class bookshelf_adapter (val books: MutableList<AudioBook>) : RecyclerView.Adapter<bookholder>()  {
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bookholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bookshelf_item, parent, false)

        return bookholder(view)
    }

    override fun onBindViewHolder(holder:bookholder, position: Int) {


        val title = books[position].bookTitle
        val author = books[position].Author
        val cover = books[position].cover



        holder.bookTitle.text = title
        holder.bookauthor.text = author
        Glide.with(holder.itemView.context).load(cover).into(holder.bookcover)

        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, full_AudioBook::class.java)
            intent.apply {
                putExtra("title", title)
                putExtra("author",author)
                putExtra("bookcover", books[position].cover)
                putExtra("bookUrl", books[position].url)
                putExtra("bookId", books[position].id)
                putExtra("bookDescription", books[position].Description)
                putExtra("Rating", books[position].Rating)
            }
            holder.itemView.context.startActivity(intent)

        }


        var mSharedPreferences: SharedPreferences = holder.itemView.context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE)

       holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure you want to delete this AudioBook from your Bookshelf ?")
            builder.setPositiveButton("Yes") { dialog, which ->
                val service = AudioBookAPi.create()
                val map : HashMap<String, String> = HashMap()
                if (title != null) {
                    map.put("title", title)
                }
                map["token"]=mSharedPreferences.getString(TOKEN,"").toString()
                service.removeFavoritesbooks(map).enqueue(object : Callback<AudioBook> {
                    override fun onResponse(call: Call<AudioBook>, response: Response<AudioBook>) {
                        if (response.isSuccessful) {
                            Toast.makeText(holder.itemView.context, "AudioBook deleted from your Bookshelf", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()
                            notifyItemRangeChanged(holder.adapterPosition, books.size)
                            books.removeAt(holder.adapterPosition)
                        }
                    }

                    override fun onFailure(call: Call<AudioBook>, t: Throwable) {
                        Toast.makeText(holder.itemView.context, "Error", Toast.LENGTH_SHORT).show()
                    }
                })

            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            true
            return@setOnLongClickListener true
            //refresh the recyclerView



        }
    }

    override fun getItemCount() = books.size
}
class bookholder (itemView: View) : RecyclerView.ViewHolder(itemView)
{
    var bookcover : ImageView
    var bookauthor : TextView
    var bookTitle : TextView



    init {
        bookcover = itemView.findViewById<ImageView>(R.id.book_cover)
        bookauthor= itemView.findViewById<TextView>(R.id.bookauthor)
        bookTitle= itemView.findViewById<TextView>(R.id.book_title)


    }

}

