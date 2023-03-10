package tn.esprit.front.Views.Activities.Height


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.databinding.WeightDialogBinding
import tn.esprit.front.models.Weight
import tn.esprit.front.viewmodels.BabyViewModel


class WeightDialog(
    val onSubmitClickListener: (String) -> Unit,
    val babyName: String,
    val token: String
):DialogFragment() {
    private lateinit var binding : WeightDialogBinding

    val services = BabyViewModel.create()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = WeightDialogBinding.inflate(LayoutInflater.from(context))
        Log.e("******************************************************************** Heigh Dialog ************************",babyName.toString())
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        binding.saveBtnW.setOnClickListener {
            onSubmitClickListener.invoke(binding.babyWeightTxt.text.toString())
            val map: HashMap<String, String> = HashMap()

            map["token"] = token
            map["weight"]=binding.babyWeightTxt.text.toString()
            map["babyName"]= babyName
            Log.e("token",token)
            Log.e("weight",binding.babyWeightTxt.text.toString())
            Log.e("babyName",babyName)
            services.addWeight(map).enqueue(object: Callback<Weight> {
                override fun onResponse(call: Call<Weight>, response: Response<Weight>) {
                    if(response.isSuccessful){
                        println(response.message())
                        dismiss()
                    }
                    else{
                        Toast.makeText(context, "Baby not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Weight>, t: Throwable) {
                    Toast.makeText(context, "Failed to add weight", Toast.LENGTH_SHORT).show()
                }

            })

        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }
}