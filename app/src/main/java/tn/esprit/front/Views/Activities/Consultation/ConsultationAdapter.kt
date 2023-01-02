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
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.Baby
import tn.esprit.front.models.Consultation
import tn.esprit.front.viewmodels.BabyViewModel

class ConsultationAdapter(val consultationList: MutableList<Consultation>) : RecyclerView.Adapter<ConsultationAdapter.ConsultationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.consultation_item,parent,false)
        return ConsultationViewHolder(view)
    }


    override fun onBindViewHolder(holder: ConsultationViewHolder, position: Int) {
        val cons= consultationList.get(position)
        holder.doctor.text=cons.doctor.toString()
        holder.date.text=cons.date
        holder.time.text=cons.time

        var mSharedPreferences: SharedPreferences = holder.itemView.context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE)

        holder.itemView.setOnLongClickListener {
            val builder= AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete Doctor's appointment")
            builder.setMessage("Are you sure you want to delete this appointment ?")
            builder.setPositiveButton("Yes"){dialog, which ->
                val service= BabyViewModel.create()
                val map:HashMap<String,String> = HashMap()
                map.put("date", consultationList[position].date.toString())
                map.put("time", consultationList[position].time.toString())
                map["token"]=mSharedPreferences.getString(TOKEN,"").toString()
                service.deleteConsultation(map).enqueue(object : Callback<Consultation> {
                    override fun onResponse(call: Call<Consultation>, response: Response<Consultation>) {
                        if(response.isSuccessful){
                            Toast.makeText(holder.itemView.context,"Consultation deleted successfully",
                                Toast.LENGTH_LONG).show()
                            notifyDataSetChanged()
                            notifyItemRangeChanged(holder.adapterPosition, consultationList.size)
                        }
                    }
                    override fun onFailure(call: Call<Consultation>, t: Throwable) {
                        Toast.makeText(holder.itemView.context, "Failed to remove", Toast.LENGTH_SHORT).show()
                    }

                })
                consultationList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,consultationList.size)
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

    override fun getItemCount(): Int =  consultationList.size

    class ConsultationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val doctor=itemView.findViewById<TextView>(R.id.doctorName)
        val date=itemView.findViewById<TextView>(R.id.dateConsultation)
        val time=itemView.findViewById<TextView>(R.id.timeConsultation)


    }
}