package tn.esprit.front.Views.Activities.PlayList

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.PlayList
import tn.esprit.front.viewmodels.musicApi


class Categories : Fragment() {
    lateinit var recylcerPlaylist: RecyclerView
    lateinit var recylcerPlaylistAdapter: PlayListViewAdapter
    lateinit var mSharedPreferences: SharedPreferences
    var tracks: ArrayList<PlayList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        recylcerPlaylist = view.findViewById(R.id.recyclerplaylist)
        mSharedPreferences=requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
       val map: HashMap<String, String> = HashMap()
        val token =mSharedPreferences.getString(TOKEN,"").toString()
        map["token"]=token
        var services = musicApi.create()




        services.getMediaCategory()!!.enqueue(object : Callback<MutableList<PlayList>> {
            override fun onResponse(
                call: Call<MutableList<PlayList>>,
                response: Response<MutableList<PlayList>>
            ) {
                if (response.code() == 200) {


                    val playList = response.body() as MutableList<PlayList>
                    tracks.clear()
                    tracks.addAll(playList)


                    recylcerPlaylistAdapter = PlayListViewAdapter(tracks)
                   // recylcerPlaylistAdapter.notifyDataSetChanged()
                    recylcerPlaylist.adapter = recylcerPlaylistAdapter
                    recylcerPlaylist.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                } else {
                    Toast.makeText(context, "error ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MutableList<PlayList>>, t: Throwable) {
                Toast.makeText(context, "error while getting the playlist", Toast.LENGTH_SHORT).show()
            }

        })


        return view
    }

}
