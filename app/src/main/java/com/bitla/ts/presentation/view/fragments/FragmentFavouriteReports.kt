package com.bitla.ts.presentation.view.fragments

import android.Manifest
import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.starred_reports.*
import com.bitla.ts.domain.pojo.starred_reports.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.downloadPdf.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentFavouriteReports : BaseFragment(), DialogSingleButtonListener, OnItemClickListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var binding: LayoutFavouriteReportsFragmentBinding
    private lateinit var favouriteReportsAdapter: ReportsDateAdapter


    private var loginModelPref: LoginModel = LoginModel()
    private lateinit var apiKey: String
    private lateinit var bccId: String
    private val starredReportsViewModel by viewModel<StarredReportsViewModel<Any?>>()
    private var locale: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun isInternetOnCallApisAndInitUI() {
//        getPref()
    }

    override fun isNetworkOff() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutFavouriteReportsFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root
//        setFavouriteReportsAdapter()
        getPref()
        swipeRefreshLayout()
        startShimmerEffect()
        lifecycleScope.launch {
            starredReportsViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        return view
    }

//    private fun setFavouriteReportsAdapter(){
//
//        val searchList = arrayListOf<StarredReport>()
//
//        layoutManager = LinearLayoutManager(context?.applicationContext, LinearLayoutManager.VERTICAL, false)
//        binding.rvFavoutriteReports.layoutManager = layoutManager
//        favouriteReportsAdapter =
//            ReportsDateAdapter(context?.applicationContext, context?.applicationContext, searchList,true)
//        binding.rvFavoutriteReports.adapter = favouriteReportsAdapter
//    }

    private fun setFavouriteReportsAdapter(list: List<StarredReport>) {

        layoutManager = LinearLayoutManager(context?.applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rvFavoutriteReports.layoutManager = layoutManager
        favouriteReportsAdapter =
            ReportsDateAdapter(
                context = requireActivity(),
                reportList = list,
                starredReport = true,
                onItemClickListener = this
            )
        binding.rvFavoutriteReports.adapter = favouriteReportsAdapter
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        callStarredReportsApi()
    }

    private fun callStarredReportsApi() {
//        if (requireActivity().isNetworkAvailable()) {
            val reqBody =
                ReqBody(
                    api_key = apiKey,
                    recent_data = false,
                    locale = locale
                )
            val starredReportsRequest =
                StarredReportsRequest(bccId, format_type, starred_reports_name, reqBody)

            /*starredReportsViewModel.starredReportsApi(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                starredReportsRequest,
                starred_reports_name
            )*/

            starredReportsViewModel.starredReportsApi(
                apiKey = loginModelPref.api_key,
                recentData = false,
                locale = locale!!,
                apiType = starred_reports_name
            )

            setStarredReportObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun setStarredReportObserver() {
        starredReportsViewModel.starredReport.observe(viewLifecycleOwner) {
//            Timber.d("Test Run Starred Report Details ${it.toString()}")
            if (it != null) {
                when {
                    it.code == 200
                            && it.data?.starred_reports != null
                            && it.data.starred_reports.isNotEmpty() -> {
                        showReports()
                        setFavouriteReportsAdapter(it.data.starred_reports)
                        stopShimmerEffect()
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    it.code == 401 -> {
                        hideReports()
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    else -> {
                        it.message?.let { it1 -> requireActivity().toast(it1) }
//                        binding.recentReportLayout.gone()
                        stopShimmerEffect()
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
//                binding.recentReportLayout.gone()
                stopShimmerEffect()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }


    private fun hideReports() {
        binding.layoutStarredReport.gone()
        binding.NoResult.visible()
        binding.tvStarredTitle.gone()
        binding.tvNote.gone()
    }

    private fun showReports() {
        binding.layoutStarredReport.visible()
        binding.NoResult.gone()
        binding.tvStarredTitle.visible()
        binding.tvNote.visible()
    }


    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
        val permission = DownloadPdf.checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            requireActivity()
        )
        if (Build.VERSION.SDK_INT > 32){
            DownloadPdf.downloadReportPdf(requireContext(), data)

        } else{
            if (permission) {
                DownloadPdf.downloadReportPdf(requireContext(), data)
            } else {
                DownloadPdf.onRequestPermissionsResult(
                    requestCode = STORAGE_PERMISSION_CODE,
                    permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    context = requireActivity()
                )
            }
        }
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun swipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            startShimmerEffect()
            Handler(Looper.getMainLooper()).postDelayed({
                callStarredReportsApi()
                setStarredReportObserver()
            }, 500)

//            favouriteReportsAdapter.notifyDataSetChanged()
        }
    }

    /*
* this method to used for start Shimmer Effect
* */
    private fun startShimmerEffect() {
        binding.shimmerFavReport.visible()
        binding.layoutStarredReport.gone()
        binding.shimmerFavReport.startShimmer()
    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.shimmerFavReport.gone()
        binding.layoutStarredReport.visible()
        if (binding.shimmerFavReport.isShimmerStarted) {
            binding.shimmerFavReport.stopShimmer()
        }
    }
}