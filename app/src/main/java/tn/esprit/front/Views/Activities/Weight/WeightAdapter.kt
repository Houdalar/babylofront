package tn.esprit.front.Views.Activities.Height

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
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
import tn.esprit.front.models.Height
import tn.esprit.front.models.Weight
import tn.esprit.front.viewmodels.BabyViewModel

class WeightAdapter(val weightList: MutableList<Weight>) : RecyclerView.Adapter<WeightAdapter.WeightViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.add_weight_item,parent,false)
        return WeightViewHolder(view)
    }


    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val wei= weightList.get(position)
        holder.weight.text=wei.weight.toString()
        holder.date.text=wei.date

        var mSharedPreferences: SharedPreferences = holder.itemView.context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE)

        holder.itemView.setOnLongClickListener {
            val builder= AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete Weight")
            builder.setMessage("Are you sure you want to remove this weight ?")
            builder.setPositiveButton("Yes"){dialog, which ->
                val service= BabyViewModel.create()
                val map:HashMap<String,String> = HashMap()
                map.put("date", weightList[position].date.toString())
                map["token"]=mSharedPreferences.getString(TOKEN,"").toString()
                service.deleteWeight(map).enqueue(object : Callback<Weight> {
                    override fun onResponse(call: Call<Weight>, response: Response<Weight>) {
                        if(response.isSuccessful){
                            Toast.makeText(holder.itemView.context,"Weight deleted successfully",
                                Toast.LENGTH_LONG).show()
                            notifyDataSetChanged()
                            notifyItemRangeChanged(holder.adapterPosition, weightList.size)
                        }
                    }
                    override fun onFailure(call: Call<Weight>, t: Throwable) {
                        Toast.makeText(holder.itemView.context, "Failed to remove", Toast.LENGTH_SHORT).show()
                    }

                })
                weightList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,weightList.size)
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


    override fun getItemCount(): Int =  weightList.size




    class WeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val weight=itemView.findViewById<TextView>(R.id.weight)
        val date=itemView.findViewById<TextView>(R.id.dateWeight)


    }

}