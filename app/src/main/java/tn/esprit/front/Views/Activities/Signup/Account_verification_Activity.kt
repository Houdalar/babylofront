package tn.esprit.front.Views.Activities.Signup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signin.Login_Activity
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.models.User
import tn.esprit.front.viewmodels.UserViewModel

class Account_verification_Activity : AppCompatActivity() {
    lateinit var resendEmail: TextView
    lateinit var verifyButton: Button
    lateinit var code: TextInputEditText
    lateinit var codeError: TextInputLayout
    var services = UserViewModel.create()
    lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_verification)

        resendEmail = findViewById(R.id.resend_email_button)
        verifyButton = findViewById(R.id.verify_button)

        code = findViewById(R.id.code)
        codeError = findViewById(R.id.activation_code)

        preference=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        verifyButton.setOnClickListener {
            verifyAccount()
        }

        resendEmail.setOnClickListener {
            if (validate())
            {
                resendEmail()
            }
        }



    }

    private fun resendEmail() {
        val sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
        val user = User( email.toString())
        services.resendCode(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@Account_verification_Activity,
                        "code sent",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@Account_verification_Activity, "Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@Account_verification_Activity, "something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun verifyAccount() {
        val editor = preference.edit()
        val mail = preference.getString("email", "")
        editor.apply()
        if(validate()){
            val user = User(mail.toString(), null ,null,null,code.text.toString())
            services.activateAccount(user).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@Account_verification_Activity,
                            "Account verified",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@Account_verification_Activity, Login_Activity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Account_verification_Activity, "please verify the code", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@Account_verification_Activity, "something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }}


    private fun validate():Boolean
    {
        codeError?.error =null
        if (code.toString().isEmpty())
        {
            codeError?.error = "Please enter the verification code"
            return false
        }

        return true
    }
}
