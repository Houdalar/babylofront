package tn.esprit.front.Views.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Home.DrawerActivity
import tn.esprit.front.Views.Activities.Signin.IS_REMEMBRED
import tn.esprit.front.Views.Activities.Signin.Login_Activity
import tn.esprit.front.Views.Activities.Signin.PREF_NAME
import tn.esprit.front.Views.Activities.Signup.SignUp_Activity

class SplashScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler().postDelayed({
            if (mainViewPagerFinished()){
                findNavController().navigate(R.id.action_splashScreenFragment_to_login_Activity)

                // get shared preferences
                val sharedPref = activity?.getSharedPreferences(PREF_NAME, 0)

                var isRemembered = sharedPref?.getString(IS_REMEMBRED, "")

                println("isRemembered: $isRemembered")
//                var remembered=sharedPref!!.getBoolean("IS_REMEMBRED",false)
                //println("remembered : $remembered")

                if (isRemembered=="true"){
                    val intent = Intent(context, DrawerActivity::class.java)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(context, Login_Activity::class.java)
                    startActivity(intent)
                }
            }
            else{
                findNavController().navigate(R.id.action_splashScreenFragment_to_viewPagerFragment)
            }

        },
            3500)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    private fun mainViewPagerFinished():Boolean{
        val sharedPref=requireActivity().getSharedPreferences("mainViewPager", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished",false)
    }


}