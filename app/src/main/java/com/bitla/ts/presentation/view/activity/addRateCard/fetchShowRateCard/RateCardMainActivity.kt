package com.bitla.ts.presentation.view.activity.addRateCard.fetchShowRateCard

import android.annotation.*
import android.content.*
import android.os.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.add_rate_card.deleteRateCard.request.*
import com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.request.*
import com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.response.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.view.activity.addRateCard.createRateCard.*
import com.bitla.ts.presentation.view.activity.addRateCard.editRateCard.*
import com.bitla.ts.presentation.view.activity.addRateCard.viewRateCard.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible

class RateCardMainActivity : BaseActivity(), OnMenuItemClickListener {

    private lateinit var binding: ActivityAddRateCardMainBinding
    private var loginModelPref: LoginModel = LoginModel()
    private var bccId: Int? = 0
    private var locale: String? = ""
    private var resID: String? = null
    private var routeId: String? = null
    private var sourceId: String? = null
    private var source: String = ""
    private var destinationId: String? = null
    private var destination: String = ""
    private var busType: String? = null
    private var adapter: FetchShowRateCardAdapter?= null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val addRateCardViewModel by viewModel<AddRateCardViewModel<Any?>>()
    private lateinit var routeWiseRateCardDetailList: MutableList<RouteWiseRateCardDetail>
    
    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun onResume() {
        super.onResume()
        callFetchShowRateCardApi()

    }
    override fun initUI() {
        binding = ActivityAddRateCardMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 15+ (API 34)
//            edgeToEdge(binding.root)
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val ime = insets.getInsets(WindowInsetsCompat.Type.ime()) // 👈 keyboard

                val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

                view.setPadding(
                    systemBars.left,
                    systemBars.top, // status bar handled visually
                    systemBars.right,
                    if (isKeyboardVisible) ime.bottom else systemBars.bottom
                )
                insets
            }
        }

        getPref()
        lifecycleScope.launch {
            addRateCardViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                  showToast(it)
                }
            }
        }
        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }



        DialogUtils.showProgressDialog(this)
        setCreateFareTemplateApiObserver()
        setDeleteRateCardApiObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()

        routeId = intent.getStringExtra(getString(R.string.updateRateCard_routeId)) ?:""

        PreferenceUtils.apply {
            resID = getString(getString(R.string.updateRateCard_resId))
            source = getString(getString(R.string.updateRateCard_origin)).toString()
            destination = getString(getString(R.string.updateRateCard_destination)).toString()
            sourceId = getString(getString(R.string.updateRateCard_originId)).toString()
            destinationId = getString(getString(R.string.updateRateCard_destinationId)).toString()
            busType = getString(getString(R.string.updateRateCard_busType))
        }
        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.rate_card)
        binding.updateRatecardToolbar.headerTitleDesc.text = busType
    }

    private fun callFetchShowRateCardApi() {

        if (isNetworkAvailable()) {
            addRateCardViewModel.fetchShowRateCardApi(
                FetchShowRateCardReqBody(
                    apiKey = loginModelPref.api_key,
                    routeId = routeId.toString(),
                    locale = locale ?: "en"
                ),
            )
        } else noNetworkToast()
    }

    private fun setCreateFareTemplateApiObserver() {
        addRateCardViewModel.fetchShowRateCardResponse.observe(this) {
            DialogUtils.dismissProgressDialog()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        routeWiseRateCardDetailList = it.routeWiseRateCardDetails
                        setFetchShowRateCardAdapter(it.routeWiseRateCardDetails)
                        binding.noData.gone()
                        binding.tvNoService.gone()
                    }
                    else -> {
                        binding.noData.visible()
                        binding.tvNoService.visible()
                        if (it.message?.isNotEmpty() == true){
                            toast(it.message)
                            binding.tvNoService.text = it.message
                        } else {
                            toast(getString(R.string.something_went_wrong))
                            binding.tvNoService.text = getString(R.string.something_went_wrong)
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun callDeleteRateCardApi(rateCardId: String) {

        if (isNetworkAvailable()) {
            addRateCardViewModel.deleteRateCardApi(
                DeleteRateCardReqBody(
                    apiKey = loginModelPref.api_key,
                    rateCardId = rateCardId,
                    locale = locale ?: "en"
                ),
            )
        } else noNetworkToast()
    }

    private fun setDeleteRateCardApiObserver() {
        addRateCardViewModel.deleteRateCardResponse.observe(this) {
            DialogUtils.dismissProgressDialog()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        it.result.message?.let { it1 ->
                            DialogUtils.successfulMsgDialog(
                                this, it1
                            )
                        }
                        callFetchShowRateCardApi()
                    }
                    else -> {
                        toast(it.result.message)
                    }
                }
            }
        }
    }


    private fun setFetchShowRateCardAdapter(routeWiseRateCardDetailList: MutableList<RouteWiseRateCardDetail>) {
        layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvFetchShowRateCard.layoutManager = layoutManager
        adapter = FetchShowRateCardAdapter(
            context = this,
            routeWiseRateCardDetailList = routeWiseRateCardDetailList,
            onMenuItemClickListener = this
        )
        binding.rvFetchShowRateCard.adapter = adapter
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, label: String) {
        Timber.d("rateCardIdAndPosition = ${routeWiseRateCardDetailList[itemPosition].rateCardId} and pos: $itemPosition")

        when(menuPosition){

            0 -> {
                val intent = Intent(this, ViewCreatedRateCardActivity::class.java)
                intent.putExtra(getString(R.string.create_rate_card), false)
                intent.putExtra(getString(R.string.rate_card_id), routeWiseRateCardDetailList[itemPosition].rateCardId)
                startActivity(intent)
            }
            1 -> {
                DialogUtils.showRateCardBottomSheet(
                    this,
                    itemPosition,
                    routeWiseRateCardDetailList,
                )
            }

            3 -> {

                DialogUtils.deleteRateCardDialog(
                    "${getString(R.string.delete_rate_card_dialog_msg_start)} $label?\n\n${getString(R.string.delete_rate_card_dialog_msg_end)}",
                    this,
                    onLeftButtonClick = {},
                    onRightButtonClick = {
                        DialogUtils.showProgressDialog(this)
                        callDeleteRateCardApi(routeWiseRateCardDetailList[itemPosition].rateCardId)
                    }
                )
            }
        }
    }

}

