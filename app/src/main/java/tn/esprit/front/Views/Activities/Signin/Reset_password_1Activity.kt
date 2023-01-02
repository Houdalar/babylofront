package tn.esprit.front.Views.Activities.Signin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.models.User
import tn.esprit.front.viewmodels.UserViewModel

class Reset_password_1_Activity : AppCompatActivity()
{
    lateinit var sendCode: Button
    lateinit var email: TextInputEditText
    lateinit var emailError: TextInputLayout

    var services = UserViewModel.create()
    lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password1)

        sendCode = findViewById(R.id.send_code_button)

        email = findViewById(R.id.code)
        emailError = findViewById(R.id.EmailTextInputLayout)

        preference=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


        sendCode.setOnClickListener{
            if (validate())
            {
                sendCode()

            }

        }
    }

    private fun validate(): Boolean
    {
        var valid = true
        if (email.text.toString().isEmpty())
        {
            emailError.error = "Please enter a valid email"
            valid = false
        } else
        {
            val user = User(email = email.text.toString())
            services.verifyEmail(user).enqueue(object : Callback<User>
            {
                override fun onResponse(call: Call<User>, response: Response<User>)
                {
                    if (response.code()==200)
                    {
                        emailError.error = null
                    }
                    else
                    {
                        emailError.error = "user doesn't exist"
                        valid = false
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable)
                {
                    emailError.error = "something went wrong, please try again!"
                    valid = false
                }
            })
        }
        return valid
    }
    private fun sendCode()
    {
        val user = User(email = email.text.toString())
        services.resetPasswordEmail(user).enqueue(object : Callback<User>
        {
            override fun onResponse(call: Call<User>, response: Response<User>)
            {
                if (response.code()==200) {
                    val editor = preference.edit()
                    editor.putString("email", email.text.toString())
                    editor.apply()
                    val intent = Intent(this@Reset_password_1_Activity, Reset_password_2_activity::class.java)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(this@Reset_password_1_Activity, "wrong email", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable)
            {
                Toast.makeText(this@Reset_password_1_Activity, "Error", Toast.LENGTH_SHORT).show()
            }
        })

    }

}
