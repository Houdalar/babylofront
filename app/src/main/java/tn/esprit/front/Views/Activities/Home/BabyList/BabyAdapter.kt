package tn.esprit.front.Views.Activities.Home.BabyList

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Profile.ProfileActivity
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.Baby
import tn.esprit.front.viewmodels.BabyViewModel

class BabyAdapter(val babyList: MutableList<Baby>):RecyclerView.Adapter<BabyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BabyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.baby_item, parent, false)

        return BabyViewHolder(view)
    }

    override fun onBindViewHolder(holder: BabyViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val babyName = babyList[position].babyName
        val pic=babyList[position].babyPic

        Glide.with(holder.itemView.context).load(pic).into(holder.babyPic)
        holder.babyName.text = babyName

        var mSharedPreferences: SharedPreferences = holder.itemView.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProfileActivity::class.java)

            intent.apply {
                putExtra("BABYNAME", babyList[position].babyName)
                putExtra("DATE",babyList[position].birthday)
                putExtra("GENDER",babyList[position].gender)
                putExtra("BABYPIC",pic)
                putExtra("token",mSharedPreferences.getString(TOKEN,"").toString())
                Log.e("******************************************* Token *************************************",mSharedPreferences.getString(TOKEN,"").toString())
            }
            holder.itemView.context.startActivity(intent)

        }

        holder.itemView.setOnLongClickListener {
            val builder=AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete Baby")
            builder.setMessage("Are you sure you want to delete this baby ?")
            builder.setPositiveButton("Yes"){dialog, which ->
                val service=BabyViewModel.create()
                val map:HashMap<String,String> = HashMap()
                map.put("babyName", babyList[position].babyName.toString())
                map["token"]=mSharedPreferences.getString(TOKEN,"").toString()
                service.deleteBaby(map).enqueue(object : Callback<Baby> {
                    override fun onResponse(call: Call<Baby>, response: Response<Baby>) {
                        if(response.isSuccessful){
                            Snackbar.make(holder.itemView,"Baby deleted successfully",Snackbar.LENGTH_LONG).show()
                            notifyDataSetChanged()
                            notifyItemRangeChanged(holder.adapterPosition, babyList.size)
                        }
                    }
                    override fun onFailure(call: Call<Baby>, t: Throwable) {
                        Toast.makeText(holder.itemView.context, "Failed to remove", Toast.LENGTH_SHORT).show()
                    }

                })
                babyList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,babyList.size)
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
    override fun getItemCount() = babyList.size

}

