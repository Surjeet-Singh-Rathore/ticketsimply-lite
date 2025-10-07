package com.bitla.ts.presentation.view.activity


import android.os.Build
import android.os.Bundle
import android.util.Log
import org.koin.androidx.viewmodel.ext.android.*
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityCoachViewSelectionBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.update_coach_type.request.UpdateCoachTypeRequest
import com.bitla.ts.presentation.viewModel.UpdateCoachTypeViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.constants.COACH_LAYOUT_SELECTION
import com.bitla.ts.utils.constants.LOGIN_ID
import com.bitla.ts.utils.constants.OPERATOR_NAME
import com.bitla.ts.utils.constants.ROLE_NAME
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import gone
import toast

class CoachViewSelectionActivity : BaseActivity() {

    private lateinit var binding: ActivityCoachViewSelectionBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var selectedCoachLayoutType = ""
    private var loginModelPref: LoginModel = LoginModel()
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private val updateCoachTypeViewModel by viewModel<UpdateCoachTypeViewModel<Any?>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        getPref()
        observeViewModel()
    }

    override fun isInternetOnCallApisAndInitUI() {
        initUI()
    }

    override fun initUI() {
        binding = ActivityCoachViewSelectionBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        loginModelPref = PreferenceUtils.getLogin()
        firebaseAnalytics = Firebase.analytics


        binding.layoutToolbar.tvCurrentHeader.text = getString(R.string.coach_layout)
        binding.layoutToolbar.imgBack.setOnClickListener{
            super.onBackPressed()
        }
        val coachPref=  PreferenceUtils.getPreference("COACH_VIEW_SELECTION", "SingleViewSelected")
        if (coachPref== "SingleViewSelected"){
            binding.singleView.isChecked= true
            binding.splitView.isChecked= false
            binding.webView.isChecked= false
        }
        else if (coachPref== "SplitViewSelected" ){
            binding.splitView.isChecked= true
            binding.singleView.isChecked= false
            binding.webView.isChecked= false

        }else if (coachPref== "WebViewSelected"){
            binding.webView.isChecked= true
            binding.singleView.isChecked= false
            binding.splitView.isChecked= false
        }
        binding.splitView.setOnClickListener {
            PreferenceUtils.setPreference("COACH_VIEW_SELECTION", "SplitViewSelected")
            updateCoachTypeViewModel.updateCoachTypeApi(UpdateCoachTypeRequest(loginModelPref.api_key, true, "split"))

            selectedCoachLayoutType = "Split View"
            binding.webView.isChecked= false
            binding.singleView.isChecked= false
            binding.splitView.isChecked= true


            logFeatureSelectedEvent(
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                selectedCoachLayoutType
            )

        }
        binding.singleView.setOnClickListener {
            PreferenceUtils.setPreference("COACH_VIEW_SELECTION", "SingleViewSelected")
            updateCoachTypeViewModel.updateCoachTypeApi(UpdateCoachTypeRequest(loginModelPref.api_key, true, "single"))

            selectedCoachLayoutType = "Single View"
            binding.webView.isChecked= false
            binding.singleView.isChecked= true
            binding.splitView.isChecked= false


            logFeatureSelectedEvent(
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                selectedCoachLayoutType
            )

        }
        binding.webView.setOnClickListener {
            PreferenceUtils.setPreference("COACH_VIEW_SELECTION", "WebViewSelected")
            updateCoachTypeViewModel.updateCoachTypeApi(UpdateCoachTypeRequest(loginModelPref.api_key, true, "web"))

            selectedCoachLayoutType = "Web View"
            binding.singleView.isChecked= false
            binding.splitView.isChecked= false
            binding.webView.isChecked= true


            logFeatureSelectedEvent(
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                selectedCoachLayoutType
            )

        }
        binding.btnSelectView.gone()
    }

    private fun logFeatureSelectedEvent(
        loginId: String?,
        operatorName: String?,
        roleName: String?,
        selectedCoachLayoutType : String) {

        firebaseAnalytics.logEvent("coach_layout_selection") {
            param(LOGIN_ID, loginId.toString())
            param(OPERATOR_NAME, operatorName.toString())
            param(ROLE_NAME, roleName.toString())
            param(COACH_LAYOUT_SELECTION, selectedCoachLayoutType)
        }
    }

    private fun observeViewModel() {
        updateCoachTypeViewModel.updateCoachType.observe(this) { response ->
            if(response.code == 200) {
                toast(response.message)
            }
            else
                toast(response.message)
        }
    }

    private fun getPref() {
        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase()
        }
    }

}