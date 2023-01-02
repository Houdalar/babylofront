package tn.esprit.front.Views.Activities.PlayList

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import tn.esprit.front.models.PlayList
import tn.esprit.front.models.Tracks
import tn.esprit.front.viewmodels.musicApi


class songplaylistviewadapter (val tracks: MutableList<Tracks>) : RecyclerView.Adapter<songviewHolder>()  {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var mediaPlayer: MediaPlayer
    var isPlaying :  Boolean = false
    var isfavorite: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): songviewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.topmusic_item, parent, false)

        return songviewHolder(view)
    }

    override fun onBindViewHolder(holder:songviewHolder, @SuppressLint("RecyclerView") position: Int) {


        val name = tracks[position].name
        val artist = tracks[position].artist
        val cover = tracks[position].cover

       // if the name of the song is too long, it will be cut and add "..." at the end
        if (name.length > 20) {
            holder.songName.text = name.substring(0, 20) + "..."
            // if the name is focused, it will show the full name with animation "fade in"
            holder.songName.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    holder.songName.text = name
                    holder.songName.isSelected = true
                } else {
                    holder.songName.text = name.substring(0, 20) + "..."
                }
            }


        } else {
            holder.songName.text = name
        }

        holder.songArtist.text = artist
        Glide.with(holder.itemView.context).load(cover).into(holder.songcover)

        //get the favorites song and set the heart icon
        sharedPreferences = holder.itemView.context.getSharedPreferences("sharedPrefs", 0)
        var favorite: MutableList<String>?
        favorite = sharedPreferences.getStringSet("favoriteTracks", mutableSetOf())?.toMutableList()

        if (favorite == null) {
            favorite = mutableListOf()
        }
        if (favorite.contains(name))
        {
            holder.fav.setImageResource(R.drawable.ic_baseline_favorite_24)
            isfavorite = true
        }
        else
        {
            holder.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            isfavorite = false
        }

        // play the song
        var preferences: SharedPreferences = holder.itemView.context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE)
        holder.itemView.setOnClickListener{
            // get the recyclerview tag
            val intent = Intent(holder.itemView.context, song_detail::class.java)
            intent.apply {
                putExtra("songName", name)
                putExtra("songArtist", artist)
                putExtra("songCover", tracks[position].cover)
                putExtra("songUrl", tracks[position].url)
                putExtra("songId", tracks[position]._id)
                putExtra("tag", "playlist")
            }
            val service = musicApi.create()
            val tracks = Tracks(name = name)
            service.countListened(tracks)
            holder.itemView.context.startActivity(intent)


    }
        // on click on the heart icon
        holder.fav.setOnClickListener {
            val service = musicApi.create()
            val map = HashMap<String, String>()
            map["songname"] = name
            map["token"]=preferences.getString(TOKEN,"").toString()
            // map["token"]= sharedPreferences.getString("token", "").toString()
            if(isfavorite)
            {

                service.removeFavoritesTrack(map).enqueue(object : Callback<Tracks> {
                    override fun onFailure(call: Call<Tracks>, t: Throwable) {
                        Toast.makeText(holder.itemView.context, "Error", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<Tracks>, response: Response<Tracks>) {
                        if (response.isSuccessful) {
                            Toast.makeText(holder.itemView.context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                            holder.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                            isfavorite = false
                        }
                    }
                })
            }
            else
            {
                    service.addFavoritesTrack(map)
                        .enqueue(object : Callback<Tracks> {
                            override fun onResponse(call: Call<Tracks>, response: Response<Tracks>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(holder.itemView.context, "Added to favorites", Toast.LENGTH_SHORT).show()
                                    holder.fav.setImageResource(R.drawable.ic_baseline_favorite_24)
                                    isfavorite = true
                                }
                            }

                            override fun onFailure(call: Call<Tracks>, t: Throwable) {
                                Toast.makeText(holder.itemView.context, "Error", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
        }

        //on a long click on the item of the recycler view show a dialog to add the song to the playlist or to the fav list or to share it
        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
            val map : HashMap<String, String> = HashMap()
            //map.put("trackid", tracks[position]._id.toString())
            map["token"]=preferences.getString(TOKEN,"").toString()
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Choose an option")
            val options = arrayOf("Add to playlist","Share","cancel")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    0 -> {
                        // show the dialog to choose the playlist
                        val service = musicApi.create()
                        val call = service.getPlaylists(map)
                        call.enqueue(object : Callback<MutableList<PlayList>> {


                            override fun onResponse(call: Call<MutableList<PlayList>>, response: Response<MutableList<PlayList>>) {
                                if (response.isSuccessful) {
                                    val playlists = response.body()
                                    val builder = AlertDialog.Builder(holder.itemView.context)
                                    builder.setTitle("Choose a playlist")
                                    val options = arrayOfNulls<String>(playlists!!.size)
                                    for (i in 0 until playlists.size) {
                                        options[i] = playlists[i].name
                                    }
                                    val service = musicApi.create()
                                    map.put("trackid", tracks[position]._id.toString())
                                    map["name"] = playlists[which].name.toString()
                                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                                         service.addTrackToPlayList(map).enqueue(object : Callback<PlayList> {
                                            override fun onResponse(call: Call<PlayList>, response: Response<PlayList>) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(holder.itemView.context, "Song added to playlist", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            override fun onFailure(call: Call<PlayList>, t: Throwable) {
                                                Toast.makeText(holder.itemView.context, "Error while adding the track", Toast.LENGTH_SHORT).show()
                                            }
                                        })
                                    })
                                    builder.show()
                                }
                            }
                            override fun onFailure(call: Call<MutableList<PlayList>>, t: Throwable) {
                                Toast.makeText(holder.itemView.context, "Error", Toast.LENGTH_SHORT).show()
                            }
                        })

                    }
                    1 -> {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TEXT, "Hey, I'm listening to ${tracks[position].name} by ${tracks[position].artist} on babylo. Download it now: https://play.google.com/store/apps/details?id=tn.esprit.front")
                        holder.itemView.context.startActivity(Intent.createChooser(intent, "Share with"))
                    }
                    2 -> {
                        dialog.dismiss()
                    }
                }
            })
            builder.show()
            true
        })

        holder.play.setOnClickListener()
        {

            if (isPlaying) {
                mediaPlayer.pause()
                holder.play.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                isPlaying = false
            } else {
                mediaPlayer.start()
                holder.play.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
                isPlaying = true
                val service = musicApi.create()
                val tracks = Tracks(name = name)
                service.countListened(tracks)
            }
        }
    }
    //


    override fun getItemCount() = tracks.size




}