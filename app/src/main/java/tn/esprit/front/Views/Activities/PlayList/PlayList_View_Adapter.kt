package tn.esprit.front.Views.Activities.PlayList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
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
import tn.esprit.front.viewmodels.musicApi

class PlayListViewAdapter (val PlayList: MutableList<PlayList>) : RecyclerView.Adapter<PlayListViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_item, parent, false)

        return PlayListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val name = PlayList[position].name
        val cover = PlayList[position].cover



        Glide.with(holder.itemView.context).load(cover).into(holder.playlistCover)
        holder.playlistName.text = name


        var mSharedPreferences: SharedPreferences = holder.itemView.context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE)

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, Full_PlayList_Activity::class.java)
            intent.apply {
                putExtra("playlistname", name)
                putExtra("playlistcover", cover)
                putExtra("playlistid", PlayList[position]._id)
                putExtra("tag", "playlist")
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener(){
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete Playlist")
            builder.setMessage("Are you sure you want to delete this playlist ?")
            builder.setPositiveButton("Yes"){dialog, which ->
               val services = musicApi.create()
                val map : HashMap<String, String> = HashMap()
                map["name"] = PlayList[position].name.toString()
                map["token"]=mSharedPreferences.getString(TOKEN,"").toString()
                services.deletePlayList(map).enqueue(object : Callback<PlayList> {
                    override fun onResponse(call: Call<PlayList>, response: Response<PlayList>) {
                        if (response.isSuccessful) {
                            Toast.makeText(holder.itemView.context, "Playlist deleted successfully", Toast.LENGTH_SHORT).show()
                            PlayList.removeAt(position)
                            notifyDataSetChanged()
                        }
                    }

                    override fun onFailure(call: Call<PlayList>, t: Throwable) {
                        Toast.makeText(holder.itemView.context, "Error deleting playlist", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            true
        }
    }

    override fun getItemCount() = PlayList.size

}
