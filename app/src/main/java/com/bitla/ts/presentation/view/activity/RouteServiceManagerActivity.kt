package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityRouteServiceManagerBinding
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.view.fragments.AdditionalInfoFragment
import com.bitla.ts.presentation.view.fragments.EditRouteViaCitiesFragment
import com.bitla.ts.presentation.view.fragments.MultiCityBookingFragment
import com.bitla.ts.presentation.view.fragments.SearchRouteFragment
import com.bitla.ts.utils.common.edgeToEdge
import gone
import visible


class RouteServiceManagerActivity : BaseActivity() {


    private lateinit var binding: ActivityRouteServiceManagerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun isInternetOnCallApisAndInitUI() {}

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setupActionBarWithNavController(findNavController(R.id.my_nav_host_fragment))

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityRouteServiceManagerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        showProgressDialog()

        binding.buttonTV.setOnClickListener {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as? NavHostFragment
            val currentFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment

            when (binding.buttonTV.text) {
                getString(R.string.update_all_) -> (currentFragment as? MultiCityBookingFragment)?.updateAllFare()
                getString(R.string.add_via_city_) -> (currentFragment as? EditRouteViaCitiesFragment)?.addViaCities()
                getString(R.string.route_review_) -> (currentFragment as? AdditionalInfoFragment)?.routeReview()
                getString(R.string.create_new_) -> {
                    (currentFragment as? SearchRouteFragment)?.createNewService(false)

                }
            }
        }

        binding.backIV.setOnClickListener {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as? NavHostFragment
            val currentFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment
            when (currentFragment) {
                is SearchRouteFragment -> {
                    val intent = Intent(this, DashboardNavigateActivity::class.java)
                    startActivity(intent)
                    this.finish()
                }

                is EditRouteViaCitiesFragment -> {
                    currentFragment.navigateBack()
                }

                else -> {
                    this.findNavController(R.id.my_nav_host_fragment).navigateUp()
                }
            }
        }
    }

    fun updateToolbar(fragment: String, title: String = "", subTitle: String = "") {
        if (::binding.isInitialized) {
            val drawable =
                resources.getDrawable(R.drawable.bg_white_little_round_color_primary, null)
            when (fragment) {
                getString(R.string.search_route)-> {
                    binding.titleTV.text = getString(R.string.service_manager)
                    binding.secondTV.gone()
                    binding.buttonTV.visible()
                    binding.buttonTV.text = getString(R.string.create_new)
                    binding.buttonTV.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.colorPrimary
                        )
                    )
                    binding.buttonTV.background = drawable
                }

                getString(R.string.edit_route_basic_details) -> {
                    if (title.isBlank()) {
                        binding.titleTV.text = getString(R.string.new_service)
                    } else {
                        binding.titleTV.text = title
                    }
                    binding.secondTV.visible()
                    binding.secondTV.text = subTitle
                    binding.buttonTV.gone()
                }

                getString(R.string.edit_route_via_cities) -> {
                    binding.titleTV.text = title
                    binding.buttonTV.visible()
                    binding.buttonTV.text = getString(R.string.add_via_city_)
                    binding.buttonTV.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.colorPrimary
                        )
                    )
                    binding.buttonTV.background = drawable
                }

                getString(R.string.via_city_booking_and_fare) -> {
                    binding.titleTV.text =
                        getString(R.string.via_city_booking_and_fare)
                    binding.secondTV.text = subTitle
                    binding.buttonTV.visible()
                    binding.buttonTV.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.colorPrimary
                        )
                    )
                    binding.buttonTV.text =
                        getString(R.string.update_all)
                    binding.buttonTV.background = null
                }

                getString(R.string.configure_bp_dp) -> {
                    binding.titleTV.text = getString(R.string.configure_bp_dp)
                    binding.buttonTV.gone()
                }

                getString(R.string.additional_info) -> {
                    binding.titleTV.text = getString(R.string.additional_info)
                    binding.buttonTV.visible()
                    binding.buttonTV.text = getString(R.string.route_review)
                    binding.buttonTV.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
                }

                getString(R.string.route_review) -> {
                    binding.titleTV.text = getString(R.string.route_review)
                    binding.buttonTV.gone()
                    binding.buttonTV.text = getString(R.string.route_review)
                }

                else -> {
                    binding.titleTV.gone()
                    binding.secondTV.gone()
                }
            }
        }
    }


    fun showProgressDialog() {
        if (::binding.isInitialized) {
            binding.progressPB.visible()
        }
    }

    fun hideProgressDialog() {
        binding.progressPB.gone()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.my_nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }

}