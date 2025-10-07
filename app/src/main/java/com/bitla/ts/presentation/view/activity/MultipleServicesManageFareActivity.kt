package com.bitla.ts.presentation.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityMultipleServicesManageFareBinding
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.view.fragments.MultipleServicesFareServiceListFragment
import com.bitla.ts.presentation.view.fragments.MultipleServicesManageFareFragment
import com.bitla.ts.utils.common.edgeToEdge
import gone
import visible

class MultipleServicesManageFareActivity : BaseActivity() {

    private lateinit var binding: ActivityMultipleServicesManageFareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initUI() {
        binding = ActivityMultipleServicesManageFareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        binding.imgBack.setOnClickListener {
            val currentFragment = getCurrentFragment()

            when (currentFragment) {
                is MultipleServicesFareServiceListFragment -> {
                    val intent = Intent(this, DashboardNavigateActivity::class.java)
                    startActivity(intent)
                    this.finish()
                }

                else -> {
                    this.findNavController(R.id.nav_host_fragment_multiple_services_fare)
                        .navigateUp()
                }
            }
        }
    }

    fun setToolbarText(headerText: String) {
        val currentFragment = getCurrentFragment()

        when (currentFragment) {
            is MultipleServicesFareServiceListFragment -> {
                if (::binding.isInitialized) {
                    binding.tvSrcDestDate.text = ""
                    binding.tvSrcDestDate.gone()
                }
            }

            is MultipleServicesManageFareFragment -> {
                if (::binding.isInitialized) {
                    binding.tvSrcDestDate.text = headerText
                    binding.tvSrcDestDate.visible()
                }
            }

            else -> {}
        }
    }

    private fun getCurrentFragment(): Fragment? {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_multiple_services_fare) as? NavHostFragment
        val currentFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment

        return currentFragment
    }

    override fun isInternetOnCallApisAndInitUI() {}
}