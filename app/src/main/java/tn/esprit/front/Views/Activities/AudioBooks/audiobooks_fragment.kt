package tn.esprit.front.Views.Activities.AudioBooks

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.AudioBook
import tn.esprit.front.viewmodels.AudioBookAPi



class AudioBook : Fragment() {
    lateinit var recentbooklistview: RecyclerView
    lateinit var recentbooklistviewAdapter: book_adapter

    lateinit var topbooksview: RecyclerView
    lateinit var topbooksviewAdapter: book_adapter
    var topbooks: ArrayList<AudioBook> = ArrayList()


    lateinit var newbooksview: RecyclerView
    lateinit var newbooksviewAdapter: book_adapter

    lateinit var fictionbooksview: RecyclerView
    lateinit var fictionbooksviewAdapter: book_adapter

    lateinit var adventurebookview: RecyclerView
    lateinit var adventurebookviewAdapter: book_adapter

    lateinit var mSharedPreferences: SharedPreferences

    var newbooks: ArrayList<AudioBook> = ArrayList()
    var fictionbooks: ArrayList<AudioBook> = ArrayList()
    var disneybooks: ArrayList<AudioBook> = ArrayList()
    var advanturebooks: ArrayList<AudioBook> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_audiobook, container, false)
        topbooksview = view.findViewById(R.id.topbooks)
        newbooksview = view.findViewById(R.id.newbooks)
        fictionbooksview = view.findViewById(R.id.fictionbooks)
        adventurebookview = view.findViewById(R.id.adventurebooks)

        mSharedPreferences=requireActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE)


       val map: HashMap<String, String> = HashMap()
        val token =mSharedPreferences.getString(TOKEN,"").toString()
        map["token"]=token
        println("token is $token")
        var services = AudioBookAPi.create()
        services.getTopAudioBook()!!.enqueue(object : Callback<MutableList<AudioBook>> {
            override fun onResponse(
                call: Call<MutableList<AudioBook>>,
                response: Response<MutableList<AudioBook>>
            ) {
                if (response.code() == 200) {
                    val List = response.body() as MutableList<AudioBook>
                    topbooks.clear()
                    topbooks.addAll(List)
                    topbooksviewAdapter = book_adapter(topbooks)
                    topbooksview.adapter = topbooksviewAdapter
                    topbooksview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                } else {
                    Toast.makeText(context, "error ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MutableList<AudioBook>>, t: Throwable) {
                Toast.makeText(context, "error while getting the AudioBook", Toast.LENGTH_SHORT).show()
            }

        })
        services.getNewestAudioBook()!!.enqueue(object : Callback<MutableList<AudioBook>> {
            override fun onResponse(
                call: Call<MutableList<AudioBook>>,
                response: Response<MutableList<AudioBook>>
            ) {
                if (response.code() == 200) {
                    val List = response.body() as MutableList<AudioBook>
                    newbooks.clear()
                    newbooks.addAll(List)
                    newbooksviewAdapter = book_adapter(newbooks)
                    newbooksview.adapter = newbooksviewAdapter
                    newbooksview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                } else {
                    Toast.makeText(context, "error ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MutableList<AudioBook>>, t: Throwable) {
                Toast.makeText(context, "error while getting the AudioBook", Toast.LENGTH_SHORT).show()
            }

        })

        return view
    }

}
