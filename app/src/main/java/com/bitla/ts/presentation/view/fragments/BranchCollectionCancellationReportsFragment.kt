package com.bitla.ts.presentation.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.alloted_Service_method_name
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.DialogProgressBarBinding
import com.bitla.ts.databinding.LayoutBranchCollectionCancellationFragmentBinding
import com.bitla.ts.domain.pojo.all_reports.AllReports
import com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
import com.bitla.ts.domain.pojo.all_reports.new_response.occupany_report_data.PagenationData
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.BranchCollectionCancellationAdapter
import com.bitla.ts.presentation.adapter.PagenationNumberAdapter
import com.bitla.ts.presentation.viewModel.AllReportsViewModel
import com.bitla.ts.utils.common.edgeToEdgeFromOnlyBottom
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.constants.STORAGE_PERMISSION_CODE
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_BRANCH_REPORT_DATA
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.gson.Gson
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible

class BranchCollectionCancellationReportsFragment(var reqBodyy: String?) : BaseFragment(),
    DialogButtonAnyDataListener {

    private var lastPos: Int = 0
    private var travelDate: String = ""
    private var pdfFilename: String = ""
    private var bookingData: AllReports? = null
    private var reqBody: ReqBody? = null
    private var loginModellPref: LoginModel? = null
    private var locale: String = ""
    private lateinit var binding: LayoutBranchCollectionCancellationFragmentBinding
    private var adapter: BranchCollectionCancellationAdapter? = null
    private var currency: String = ""
    private var currencyFormat: String = ""
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private val allReportsViewModel by viewModel<AllReportsViewModel<Any?>>()
    private var pagenationList : ArrayList<PagenationData> = arrayListOf()
    private var pageAdapter: PagenationNumberAdapter?= null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutBranchCollectionCancellationFragmentBinding.inflate(inflater, container, false)
        binding.root
        lifecycleScope.launch {
            allReportsViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }

        init()

        return binding.root
    }

    private fun init() {
        setUpBranchCollectionApiObserver()
        getPref()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdgeFromOnlyBottom(binding.root)
        }

        if (PreferenceUtils.getPreference(PREF_BRANCH_REPORT_DATA, "") != null) {
            val data = PreferenceUtils.getPreference(PREF_BRANCH_REPORT_DATA, "")

            if (!data.isNullOrEmpty()) {
                bookingData = Gson().fromJson(data, AllReports::class.java)

                if(bookingData!!.result!!.cancellations.size > 0){
                    binding.noDataTV.gone()
                }else{
                    binding.noDataTV.visible()

                }

                pagenationList.clear()
                for (i in 0 until bookingData!!.result!!.noOfPagesCancelled){
                    val obj = PagenationData()
                    obj.position = i
                    obj.isSelected = false
                    pagenationList.add(obj)
                }
            }

            if(pagenationList.size > 1){
                binding.bottomV.visible()
                pagenationList[0].isSelected  = true
                setPageAdapter()
            }else{
                binding.bottomV.gone()
            }
        }

        binding.prevPageBT.setOnClickListener{
            if(lastPos + 1 != 1){
                pageAdapter!!.changeItemPosition(lastPos,lastPos-1)

            }
        }

        binding.nextButtonBT.setOnClickListener {
            if(bookingData!!.result!!.noOfPagesCancelled > lastPos + 1){
                pageAdapter!!.changeItemPosition(lastPos,lastPos+1)

            }
        }


        reqBody = Gson().fromJson(reqBodyy, ReqBody::class.java)

        binding.totalSeatsValueTV.text = bookingData?.result?.totalBookingCount.toString()
        binding.totalAmountValueTV.text = " ${bookingData?.result?.totalCancellationCount}"


        if (bookingData != null) {
            setAdpater(bookingData!!)
        }
    }

    private fun hitBranchCollectionApi(pageCount : Int){
        val body = reqBody
        body?.page = pageCount
        allReportsViewModel.userCollectionDetailApi(
            loginModellPref!!.api_key,
            locale,
            body!!,
            alloted_Service_method_name
        )

    }

    private fun setPageAdapter() {

        pageAdapter = PagenationNumberAdapter(requireActivity(),pagenationList,this)
        binding.pagenationRV.adapter = pageAdapter


    }


    override fun isInternetOnCallApisAndInitUI() {}

    override fun isNetworkOff() {
    }


    private fun getPref() {
        if ((activity as BaseActivity).getPrivilegeBase()!= null) {
            privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase() as PrivilegeResponseModel
            currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(requireContext(), privilegeResponseModel.currencyFormat)
            locale = PreferenceUtils.getlang()
            loginModellPref = PreferenceUtils.getLogin()
        }
    }

    private fun setAdpater(list: AllReports) {



        if (list.result != null) {
            adapter = BranchCollectionCancellationAdapter(
                context = requireContext(),
                cancellationDataList = list.result.cancellations,privilegeResponseModel
            )
            binding.listRV.adapter = adapter
        }
    }

    private fun setUpBranchCollectionApiObserver() {
        allReportsViewModel.allReports.observe(requireActivity()) { it ->
            Timber.d("allReports_TicketBookedByYou$it")
            dismissProgressDialog()

            if (it != null) {

                when (it.code) {
                    200 -> {
                        binding.pagenationRV.smoothScrollToPosition(lastPos)

                        setAdpater(it)

                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        if (it.message != null) {
                            it.message.let { it1 -> requireActivity().toast(it1) }
                        }
                    }
                }
            } else {
                requireActivity().toast(getString(R.string.server_error))
            }
        }
    }

    private fun dismissProgressDialog() {
        if (DialogUtils.progressDialog != null && DialogUtils.progressDialog!!.isShowing) {
            DialogUtils.progressDialog!!.dismiss()
        }
    }

    fun showProgressDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.Style_Dialog_Rounded_littl_Corner)
        val dialogBinding = DialogProgressBarBinding.inflate(LayoutInflater.from(context))
        builder.setView(dialogBinding.root)
        DialogUtils.progressDialog = builder.create()
        DialogUtils.progressDialog!!.setCancelable(false)
        DialogUtils.progressDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        DialogUtils.progressDialog!!.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
//            DownloadPdf.downloadReportPdf(this, bookingData!!.pdfUrl)
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onDataSend(type: Int, file: Any) {
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

                if(bookingData!!.result!!.noOfPagesCancelled == lastPos + 1){
                    binding.nextButtonBT.background = ContextCompat.getDrawable(requireContext(),R.drawable.button_selected_bg_grey_round)
                    binding.nextButtonBT.setImageResource(R.drawable.page_right_grey)
                }else{
                    binding.nextButtonBT.background = ContextCompat.getDrawable(requireContext(),R.drawable.button_selected_bg_round)
                    binding.nextButtonBT.setImageResource(R.drawable.page_right_white)
                }
                showProgressDialog(requireContext())
                for (i in 0 until pagenationList.size){
                    pagenationList[i].isSelected = false
                }
                pagenationList[lastPos].isSelected = true
                pageAdapter?.notifyDataSetChanged()
                hitBranchCollectionApi(lastPos + 1)
            }
        }
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {


    }


}