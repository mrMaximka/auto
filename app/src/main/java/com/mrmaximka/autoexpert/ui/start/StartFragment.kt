package com.mrmaximka.autoexpert.ui.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mrmaximka.autoexpert.MainActivity
import com.mrmaximka.autoexpert.R
import com.mrmaximka.autoexpert.model.UserModel
import com.mrmaximka.autoexpert.utils.NavigationUtils
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : Fragment() {

    private lateinit var viewModel: StartViewModel
    private lateinit var userModer: UserModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userModer = UserModel.instance
        setViewSettings()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StartViewModel::class.java)

        userModer.email.observe(this, Observer {
            it?.let {
                setUserSettings()
            }
        })

        viewModel.needStart.observe(this, Observer {
            it?.let {
                NavigationUtils.observerNavigation(
                    requireActivity(),
                    it,
                    viewModel.needStart,
                    R.id.action_startFragment_to_categoryFragment
                )
            }
        })
//        setUserSettings()
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
    private fun setViewSettings() {

        google_sign_in_btn.setOnClickListener { userModer.needToLogin.value = true }
        btn_exit.setOnClickListener { activity?.onBackPressed() }
        btn_start.setOnClickListener { onStartClick() }
    }

    private fun onStartClick() {
        viewModel.onStartClick()
    }

    private fun setUserSettings(){
        if (userModer.email.value!!.isNotEmpty()){
            google_sign_in_btn.visibility = View.GONE
            val name = userModer.getName()
            if (name.isNotEmpty()) start_welcome.text = "Здравствуйте, $name"
            start_welcome.visibility = View.VISIBLE
        }else{
            google_sign_in_btn.visibility = View.VISIBLE
            start_welcome.visibility = View.GONE
        }
    }

}
