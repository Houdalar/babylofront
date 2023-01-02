package tn.esprit.front.Views.Activities.Home

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_drawer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.*
import tn.esprit.front.Views.Activities.AddBabyActivity
import tn.esprit.front.Views.Activities.AudioBooks.book_home_page
import tn.esprit.front.Views.Activities.Home.BabyList.BabyAdapter
import tn.esprit.front.Views.Activities.PlayList.music_home_page
import tn.esprit.front.Views.Activities.Signin.Login_Activity
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.Baby
import tn.esprit.front.viewmodels.BabyViewModel


class DrawerActivity : AppCompatActivity() {
    var services = BabyViewModel.create()

    lateinit var recyclerBaby: RecyclerView
    lateinit var recyclerBabyAdapter: BabyAdapter
    lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        mSharedPreferences=getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val userEmail =mSharedPreferences.getString(TOKEN,"").toString()
        Log.e("usermail : ",userEmail)

        newbabies.setOnClickListener {
            Log.e("token ***************************************** ",userEmail)
            val intent= Intent(this, AddBabyActivity::class.java)
            intent.putExtra("token",userEmail)
            startActivity(intent)
        }

        recyclerBaby=findViewById(R.id.recyclerView)

        var babylist = mutableListOf<Baby>()
        val map: HashMap<String, String> = HashMap()

        map["token"] = userEmail
        services.getBabyList(map).enqueue(object : Callback<MutableList<Baby>> {
            override fun onResponse(call: Call<MutableList<Baby>>, response: Response<MutableList<Baby>>) {
                println("getBabylistCalled")

                if (response.isSuccessful) {
                    val babies = response.body() as MutableList<Baby>
                    babylist.addAll(babies)
                    recyclerBabyAdapter.notifyDataSetChanged()

                    Log.e("babies : ",babies.toString())
                    println(response.body())
                } else {
                    Toast.makeText(this@DrawerActivity, "Baby List can not be displayed", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<MutableList<Baby>>, t: Throwable) {
                Toast.makeText(this@DrawerActivity, "Check your internet connexion !", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        recyclerBabyAdapter= BabyAdapter(babylist)
        recyclerBaby.adapter=recyclerBabyAdapter

        recyclerBaby.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        nav_view.setNavigationItemSelectedListener {
            it.isChecked=true

            when (it.itemId) {
                R.id.nav_home -> {
                    val intent= Intent(this, DrawerActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_music -> {
                    val intent= Intent(this, music_home_page::class.java)
                    startActivity(intent)
                }
                R.id.nav_Audiobooks -> {
                    val intent= Intent(this, book_home_page::class.java)
                    startActivity(intent)
                }
                /*R.id.nav_settings -> {
                    val intent= Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }*/
                R.id.nav_logout -> {
                    val builder=AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.logoutTitle))
                    builder.setMessage(getString(R.string.logoutMessage))
                    builder.setPositiveButton("Yes"){ dialogInterface, which ->
                        getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().clear().apply()

                        val editor = mSharedPreferences.edit()
                        editor.putString("IS_REMEMBRED","false")
                        editor.apply()

                        finish()
                        val intent= Intent(this, Login_Activity::class.java)
                        startActivity(intent)
                    }
                    builder.setNegativeButton("No"){dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    builder.create().show()
                }
                /*R.id.nav_help -> {
                    val intent= Intent(this, HelpActivity::class.java)
                    startActivity(intent)
                }*/
            }
            true
        }

        val toggle = ActionBarDrawerToggle(this, DrawerLayout, R.string.open, R.string.close)
        DrawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onBackPressed(){
        when {
            //If drawer layout is open close that on back pressed
            DrawerLayout.isDrawerOpen(GravityCompat.START)->{
                DrawerLayout.closeDrawer(GravityCompat.START)
            }
            else->{
                //If drawer is already in closed condition then go back
                super.getOnBackPressedDispatcher().onBackPressed()
            }}
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return when(item.itemId){
            android.R.id.home ->{
                DrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}