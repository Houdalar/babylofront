package tn.esprit.front.Views.Activities.AudioBooks

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.with
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.AudioBook
import tn.esprit.front.viewmodels.AudioBookAPi
import tn.esprit.front.viewmodels.musicApi

class book_adapter (val books: MutableList<AudioBook>) : RecyclerView.Adapter<booksholder>()  {
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): booksholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.audiobook_item, parent, false)

        return booksholder(view)
    }

    override fun onBindViewHolder(holder:booksholder, position: Int) {


        val cover = books[position].cover
        Glide.with(holder.itemView.context).load(cover).into(holder.bookcover)
        println("id book is ${books[position].id}")

        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, full_AudioBook::class.java)
            intent.apply {
                putExtra("title", books[position].bookTitle)
                putExtra("author", books[position].Author)
                putExtra("bookcover", books[position].cover)
                putExtra("bookUrl", books[position].url)
                putExtra("bookId", books[position].id)
                putExtra("Description", books[position].Description)
                putExtra("Rating", books[position].Rating)
                putExtra("duration", books[position].duration)
                putExtra("narrator", books[position].narrator)
                putExtra("category", books[position].category)
                putExtra("language", books[position].language)
            }
            holder.itemView.context.startActivity(intent)

        }
        var mSharedPreferences: SharedPreferences = holder.itemView.context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE)
        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Add to bookshelf")
            builder.setMessage("Do you want to add this book to your bookshelf ?")
            builder.setPositiveButton("Yes") { dialog, which ->
                val api = AudioBookAPi.create()
                val map: HashMap<String, String> = HashMap()
                val token =mSharedPreferences.getString(TOKEN,"").toString()
                map["token"]=token
                map["title"]=books[position].bookTitle.toString()
                val call = api.addFavoritesbooks(map)
                call.enqueue(object : Callback<AudioBook> {
                    override fun onResponse(call: Call<AudioBook>, response: Response<AudioBook>) {
                        if (response.isSuccessful) {
                            Toast.makeText(holder.itemView.context, "book added succesfully", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<AudioBook>, t: Throwable) {
                        Toast.makeText(holder.itemView.context, "Error", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            builder.setNegativeButton("No") { dialog, which ->
                Toast.makeText(holder.itemView.context, "book not added to bookshelf", Toast.LENGTH_SHORT).show()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            true
        })

    }

    override fun getItemCount() = books.size
}
class booksholder (itemView: View) : RecyclerView.ViewHolder(itemView)
{
    var bookcover : ImageView




    init {
        bookcover = itemView.findViewById<ImageView>(R.id.book_cover)


    }

}

