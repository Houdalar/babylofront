package tn.esprit.front.Views.Activities.Vaccins

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Height.HeightAdapter
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.Consultation
import tn.esprit.front.models.Height
import tn.esprit.front.models.Vaccine
import tn.esprit.front.viewmodels.BabyViewModel

class VaccineAdapter(val vaccineList: MutableList<Vaccine>) : RecyclerView.Adapter<VaccineAdapter.VaccineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccineViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.vaccin_item,parent,false)
        return VaccineViewHolder(view)
    }


    override fun onBindViewHolder(holder: VaccineViewHolder, position: Int) {
        val vacc= vaccineList.get(position)
        holder.vaccine.text=vacc.vaccine.toString()
        holder.date.text=vacc.date

        var mSharedPreferences: SharedPreferences = holder.itemView.context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE)

        holder.itemView.setOnLongClickListener {
            val builder= AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete Vaccine")
            builder.setMessage("Are you sure you want to delete this vaccine ?")
            builder.setPositiveButton("Yes"){dialog, which ->
                val service= BabyViewModel.create()
                val map:HashMap<String,String> = HashMap()
                map.put("date", vaccineList[position].date.toString())
                map.put("vaccine", vaccineList[position].vaccine.toString())
                map["token"]=mSharedPreferences.getString(TOKEN,"").toString()
                service.deleteVaccine(map).enqueue(object : Callback<Vaccine> {
                    override fun onResponse(call: Call<Vaccine>, response: Response<Vaccine>) {
                        if(response.isSuccessful){
                            Toast.makeText(holder.itemView.context,"Vaccine deleted successfully",
                                Toast.LENGTH_LONG).show()
                            notifyDataSetChanged()
                            notifyItemRangeChanged(holder.adapterPosition, vaccineList.size)
                        }
                    }
                    override fun onFailure(call: Call<Vaccine>, t: Throwable) {
                        Toast.makeText(holder.itemView.context, "Failed to remove", Toast.LENGTH_SHORT).show()
                    }

                })
                vaccineList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,vaccineList.size)
            }
            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            true
            return@setOnLongClickListener true
        }
    }


    override fun getItemCount(): Int =  vaccineList.size




    class VaccineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val vaccine=itemView.findViewById<TextView>(R.id.vaccine)
        val date=itemView.findViewById<TextView>(R.id.dateVaccine)


    }
}