package tn.esprit.front.Views.Activities.Profile

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_profile.*
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Consultation.ConsultationActivity
import tn.esprit.front.Views.Activities.Height.HeightActivity
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.Views.Activities.Vaccins.VaccinsActivity
import tn.esprit.front.Views.Activities.Weight.WeightActivity

class ProfileActivity : AppCompatActivity() {
    lateinit var babyPic : ImageView
    lateinit var heightBtn:MaterialButton
    lateinit var weightBtn:MaterialButton
    lateinit var doctorBtn:MaterialButton
    lateinit var vaccinesBtn:MaterialButton
    lateinit var mSharedPreferences: SharedPreferences
    lateinit var shape:ImageView



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.hide()

        heightBtn=findViewById(R.id.HeightBtn)
        weightBtn=findViewById(R.id.WeightBtn)
        doctorBtn=findViewById(R.id.DoctorBtn)
        vaccinesBtn=findViewById(R.id.VaccinesBtn)
        babyPic=findViewById(R.id.profilePic)
        shape=findViewById(R.id.shape)

        val background = findViewById<ConstraintLayout>(R.id.background)
        val scrollViewProfile = findViewById<ScrollView>(R.id.scrollViewProfile)

        val name : String = intent.getStringExtra("BABYNAME").toString()
        val picture:String=intent.getStringExtra("BABYPIC").toString()

        mSharedPreferences=getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        //Glide.with(this).load(picture).into(babyPic)

        Glide.with(this).load(picture).into(object : CustomTarget<Drawable>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(
                resource: Drawable,
                transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
            ) {
                val bitmap = resource
                //set the image
                babyPic.setImageDrawable(bitmap)
                val palette = Palette.from(bitmap.toBitmap()).generate()
                val dominantColor = palette.getDominantColor(Color.WHITE)
                val lightDominantColor=palette.getLightVibrantColor(Color.WHITE)
                val darkMutedColor = palette.getDarkMutedColor(Color.WHITE)
                val lightcolor = palette.getLightMutedColor(Color.WHITE)
                //make the dominant color less opaque
                val lessOpaqueDominantColor = dominantColor and 0x00ffffff or 0x44000000
                val redValue = Color.red(dominantColor)
                val greenValue = Color.green(dominantColor)
                val blueValue = Color.blue(dominantColor)
                // set the color of the vector drawable
                val color = Color.argb(255, redValue, greenValue, blueValue)
                val lightcolor2 = Color.argb(
                    Color.alpha(lightcolor),
                    Math.min(Color.red(lightcolor) + 40, 255),
                    Math.min(Color.green(lightcolor) + 40, 255),
                    Math.min(Color.blue(lightcolor) + 40, 255)
                )
                val lightcolor3 = Color.argb(
                    Color.alpha(lightcolor2),
                    Math.min(Color.red(lightcolor2) + 40, 255),
                    Math.min(Color.green(lightcolor2) + 40, 255),
                    Math.min(Color.blue(lightcolor2) + 40, 255)
                )
                shape.setColorFilter(dominantColor)
                heightBtn.setBackgroundColor(dominantColor)
                weightBtn.setBackgroundColor(dominantColor)
                doctorBtn.setBackgroundColor(dominantColor)
                vaccinesBtn.setBackgroundColor(dominantColor)
                background.setBackgroundColor(lightcolor3)
                scrollViewProfile.setBackgroundColor(lightcolor3)

                window.statusBarColor = dominantColor
            }
        })

        heightBtn.setOnClickListener {
            val intent=Intent(this,HeightActivity::class.java)
            intent.apply {
                putExtra("BABYNAME", name)
                putExtra("token", mSharedPreferences.getString(TOKEN,"").toString())
            }
            startActivity(intent)
        }

        weightBtn.setOnClickListener {
            val intent=Intent(this,WeightActivity::class.java)
            intent.apply {
                putExtra("BABYNAME", name)
                putExtra("token", mSharedPreferences.getString(TOKEN,"").toString())
            }
            startActivity(intent)
        }

        doctorBtn.setOnClickListener {
            val intent=Intent(this,ConsultationActivity::class.java)
            intent.apply {
                putExtra("BABYNAME", name)
                putExtra("token", mSharedPreferences.getString(TOKEN,"").toString())
            }
            startActivity(intent)
        }

        vaccinesBtn.setOnClickListener {
            val intent=Intent(this,VaccinsActivity::class.java)
            intent.apply {
                putExtra("BABYNAME", name)
                putExtra("token", mSharedPreferences.getString(TOKEN,"").toString())
            }
            startActivity(intent)
        }
        bbName.text = intent.getStringExtra("BABYNAME").toString()
        bbBirthday.text = intent.getStringExtra("DATE").toString()
        bbGender.text=intent.getStringExtra("GENDER").toString()
}}