package tn.esprit.front.Views.Activities.Weight


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
import tn.esprit.front.Views.Activities.Height.WeightAdapter
import tn.esprit.front.Views.Activities.Height.WeightDialog
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.Weight
import tn.esprit.front.viewmodels.BabyViewModel

class WeightActivity : AppCompatActivity() {

    var services = BabyViewModel.create()

    lateinit var weightRecyclerView:RecyclerView
    lateinit var weightAdapter: WeightAdapter
    lateinit var weightList: MutableList<Weight>
    lateinit var mSharedPreferences: SharedPreferences

    lateinit var addBtn: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight)

        supportActionBar?.setTitle("Weight")

        mSharedPreferences=getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        addBtn=findViewById(R.id.floating_action_button_weight)

        addBtn.setColorFilter(R.color.gray)

        val bbName : String = intent.getStringExtra("BABYNAME").toString()
        addBtn.setOnClickListener{
            WeightDialog(
                onSubmitClickListener = {weight ->
                    Toast.makeText(this,"weight : $weight", Toast.LENGTH_SHORT)
                }, babyName = bbName
                , token = mSharedPreferences.getString(TOKEN,"").toString()
            ).show(supportFragmentManager,"dialog")
        }

        weightList=ArrayList()

        weightRecyclerView=findViewById(R.id.weightRecyclerView)

        val map: HashMap<String, String> = HashMap()

        map["token"] = mSharedPreferences.getString(TOKEN,"").toString()
        map["babyName"] = intent.getStringExtra("BABYNAME").toString()
        Log.e("************ babyName BY WEIGHT **************",intent.getStringExtra("BABYNAME").toString())
        var weightlist = mutableListOf<Weight>()
        services.getBabyWeights(map).enqueue(object : Callback<MutableList<Weight>> {
            override fun onResponse(call: Call<MutableList<Weight>>, response: Response<MutableList<Weight>>) {
                if (response.isSuccessful) {
                    val list = response.body() as MutableList<Weight>
                    weightlist.addAll(list)
                    weightAdapter= WeightAdapter(weightlist)
                    weightRecyclerView.adapter=weightAdapter
                    weightRecyclerView.layoutManager=LinearLayoutManager(this@WeightActivity,LinearLayoutManager.VERTICAL,false)
                    weightAdapter.notifyDataSetChanged()
                }

                else
                {
                    Toast.makeText(this@WeightActivity, "Baby not found", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MutableList<Weight>>, t: Throwable)
            {
                Toast.makeText(this@WeightActivity, "error while getting weights", Toast.LENGTH_SHORT).show()
            }
        })
}}