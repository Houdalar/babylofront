package tn.esprit.front.Views.Activities.Vaccins

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.Vaccine
import tn.esprit.front.viewmodels.BabyViewModel

class VaccinsActivity : AppCompatActivity() {
    var services = BabyViewModel.create()

    lateinit var vaccineRecyclerView: RecyclerView
    lateinit var vaccineAdapter: VaccineAdapter
    lateinit var vaccineList: MutableList<Vaccine>
    lateinit var mSharedPreferences: SharedPreferences

    lateinit var addBtn: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vaccins)
        supportActionBar?.title = "Vaccines"
        mSharedPreferences=getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        addBtn=findViewById(R.id.floating_action_button_vaccins)

        addBtn.setColorFilter(R.color.gray)

        val bbName : String = intent.getStringExtra("BABYNAME").toString()
        addBtn.setOnClickListener{
            VaccineDialog(
                onSubmitClickListener = {vaccine ->
                    Toast.makeText(this,"vaccine : $vaccine", Toast.LENGTH_SHORT)
                }, babyName = bbName
                , token = mSharedPreferences.getString(TOKEN,"").toString()
            ).show(supportFragmentManager,"dialog")
        }

        vaccineList=ArrayList()

        vaccineRecyclerView=findViewById(R.id.vaccinsRecyclerView)

        val map: HashMap<String, String> = HashMap()

        map["token"] = mSharedPreferences.getString(TOKEN,"").toString()
        map["babyName"] = intent.getStringExtra("BABYNAME").toString()
        Log.e("************ babyName BY Vaccine **************",intent.getStringExtra("BABYNAME").toString())
        var vaccineList = mutableListOf<Vaccine>()
        services.getBabyVaccines(map).enqueue(object : Callback<MutableList<Vaccine>> {
            override fun onResponse(call: Call<MutableList<Vaccine>>, response: Response<MutableList<Vaccine>>) {
                if (response.isSuccessful) {
                    val list = response.body() as MutableList<Vaccine>
                    vaccineList.addAll(list)
                    vaccineAdapter= VaccineAdapter(vaccineList)
                    vaccineRecyclerView.adapter=vaccineAdapter
                    vaccineRecyclerView.layoutManager=
                        LinearLayoutManager(this@VaccinsActivity, LinearLayoutManager.VERTICAL,false)
                    vaccineAdapter.notifyDataSetChanged()
                }

                else
                {
                    Toast.makeText(this@VaccinsActivity, "Baby not found", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MutableList<Vaccine>>, t: Throwable)
            {
                Toast.makeText(this@VaccinsActivity, "error while getting vaccines", Toast.LENGTH_SHORT).show()
            }
        })
}}