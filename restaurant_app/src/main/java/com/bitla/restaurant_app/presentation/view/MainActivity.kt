package com.bitla.restaurant_app.presentation.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bitla.restaurant_app.R
import com.bitla.restaurant_app.databinding.ActivityMainBinding
import com.bitla.restaurant_app.databinding.DialogUnauthorizedBinding
import com.bitla.restaurant_app.presentation.pojo.LoginModel
import com.bitla.restaurant_app.presentation.utils.PreferenceUtils
import com.bitla.restaurant_app.presentation.utils.gone
import com.bitla.restaurant_app.presentation.utils.isNetworkAvailable
import com.bitla.restaurant_app.presentation.utils.toast
import com.bitla.restaurant_app.presentation.utils.visible
import com.bitla.restaurant_app.presentation.viewModel.MainViewModel
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.Gson

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private var binding: ActivityMainBinding? = null

    private val viewModel by viewModels<MainViewModel>()

    private var currentUser: LoginModel? = null

    private var drawerLayout: DrawerLayout? = null
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)



        getCurrentUserData()
        setDrawerData()
        setToolBarTitle()

        setSupportActionBar(binding?.toolbar)

        // Initialize DrawerLayout and ActionBarDrawerToggle
        drawerLayout = binding?.drawerLayout
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding?.toolbar,
            R.string.nav_open,
            R.string.nav_close
        )

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout?.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle?.syncState()

//        viewModel.getMealCouponDetailsApi("","KH175366","")


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        binding?.bottomNavBar?.setupWithNavController(navController)

//        binding?.bottomNavBar?.setOnItemSelectedListener(this)

        binding?.tvLogout?.setOnClickListener { logout() }
        binding?.imageLogout?.setOnClickListener { logout() }
        setObserver()
    }

    fun unAuthorizedDialog(
        context: Context,
        message: String
    ) {

        val builder = AlertDialog.Builder(context).create()
        val binding: DialogUnauthorizedBinding =
            DialogUnauthorizedBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)
        binding.tvContent.text = message
        binding.btnOk.tag = context.getString(R.string.unauthorized)
        binding.btnOk.setOnClickListener {
            clearDataAndFinish()
        }
        builder.setView(binding.root)
        builder.show()
    }

    private fun getDeviceUniqueId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun setObserver() {
        viewModel.logoutUserResponse.observe(this, Observer {
            if (it != null) {
                if (it.code == 200) {
                    toast(getString(R.string.logout_successful))
                    clearDataAndFinish()
                }
            } else {
                this.toast(getString(R.string.server_error))
            }
        })
    }

    private fun clearDataAndFinish() {
        PreferenceUtils.clear()
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun logout() {
        if (isNetworkAvailable()) {
            viewModel.logoutApi(
                currentUser?.api_key ?: "",
                getDeviceUniqueId(this)
            )
        } else {
            toast(getString(R.string.network_not_available))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment

        if (navHostFragment.childFragmentManager.backStackEntryCount == 0) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }


    private fun getCurrentUserData() {
        currentUser = PreferenceUtils.getLogin()
    }

    private fun setDrawerData() {
        setOperatorLogo(currentUser?.logo_url.toString())
        binding?.tvShortTitle?.text = currentUser?.name?.uppercase()?.substring(0, 2)
        binding?.navTextUserName?.text = currentUser?.name.toString()
        binding?.tvPosition?.text = currentUser?.role.toString()
        binding?.tvTravelsName?.text = currentUser?.travels_name.toString()

        getAppVersion()
    }

    fun setToolBarTitle() {
        val title = currentUser?.name.toString()
        showToolBar("${getString(R.string.hello)} $title")
    }

    private fun setOperatorLogo(logoURL: String) {
        Glide.with(this).load(logoURL).fitCenter().into(binding!!.operatorLogoIV)
    }

    private fun getAppVersion() {
        val version = intent.getStringExtra("version")
        binding?.tvVersion?.text = "${getString(R.string.version)} $version"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }


    fun hideToolBar() {
        binding?.appBarLayout?.gone()
    }

    fun showToolBar(title: String) {
        binding?.appBarLayout?.visible()
        binding?.tvToolBarText?.text = title
    }


    fun hideBottomBar() {
        binding?.bottomNavBar?.gone()
    }

    fun showBottomBar() {
        binding?.bottomNavBar?.visible()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return true
    }


}
