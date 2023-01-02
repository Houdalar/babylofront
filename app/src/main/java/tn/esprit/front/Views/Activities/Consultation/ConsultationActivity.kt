package tn.esprit.front.Views.Activities.Consultation

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
import tn.esprit.front.Views.Activities.Vaccins.ConsultationAdapter
import tn.esprit.front.Views.Activities.Vaccins.ConsultationDialog
import tn.esprit.front.models.Consultation
import tn.esprit.front.viewmodels.BabyViewModel

class ConsultationActivity : AppCompatActivity() {
    var services = BabyViewModel.create()

    lateinit var consultationRecyclerView: RecyclerView
    lateinit var consultationAdapter: ConsultationAdapter
    lateinit var consultationList: MutableList<Consultation>
    lateinit var mSharedPreferences: SharedPreferences

    lateinit var addBtn: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultation)
        supportActionBar?.title = "Consultations"
        mSharedPreferences=getSharedPreferences(PREF_NAME, MODE_PRIVATE)



        addBtn=findViewById(R.id.floating_action_button_consultations)

        addBtn.setColorFilter(R.color.gray)
        val bbName : String = intent.getStringExtra("BABYNAME").toString()
        addBtn.setOnClickListener{
            ConsultationDialog(
                onSubmitClickListener = {doctor ->
                    Toast.makeText(this,"doctor : $doctor", Toast.LENGTH_SHORT)
                }, babyName = bbName
                , token = mSharedPreferences.getString(TOKEN,"").toString()
            ).show(supportFragmentManager,"dialog")
        }

        consultationList=ArrayList()

        consultationRecyclerView=findViewById(R.id.consultationsRecyclerView)

        val map: HashMap<String, String> = HashMap()

        map["token"] = mSharedPreferences.getString(TOKEN,"").toString()
        map["babyName"] = intent.getStringExtra("BABYNAME").toString()
        Log.e("************ babyName BY Consultation **************",intent.getStringExtra("BABYNAME").toString())
        var consultationList = mutableListOf<Consultation>()
        services.getDoctorConsultations(map).enqueue(object : Callback<MutableList<Consultation>> {
            override fun onResponse(call: Call<MutableList<Consultation>>, response: Response<MutableList<Consultation>>) {
                if (response.isSuccessful) {
                    val list = response.body() as MutableList<Consultation>
                    consultationList.addAll(list)
                    consultationAdapter= ConsultationAdapter(consultationList)
                    consultationRecyclerView.adapter=consultationAdapter
                    consultationRecyclerView.layoutManager=
                        LinearLayoutManager(this@ConsultationActivity, LinearLayoutManager.VERTICAL,false)
                    consultationAdapter.notifyDataSetChanged()
                }

                else
                {
                    Toast.makeText(this@ConsultationActivity, "Baby not found", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MutableList<Consultation>>, t: Throwable)
            {
                Toast.makeText(this@ConsultationActivity, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        })
    }
}