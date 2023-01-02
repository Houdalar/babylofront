package tn.esprit.front.Views.Activities.Height

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.Height
import tn.esprit.front.viewmodels.BabyViewModel


class HeightActivity : AppCompatActivity() {

    var services = BabyViewModel.create()

    lateinit var heightRecyclerView:RecyclerView
    lateinit var heightAdapter: HeightAdapter
    lateinit var heightList: MutableList<Height>
    lateinit var mSharedPreferences: SharedPreferences


    lateinit var addBtn: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_height)
        supportActionBar?.setTitle("Height")
        mSharedPreferences=getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        addBtn=findViewById(R.id.floating_action_button_height)

        addBtn.setColorFilter(R.color.gray)

        val bbName : String = intent.getStringExtra("BABYNAME").toString()
        addBtn.setOnClickListener{
            HeightDialog(
                onSubmitClickListener = {height ->
                    Toast.makeText(this,"height : $height",Toast.LENGTH_SHORT)
                }, babyName = bbName
                , token = mSharedPreferences.getString(TOKEN,"").toString()
            ).show(supportFragmentManager,"dialog")
        }

        heightList=ArrayList()

        heightRecyclerView=findViewById(R.id.heightRecyclerView)

        val map: HashMap<String, String> = HashMap()

        map["token"] = mSharedPreferences.getString(TOKEN,"").toString()
        map["babyName"] = intent.getStringExtra("BABYNAME").toString()
        Log.e("************ babyName BY HEIGHT **************",intent.getStringExtra("BABYNAME").toString())
        var heightlist = mutableListOf<Height>()
        services.getBabyHeights(map).enqueue(object : Callback<MutableList<Height>> {
            override fun onResponse(call: Call<MutableList<Height>>, response: Response<MutableList<Height>>) {
                if (response.isSuccessful) {
                    val list = response.body() as MutableList<Height>
                    heightlist.addAll(list)
                    heightAdapter= HeightAdapter(heightlist)
                    heightRecyclerView.adapter=heightAdapter
                    heightRecyclerView.layoutManager=LinearLayoutManager(this@HeightActivity,LinearLayoutManager.VERTICAL,false)
                    heightAdapter.notifyDataSetChanged()
                }

                else
                {
                    Toast.makeText(this@HeightActivity, "Baby not found", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MutableList<Height>>, t: Throwable)
            {
                Toast.makeText(this@HeightActivity, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        })

        /*heightList.add(Height("ines.said@esprit.tn","wafa","50","28/11/2022"))
        heightList.add(Height("ines.said@esprit.tn","wafa","50","28/11/2022"))
        heightList.add(Height("ines.said@esprit.tn","wafa","50","30/11/2022"))
        heightList.add(Height("ines.said@esprit.tn","wafa","60","30/11/2022"))*/






       /* var heightList:MutableList<Height> = ArrayList()

        val baby= Baby(email="ines.said@esprit.tn", babyName = "wafa")

        services.getBabyHeights(baby).enqueue(object : Callback<Baby> {

            override fun onResponse(call: Call<Baby>, response: Response<Baby>) {

                if (response.code()==200){

                }

                if (response.code()==400){
                    Snackbar.make(
                        findViewById(R.id.addHeightPage),
                        getString(R.string.userNotfound),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                if (response.code()==401){
                    Snackbar.make(
                        findViewById(R.id.addHeightPage),
                        getString(R.string.babyNotfound),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Baby>, t: Throwable) {
                t.message?.let {
                    Snackbar.make(
                        findViewById(R.id.addHeightPage),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

        })*/






}}