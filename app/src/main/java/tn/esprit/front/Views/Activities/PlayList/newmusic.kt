package tn.esprit.front.Views.Activities.PlayList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.models.Tracks
import tn.esprit.front.viewmodels.musicApi
import java.util.*
import kotlin.collections.ArrayList


class newmusic : Fragment() {
    lateinit var recylcersong: RecyclerView
    lateinit var recylcersongAdapter: songnewviewadapter
    var tracks: ArrayList<Tracks> = ArrayList()
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_topmusic, container, false)
        recylcersong = view.findViewById(R.id.recyclerplaylist)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        //preference= requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        //val token = preference.getString("token", "")
        var services = musicApi.create()




        services.getNewestTracks()!!.enqueue(object : Callback<MutableList<Tracks>> {
            override fun onResponse(
                call: Call<MutableList<Tracks>>,
                response: Response<MutableList<Tracks>>
            ) {
                if (response.code() == 200) {


                    val tracksList = response.body() as MutableList<Tracks>
                    tracks.clear()
                    tracks.addAll(tracksList)

                    recylcersong.tag = "newmusic"
                    recylcersongAdapter = songnewviewadapter(tracks)
                    recylcersongAdapter.notifyDataSetChanged()
                    recylcersong.adapter = recylcersongAdapter
                    recylcersong.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                } else {
                    Toast.makeText(context, "error ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MutableList<Tracks>>, t: Throwable) {
                Toast.makeText(context, "error while getting the tracks", Toast.LENGTH_SHORT).show()
            }

        })

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            recylcersongAdapter.notifyDataSetChanged()
        }
        return view
    }
}


