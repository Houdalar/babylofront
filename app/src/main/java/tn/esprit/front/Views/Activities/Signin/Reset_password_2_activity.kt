package tn.esprit.front.Views.Activities.Signin

import android.content.Intent
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

class Reset_password_2_activity : AppCompatActivity()
{
    lateinit var verify: Button
    lateinit var code: TextInputEditText
    lateinit var codeError: TextInputLayout
    var services = UserViewModel.create()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password2)

        code = findViewById(R.id.code)
        codeError = findViewById(R.id.reset_password_code)

        verify = findViewById(R.id.verify_button)


        verify.setOnClickListener {
            if (validate())
            {
                verify()
            }
        }
    }
    private fun verify()
    {
        val sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val mail = sharedPreferences.getString("email", "")
        val user = User(email = mail.toString(), token = code.text.toString())
        services.verifyCode(user).enqueue(object : Callback<User>
        {
            override fun onResponse(call: Call<User>, response: Response<User>)
            {
                if (response.isSuccessful)
                {
                    val editor = sharedPreferences.edit()
                    editor.putString("email", mail.toString())
                    editor.apply()
                    val intent = Intent(this@Reset_password_2_activity, Reset_password_3_Activity::class.java)
                    startActivity(intent)
                } else
                {
                    Toast.makeText(this@Reset_password_2_activity, "Invalid code", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable)
            {
                Toast.makeText(this@Reset_password_2_activity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun validate(): Boolean
    {
        var valid = true
        val code = code.text.toString()
        if (code.isEmpty())
        {
            codeError.error = "Please the  enter code"
            valid = false
        }
        else
        {
            codeError.error = null
        }
        return valid
    }
}