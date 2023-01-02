package tn.esprit.front.Views.Activities.Signin

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

class Reset_password_3_Activity : AppCompatActivity()
{
    lateinit var verifyButton: Button
    lateinit var code: TextInputEditText
    lateinit var codeError: TextInputLayout

    var services = UserViewModel.create()
    lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password3)

        verifyButton = findViewById(R.id.reset_password)
        code = findViewById(R.id.new_password_text)
        codeError = findViewById(R.id.new_password)

        verifyButton.setOnClickListener {
            if (validate())
            {
                resetPassword()
            }
        }
    }

    private fun resetPassword()
    {
        val sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
        val user = User(email = email.toString(), password = code.text.toString())
        services.resetPassword(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@Reset_password_3_Activity, Login_Activity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Reset_password_3_Activity, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@Reset_password_3_Activity, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun validate(): Boolean
    {
        var valid = true
        val password = code.text.toString()
        if (password.isEmpty())
        {
            codeError.error = "Please enter a password"
            valid = false
        }
        else
        {
            codeError.error = null
        }
        return valid
    }
}