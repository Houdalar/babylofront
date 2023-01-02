package tn.esprit.front.Views.Activities.Signin


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import android.widget.EditText
import tn.esprit.front.Views.Activities.Home.DrawerActivity
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signup.SignUp_Activity
import tn.esprit.front.models.User
import tn.esprit.front.viewmodels.UserViewModel

const val PREF_NAME = "LOGIN_PREF_Bear"
const val IS_REMEMBRED = "IS_REMEMBRED"
const val TOKEN = "TOKEN"

class Login_Activity : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var mailError: TextInputLayout
    lateinit var password: EditText
    lateinit var passwordError: TextInputLayout

    lateinit var forgotYourPassword : TextView
    lateinit var rememberMe: CheckBox

    lateinit var preference : SharedPreferences

    lateinit var backToSignUpButton : TextView
    lateinit var loginButton: Button

    var services = UserViewModel.create()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        supportActionBar?.hide()

        email = findViewById(R.id.txtEmail)
        mailError = findViewById(R.id.txtLayoutEmail)
        password = findViewById(R.id.txtPassword)
        passwordError = findViewById(R.id.txtLayoutPassword)

        loginButton = findViewById(R.id.btnLogin)
        backToSignUpButton = findViewById(R.id.back_to_sign_up_button)
        rememberMe = findViewById(R.id.checkBox)

        forgotYourPassword = findViewById(R.id.forgot_password)

        preference=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)





        loginButton.setOnClickListener()
        {
            clickLogin()
        }

        backToSignUpButton.setOnClickListener()
        {
            val intent = Intent(this@Login_Activity, SignUp_Activity::class.java)
            startActivity(intent)
        }

        forgotYourPassword.setOnClickListener()
        {
            val intent = Intent(this@Login_Activity, Reset_password_1_Activity::class.java)
            startActivity(intent)
        }

    }
    private fun clickLogin()
    {
        if (validate())
        {
            val user = User( email.text.toString(), password.text.toString())
            services.login(user).enqueue(object : Callback<User>
            {
                override fun onResponse(call: Call<User>, response: Response<User>)
                {

                    if (response.isSuccessful)
                    {
                        val editor = preference.edit()
                        editor.putString(TOKEN, response.body()?.token)
                        editor.apply()
                        if (rememberMe.isChecked)
                        {
                            val editor = preference.edit()
                            editor.putString(IS_REMEMBRED, "true")
                            editor.apply()
                        }
                        println("is remembered : ${IS_REMEMBRED}")
                        val intent = Intent(this@Login_Activity, DrawerActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if(response.code() == 400)
                    {
                        Toast.makeText(this@Login_Activity, "Wrong password", Toast.LENGTH_SHORT).show()
                    }
                    else if (response.code() ==402)
                    {
                        Toast.makeText(this@Login_Activity, "Your Email has not been verified. Please check your emails !", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this@Login_Activity, "This email address is not associated with any account. please check and try again!", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<User>, t: Throwable)
                {
                    Log.d("something went wrong ", t.message.toString())
                }
            })
        }
    }

    private fun validate():Boolean
    {
        var mail=true
        var pswd=true

        passwordError?.error =null
        mailError?.error =null

        if(email?.text!!.isEmpty())
        {
            mailError?.error="Please enter your e-mail !"
            mail=false
        }
        if(password?.text!!.isEmpty())
        {
            passwordError?.error="Please enter your password !"
            pswd=false
        }

        if (pswd===false || mail===false)
        {
            return false
        }
        return true
    }
}