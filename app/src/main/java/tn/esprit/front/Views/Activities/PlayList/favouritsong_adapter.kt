package tn.esprit.front.Views.Activities.PlayList

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.Tracks
import tn.esprit.front.viewmodels.musicApi

class favouritsong_adapter (val tracks: MutableList<Tracks>) : RecyclerView.Adapter<favholder>()  {

    lateinit var mediaPlayer: MediaPlayer
    var isPlaying :  Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): favholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favoritmusic_item, parent, false)

        return favholder(view)
    }

    override fun onBindViewHolder(holder:favholder, position: Int) {


        val name = tracks[position].name
        val artist = tracks[position].artist
        val cover = tracks[position].cover



        holder.songName.text = name
        holder.songArtist.text = artist
        Glide.with(holder.itemView.context).load(cover).into(holder.songcover)
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(tracks[position].url)
        mediaPlayer.prepare()

        var mSharedPreferences: SharedPreferences = holder.itemView.context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE)

        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, song_detail::class.java)
            intent.apply {
                putExtra("songName", name)
                putExtra("songArtist", artist)
                putExtra("songCover", tracks[position].cover)
                putExtra("songUrl", tracks[position].url)
                putExtra("songId", tracks[position]._id)
                putExtra("tag", "fav")
            }
            holder.itemView.context.startActivity(intent)
            val service = musicApi.create()
            val tracks = Tracks(name = name)
            service.countListened(tracks)
        }
        holder.songplay.setOnClickListener()
        {

            if (isPlaying) {
                mediaPlayer.pause()
                holder.songplay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                isPlaying = false
            } else {
                mediaPlayer.start()
                holder.songplay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
                isPlaying = true
                val service = musicApi.create()
                val tracks = Tracks(name = name)
                service.countListened(tracks)
            }
        }



       holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure you want to delete this song from your favorits ?")
            builder.setPositiveButton("Yes") { dialog, which ->
                val service = musicApi.create()
                val map : HashMap<String, String> = HashMap()
                map.put("songname", name)
                map["token"]=mSharedPreferences.getString(TOKEN,"").toString()
                service.removeFavoritesTrack(map).enqueue(object : Callback<Tracks> {
                    override fun onResponse(call: Call<Tracks>, response: Response<Tracks>) {
                        if (response.isSuccessful) {
                            Toast.makeText(holder.itemView.context, "Song deleted from your favorits", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()
                            notifyItemRangeChanged(holder.adapterPosition, tracks.size)
                            Toast.makeText(holder.itemView.context, "Song deleted from your favorits", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Tracks>, t: Throwable) {
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

    override fun getItemCount() = tracks.size
}
class favholder (itemView: View) : RecyclerView.ViewHolder(itemView)
{
    var card : CardView = itemView.findViewById<CardView>(R.id.cardView)
    var songcover : ImageView
    var songArtist : TextView
    var songName : TextView
    var songplay : ImageView


    init {
        songcover = itemView.findViewById<ImageView>(R.id.ivTopMusic)
        songArtist= itemView.findViewById<TextView>(R.id.songauthor)
        songName= itemView.findViewById<TextView>(R.id.track_name)
        songplay= itemView.findViewById<ImageView>(R.id.play)

    }

}

