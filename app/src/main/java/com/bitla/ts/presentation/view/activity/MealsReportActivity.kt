package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogButtonAnyDataListener


import com.bitla.ts.databinding.ActivityMealsReportBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.occupany_report_data.PagenationData
import com.bitla.ts.domain.pojo.reports.ReportsResponse
import com.bitla.ts.presentation.adapter.MealReportDateAdapter
import com.bitla.ts.presentation.adapter.PagenationNumberAdapter
import com.bitla.ts.presentation.viewModel.RestaurantViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getDateDMY
import com.bitla.ts.utils.constants.Pagination
import com.bitla.ts.utils.constants.STORAGE_PERMISSION_CODE
import com.bitla.ts.utils.downloadPdf.DownloadPdf
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.gson.Gson
import isNetworkAvailable
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast


class MealsReportActivity : BaseActivity(), DialogButtonAnyDataListener {
    private lateinit var binding: ActivityMealsReportBinding
    private var reportsData : ReportsResponse?=null
    private var currency=""
    private var currencyFormat=""
    private var pageAdapter: PagenationNumberAdapter?= null
    private var pagenationList : ArrayList<PagenationData> = arrayListOf()
    private var lastPos: Int = 0
    private val viewModel by viewModel<RestaurantViewModel>()
    private var fromDate=""
    private var toDate=""
    override fun initUI() {}


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealsReportBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }


        intent?.let {
            if(it.hasExtra("reportsData")){
                val data = intent.getStringExtra("reportsData")
                if (!data.isNullOrEmpty()) {
                    reportsData = Gson().fromJson(data, ReportsResponse::class.java)}
                fromDate=it.getStringExtra("fromDate").toString()
                toDate=it.getStringExtra("toDate").toString()
                viewModel.serviceId=it.getStringExtra("serviceId").toString()
                if(viewModel.serviceId.isEmpty()){
                    viewModel.serviceId="-1"
                }
                viewModel.restaurantId=it.getStringExtra("restaurantId").toString()

                setPaginationList()

                setHeader()
            }
        }


        getCurrencyData()

        setData()

        setPaginationAdapter()

        setClickListeners()

        setReportObserver()

    }

    override fun isInternetOnCallApisAndInitUI() {}

    @SuppressLint("SetTextI18n")
    private fun setHeader() {
        binding.textView2.text = "${getDateDMY(fromDate)} to ${getDateDMY(toDate)}"
    }


    private fun setPaginationList() {
        pagenationList.clear()
        val nosOfpage = reportsData?.numberOfPages ?: 0
        for (i in 0 until nosOfpage) {
            val obj = PagenationData()
            obj.position = i
            obj.isSelected = false
            pagenationList.add(obj)
        }
    }





    private fun setPaginationAdapter() {
        if (pagenationList.size > 1) {
            binding.bottomV.visibility = View.VISIBLE
            pagenationList[0].isSelected = true
            setPageAdapter()
        } else {
            binding.bottomV.visibility = View.GONE
        }
    }

    private fun setClickListeners() {
        binding.showHideDetails.setOnClickListener {
            if (binding.hiddenDetails.visibility == View.GONE) {
                binding.hiddenDetails.visibility = View.VISIBLE
                binding.showHideDetails.text = getString(R.string.view_less)
                binding.showHideDetails.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_blue_up_arrow,
                    0
                )
            } else {
                binding.hiddenDetails.visibility = View.GONE
                binding.showHideDetails.text = getString(R.string.view_more)
                binding.showHideDetails.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_blue_down_arrow,
                    0
                )
            }
        }

        binding.prevPageBT.setOnClickListener {
            if (lastPos + 1 != 1) {
                pageAdapter?.changeItemPosition(lastPos, lastPos - 1)

            }
        }

        binding.nextButtonBT.setOnClickListener {
            if (reportsData?.numberOfPages!! > lastPos + 1) {
                pageAdapter?.changeItemPosition(lastPos, lastPos + 1)

            }
        }


        binding.shareReport.setOnClickListener {
            shareReport()
        }

        binding.downloadReport.setOnClickListener {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        binding.backArrow.setOnClickListener {
            navigateBack()
        }


    }


    private fun getCurrencyData() {
       currency = getPrivilegeBase()?.currency ?: ""
       currencyFormat = getPrivilegeBase()?.currencyFormat ?: this.getString(R.string.indian_currency_format)
    }

    private fun setData() {
        if (reportsData != null) {
            setOverAllData()
            setAdapter()
        }
    }

    private fun navigateBack() {
        finish()
    }

    private fun shareReport() {
        val uri = reportsData?.pdfUrl
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_report_msg) + "\n" + uri)
        startActivity(Intent.createChooser(sharingIntent, "Share in..."))
    }

    private fun checkPermission(permission: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, this)
        if (Build.VERSION.SDK_INT >= 33) {
            DownloadPdf.downloadReportPdf(
                this, reportsData?.pdfUrl
            )
        } else {
            if (permissionResult) {
                DownloadPdf.downloadReportPdf(
                    this, reportsData?.pdfUrl
                )
            } else {
                DownloadPdf.onRequestPermissionsResult(
                    STORAGE_PERMISSION_CODE,
                    permission,
                   this
                )
            }
        }
    }

    private fun setOverAllData() {
        reportsData?.result?.let {
            binding.tvNoOfCoupons.text=it.couponRedeemed
            binding.tvNetRevenueAmount.text=it.netRevenue.toString()
            binding.tvNoOfServices.text=it.totalServices.toString()
        }
    }

    private fun setPageAdapter() {
        pageAdapter = PagenationNumberAdapter(this,pagenationList,this)
        binding.pagenationRV.adapter = pageAdapter
    }

    private fun setAdapter() {
        val mealReportDateAdapter =
            reportsData?.result?.data?.let { MealReportDateAdapter(it,currency,currencyFormat,this) }
        binding.mealDateRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mealDateRecyclerView.adapter = mealReportDateAdapter
    }

    override fun onDataSend(type: Int, file: Any) {
        when(type) {
            1 -> {
                lastPos = file as Int
                if(lastPos + 1 == 1){
                    binding.prevPageBT.background = ContextCompat.getDrawable(this, R.drawable.button_selected_bg_grey_round)
                    binding.prevPageBT.setImageResource(R.drawable.page_left_grey)
                }else{
                    binding.prevPageBT.background = ContextCompat.getDrawable(this, R.drawable.button_selected_bg_round)
                    binding.prevPageBT.setImageResource(R.drawable.page_left_white)
                }

                if(reportsData?.numberOfPages == lastPos + 1){
                    binding.nextButtonBT.background = ContextCompat.getDrawable(this, R.drawable.button_selected_bg_grey_round)
                    binding.nextButtonBT.setImageResource(R.drawable.page_right_grey)
                }else{
                    binding.nextButtonBT.background = ContextCompat.getDrawable(this, R.drawable.button_selected_bg_round)
                    binding.nextButtonBT.setImageResource(R.drawable.page_right_white)
                }

//                showProgressDialog(this)
                for (i in 0 until pagenationList.size){
                    pagenationList[i].isSelected = false
                }
                pagenationList[lastPos].isSelected = true
                pageAdapter?.notifyDataSetChanged()
                callReportsApi(lastPos+1)
            }
        }
    }


    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {

    }


    private fun callReportsApi(page: Int) {
        if (isNetworkAvailable()) {
            binding.progressBarList?.visibility= View.VISIBLE
            viewModel.getReportsApi(
                apiKey = PreferenceUtils.getLogin().api_key ?: "",
                "hash",
                false,
                fromDate,
                toDate,
                "en",
                page,
                Pagination.PER_PAGE,
                true,
                viewModel.restaurantId,
                viewModel.serviceId
            )
        } else {
            toast(getString(R.string.network_not_available))
        }
    }


    private fun setReportObserver() {
        viewModel.reportsResponse.observe(this, Observer {
            binding.progressBarList?.visibility= View.GONE
            if(it!=null){

                    if(it.code==200){
                        reportsData=it
                        setData()
                    }
                    else{
                     toast(it.message)
                    }
            }
            else{
                toast(getString(R.string.server_error))
            }
        }
        )
    }



}