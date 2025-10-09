package com.bitla.restaurant_app.presentation.view.fragments

import android.annotation.SuppressLint
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.restaurant_app.R
import com.bitla.restaurant_app.databinding.FragmentMealsReportBinding
import com.bitla.restaurant_app.presentation.pojo.PagenationData
import com.bitla.restaurant_app.presentation.pojo.reports.ReportsResponse
import com.bitla.restaurant_app.presentation.utils.DownloadPdf
import com.bitla.restaurant_app.presentation.utils.PreferenceUtils
import com.bitla.restaurant_app.presentation.utils.isNetworkAvailable
import com.bitla.restaurant_app.presentation.utils.toast
import com.bitla.restaurant_app.presentation.utils.Constants
import com.bitla.restaurant_app.presentation.utils.gone
import com.bitla.restaurant_app.presentation.utils.visible
import com.bitla.restaurant_app.presentation.view.MainActivity
import com.bitla.restaurant_app.presentation.view.adapters.MealReportDateAdapter
import com.bitla.restaurant_app.presentation.view.adapters.PagenationNumberAdapter
import com.bitla.restaurant_app.presentation.viewModel.RestaurantViewModel

class MealsReportFragment : Fragment() {

    private var _binding: FragmentMealsReportBinding? = null
    private val binding get() = _binding!!
    private var reportsData :ReportsResponse?=null
    private var currency=""
    private var currencyFormat=""
    private var pageAdapter: PagenationNumberAdapter?= null
    private var pagenationList : ArrayList<PagenationData> = arrayListOf()
    private var lastPos: Int = 0
    private val viewModel by viewModels<RestaurantViewModel>()
    private var fromDate=""
    private var toDate=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMealsReportBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if(it.containsKey("reportsData")){
                reportsData=it.getParcelable("reportsData")
                fromDate=it.getString("fromDate").toString()
                toDate=it.getString("toDate").toString()
                viewModel.serviceId=it.getString("serviceId").toString()
                viewModel.restaurantId=it.getString("restaurantId").toString()


                setPaginationList()
            }
        }
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCurrencyData()

        setData()

        setPaginationAdapter()

        setClickListeners()

        (activity as MainActivity).hideToolBar()
        (activity as MainActivity).hideBottomBar()
        setReportObserver()
    }



    private fun setPaginationAdapter() {
        if (pagenationList.size > 1) {
            binding.bottomV.visible()
            pagenationList[0].isSelected = true
            setPageAdapter()
        } else {
            binding.bottomV.gone()
        }
    }

    private fun setClickListeners() {
        binding.showHideDetails.setOnClickListener {
            if (binding.hiddenDetails.visibility == View.GONE) {
                binding.hiddenDetails.visible()
                binding.showHideDetails.text = getString(R.string.view_less)
                binding.showHideDetails.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_blue_up_arrow,
                    0
                )
            } else {
                binding.hiddenDetails.gone()
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
                pageAdapter!!.changeItemPosition(lastPos, lastPos - 1)

            }
        }

        binding.nextButtonBT.setOnClickListener {
            if (reportsData?.numberOfPages!! > lastPos + 1) {
                pageAdapter!!.changeItemPosition(lastPos, lastPos + 1)

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
        currency = PreferenceUtils.getPreference("currency", "") ?: ""
        currencyFormat = PreferenceUtils.getPreference("currencyFormat", "") ?: ""
    }

    private fun setData() {
        if (reportsData != null) {
            setOverAllData()
            setAdapter()
        }
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

    private fun shareReport() {
        val uri = reportsData?.pdfUrl
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_report_msg) + "\n" + uri)
        startActivity(Intent.createChooser(sharingIntent, "Share in..."))
    }

    private fun checkPermission(permission: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, requireActivity())
        if (Build.VERSION.SDK_INT >= 33) {
            DownloadPdf.downloadReportPdf(
                requireContext(), reportsData?.pdfUrl
            )
        } else {
            if (permissionResult) {
                DownloadPdf.downloadReportPdf(
                    requireContext(), reportsData?.pdfUrl
                )
            } else {
                DownloadPdf.onRequestPermissionsResult(
                    Constants.STORAGE_PERMISSION_CODE,
                    permission,
                    requireActivity()
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
        pageAdapter = PagenationNumberAdapter(requireContext(),pagenationList) { type, file ->
            onDataSend(
                type,
                file
            )
        }
        binding.pagenationRV.adapter = pageAdapter
    }

    private fun setAdapter() {
        val mealReportDateAdapter = MealReportDateAdapter(reportsData?.result?.data!!,currency,currencyFormat)
        binding.mealDateRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.mealDateRecyclerView.adapter = mealReportDateAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



     fun onDataSend(type: Int, file: Any) {
        when(type) {
            1 -> {
                lastPos = file as Int
                if(lastPos + 1 == 1){
                    binding.prevPageBT.background = ContextCompat.getDrawable(requireContext(),R.drawable.button_selected_bg_grey_round)
                    binding.prevPageBT.setImageResource(R.drawable.page_left_grey)
                }else{
                    binding.prevPageBT.background = ContextCompat.getDrawable(requireContext(),R.drawable.button_selected_bg_round)
                    binding.prevPageBT.setImageResource(R.drawable.page_left_white)
                }

                if(reportsData!!.numberOfPages == lastPos + 1){
                    binding.nextButtonBT.background = ContextCompat.getDrawable(requireContext(),R.drawable.button_selected_bg_grey_round)
                    binding.nextButtonBT.setImageResource(R.drawable.page_right_grey)
                }else{
                    binding.nextButtonBT.background = ContextCompat.getDrawable(requireContext(),R.drawable.button_selected_bg_round)
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


    private fun callReportsApi(page: Int) {
        if (requireContext().isNetworkAvailable()) {
            binding.progressBarList?.visible()
            viewModel.getReportsApi(
                apiKey = PreferenceUtils.getLogin().api_key ?: "",
                "hash",
                false,
               fromDate,
                toDate,
                "en",
                page,
                5,
                true,
                viewModel.restaurantId,
                viewModel.serviceId
            )
        } else {
            requireContext().toast(getString(R.string.network_not_available))
        }
    }


    private fun setReportObserver() {
        viewModel.reportsResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBarList?.gone()
            if(it!=null){
                it.peekContent().let {
                    if(it.code==200){
                        reportsData=it
                        setData()
                    }
                    else{
                    requireContext().toast(it.message)
                }
                }
            }
            else{
                requireContext().toast(getString(R.string.server_error))
            }
        }
        )
    }


}