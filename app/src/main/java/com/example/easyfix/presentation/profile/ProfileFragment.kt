package com.example.easyfix.presentation.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.easyfix.R
import com.example.easyfix.base.BaseFragment
import kotlinx.android.synthetic.main.frament_profile.*


class ProfileFragment : BaseFragment(R.layout.frament_profile) {
    private lateinit var viewModel: ProfileViewModel
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val  userId=context?.getSharedPreferences("file",
        AppCompatActivity.MODE_PRIVATE)?.getInt("id",0)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.profile(userId)

        viewModel.profileModel.observe(this, { profile ->
            tv_username.text = profile.name.toString()
            tv_full_name.text = profile?.name.toString()
            tv_phone.text = profile?.telephone.toString()
        })



    }


}