package tn.esprit.front.Views.Activities.AudioBooks

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.AudioBook
import tn.esprit.front.viewmodels.AudioBookAPi
import tn.esprit.front.viewmodels.musicApi

class bookshelf : Fragment() {
    lateinit var booklistview: RecyclerView
    lateinit var booklistviewAdapter: bookshelf_adapter
    var booklist: ArrayList<AudioBook> = ArrayList()
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var mSharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_bookshelf, container, false)
        mSharedPreferences=requireActivity().getSharedPreferences("sharedPrefs",
            Context.MODE_PRIVATE
        )
        val preferences:SharedPreferences=requireActivity().getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
        val token =preferences.getString(TOKEN,"").toString()
        booklistview = view.findViewById(R.id.bookshelf)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        val map: HashMap<String, String> = HashMap()
        var services = AudioBookAPi.create()

        map["token"] = token

        services.getFavoritesbooks(map)!!.enqueue(object : Callback<MutableList<AudioBook>> {
            override fun onResponse(call: Call<MutableList<AudioBook>>, response: Response<MutableList<AudioBook>>) {
                if (response.code()==200) {


                    val List = response.body() as MutableList<AudioBook>
                    println(List)
                    // add the list to shared preferences
                    val editor = mSharedPreferences.edit()
                    val set: MutableSet<String> = HashSet()
                    set.addAll( List.map { it.bookTitle?: "" })
                    editor.putStringSet("bookshelf", set)
                    editor.commit()
                    println(set)
                      //  editor.putStringSet("favoriteTracks", tracksList.map { it.name }.toSet())


                   // editor.putString("favorites", tracks.toString())

                    booklistviewAdapter = bookshelf_adapter(List)
                    booklistview.adapter = booklistviewAdapter
                    booklistviewAdapter.notifyDataSetChanged()
                    booklistview.layoutManager = LinearLayoutManager(context , LinearLayoutManager.VERTICAL, false)
                }

                else
                {
                    Toast.makeText(context, "error ", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MutableList<AudioBook>>, t: Throwable)
            {
                Toast.makeText(context, "error while getting the tracks", Toast.LENGTH_SHORT).show()
            }
        })
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            booklistviewAdapter.notifyDataSetChanged()
        }
        return  view

    }


}