package tn.esprit.front.viewmodels

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import tn.esprit.front.models.*

interface BabyViewModel {

    @Multipart
    @POST("/user/baby/addBaby")
    fun addBaby(
        @Part babyName: MultipartBody.Part,
        @Part birthday: MultipartBody.Part,
        @Part babyPic: MultipartBody.Part,
        @Part gender: MultipartBody.Part,
        @Part("token") token: String,
    ) : Call<Baby>

    @POST("user/baby/getbabylist")
    fun getBabyList(@Body map : HashMap<String, String> ) : Call<MutableList<Baby>>

    @POST("/user/baby/addHeight")
    fun addHeight(@Body map : HashMap<String, String>) : Call<Height>

    @POST("user/baby/getbabyheights")
    fun getBabyHeights(@Body map : HashMap<String, String>) : Call<MutableList<Height>>

    @POST("/user/baby/addWeight")
    fun addWeight(@Body map : HashMap<String, String>) : Call<Weight>

    @POST("user/baby/getbabyweights")
    fun getBabyWeights(@Body map : HashMap<String, String>) : Call<MutableList<Weight>>

    @POST("/user/baby/addVaccine")
    fun addVaccine(@Body map : HashMap<String, String>) : Call<Vaccine>

    @POST("user/baby/getbabyvaccines")
    fun getBabyVaccines(@Body map : HashMap<String, String>) : Call<MutableList<Vaccine>>

    @POST("/user/baby/addConsultation")
    fun addConsultation(@Body map : HashMap<String, String>) : Call<Consultation>

    @POST("user/baby/getDoctorConsultations")
    fun getDoctorConsultations(@Body map : HashMap<String, String>) : Call<MutableList<Consultation>>

    @PUT("/user/baby/deleteBaby")
    fun deleteBaby(@Body hashMap: HashMap<String, String>):Call<Baby>


    @PUT("/user/baby/deleteConsultation")
    fun deleteConsultation(@Body hashMap: HashMap<String, String>):Call<Consultation>

    @PUT("/user/baby/deleteHeight")
    fun deleteHeight(@Body hashMap: HashMap<String, String>):Call<Height>

    @PUT("/user/baby/deleteWeight")
    fun deleteWeight(@Body hashMap: HashMap<String, String>):Call<Weight>

    @PUT("/user/baby/deleteVaccine")
    fun deleteVaccine(@Body hashMap: HashMap<String, String>):Call<Vaccine>

    companion object {

        fun create() : BabyViewModel {

            println("Baby_API_Interface")
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://web-production-d88c.up.railway.app/")
                .build()


            return retrofit.create(BabyViewModel::class.java)
        }
    }
}