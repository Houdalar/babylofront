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
import tn.esprit.front.models.Consultation
import tn.esprit.front.models.Height
import tn.esprit.front.viewmodels.BabyViewModel

class HeightAdapter(val heightList: MutableList<Height>) : RecyclerView.Adapter<HeightAdapter.HeightViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeightViewHolder {
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.add_height_item,parent,false)
        return HeightViewHolder(view)
    }


    override fun onBindViewHolder(holder: HeightViewHolder, position: Int) {
        val hei= heightList.get(position)
        holder.height.text=hei.height.toString()
        holder.date.text=hei.date

        var mSharedPreferences: SharedPreferences = holder.itemView.context.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE)

        holder.itemView.setOnLongClickListener {
            val builder= AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete Height")
            builder.setMessage("Are you sure you want to remove this height ?")
            builder.setPositiveButton("Yes"){dialog, which ->
                val service= BabyViewModel.create()
                val map:HashMap<String,String> = HashMap()
                map.put("date", heightList[position].date.toString())
                map["token"]=mSharedPreferences.getString(TOKEN,"").toString()
                service.deleteHeight(map).enqueue(object : Callback<Height> {
                    override fun onResponse(call: Call<Height>, response: Response<Height>) {
                        if(response.isSuccessful){
                            Toast.makeText(holder.itemView.context,"Height deleted successfully",
                                Toast.LENGTH_LONG).show()
                            notifyDataSetChanged()
                            notifyItemRangeChanged(holder.adapterPosition, heightList.size)
                        }
                    }
                    override fun onFailure(call: Call<Height>, t: Throwable) {
                        Toast.makeText(holder.itemView.context, "Failed to remove", Toast.LENGTH_SHORT).show()
                    }

                })
                heightList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,heightList.size)
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


    override fun getItemCount(): Int =  heightList.size




    class HeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val height=itemView.findViewById<TextView>(R.id.height)
        val date=itemView.findViewById<TextView>(R.id.dateHeight)


    }


}