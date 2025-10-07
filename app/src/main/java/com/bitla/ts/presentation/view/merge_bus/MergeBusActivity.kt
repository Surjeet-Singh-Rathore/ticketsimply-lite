package com.bitla.ts.presentation.view.merge_bus


import android.os.Build
import androidx.navigation.fragment.NavHostFragment
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityMergeBusBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class MergeBusActivity : BaseActivity() {

    private lateinit var binding: ActivityMergeBusBinding
    private var loginModel = LoginModel()
    private var navHostFragment: NavHostFragment? = null


    override fun initUI() {
        binding = ActivityMergeBusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()
        //callServiceApi()
        setUpNavController()
        //setUpObserver()

    }

    private fun setUpNavController() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment!!.navController

        val navGraph = navController.navInflater.inflate(R.navigation.merge_bus_navigation)
        navGraph.setStartDestination(R.id.mergeCoachFragment)

        navController.setGraph(navGraph, null)
    }
    override fun isInternetOnCallApisAndInitUI() {

    }

    private fun getPref() {
        loginModel = PreferenceUtils.getLogin()
    }

}