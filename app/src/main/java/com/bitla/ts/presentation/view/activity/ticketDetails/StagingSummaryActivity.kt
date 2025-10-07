package com.bitla.ts.presentation.view.activity.ticketDetails

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.ActivityStagingSummaryBinding
import com.bitla.ts.databinding.LayoutBookTicketsBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.phase2.adapter.parent.BoardingStagingAdapter
import com.bitla.ts.phase2.adapter.parent.DroppingStagingAdapter
import com.bitla.ts.presentation.viewModel.StagingSummaryViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible

class StagingSummaryActivity : BaseActivity() , DialogSingleButtonListener {

    private lateinit var binding: ActivityStagingSummaryBinding
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String? = null
    private val stagingSummaryDetailsViewModel by viewModel<StagingSummaryViewModel<Any?>>()
    private var boardingStageAdapter: BoardingStagingAdapter? = null
    private var droppingStageAdapter: DroppingStagingAdapter? = null
    private var boardingStageSummaryTitle: String? = null
    private var droppingStageSummaryTitle: String? = null
    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        resID = PreferenceUtils.getPreference(
            PREF_RESERVATION_ID, 0L
        ).toString()
    }

    override fun initUI() {
    }

    @SuppressLint("LongLogTag", "SuspiciousIndentation", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStagingSummaryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        val emptySeatsStaging = getString(R.string.empty_seats_staging)
        val emptySeatsStaging1 = getString(R.string.empty_seats_staging1)
        boardingStageSummaryTitle = getString(R.string.boarding_stage_summary)
        droppingStageSummaryTitle = getString(R.string.dropping_stage_summary)

        getPref()
        binding.layoutToolbar.imgBack.setOnClickListener {
            onBackPressed()
        }
        val stageSummaryTitle = getString(R.string.stage_summary)
        binding.layoutToolbar.toolbarHeaderText.text = stageSummaryTitle

        binding.summaryImg.setOnClickListener {
            if (binding.summaryRV.visibility == View.VISIBLE) {
                binding.summaryRV.gone()
                binding.summaryImg.animate().rotation(0f).setDuration(500)


            } else {
                binding.summaryRV.visible()
                binding.summaryImg.animate().rotation(-180.0f).setDuration(500)
            }

        }
        binding.summaryImg1.setOnClickListener {
            if (binding.summaryRV1.visibility == View.VISIBLE) {
                binding.summaryRV1.gone()
                binding.summaryImg1.animate().rotation(0f).setDuration(500)
            } else {
                binding.summaryRV1.visible()
                binding.summaryImg1.animate().rotation(-180.0f).setDuration(500)

            }
        }


        stagingSummaryDetailsViewModel.StagingSummaryDetailsAPI(
            loginModelPref.api_key,
            resID.toString()
        )
        stagingSummaryDetailsViewModel.stagingsummaryDetails.observe(this) {
            try {
                if (it != null) {
                    binding.progressBar.gone()
                    when (it.code) {
                        200 -> {

                            binding.fareHeadTV.text =
                                it?.result?.boardingSummary?.totalSeats.toString()

                            binding.fareValueTV.text =
                                it?.result?.boardingSummary?.boardedCount.toString()

                            binding.BoardseatValue.text =
                                it?.result?.boardingSummary?.totalYetToBoard?.joinToString(", ")
                            binding.emptyseat.text =
                                emptySeatsStaging + "${it?.result?.boardingSummary?.emptySeats?.size}" + emptySeatsStaging1
                            it?.result?.boardingSummary?.emptySeats?.size.toString()
                            binding.emptySeatValue.text =
                                it?.result?.boardingSummary?.emptySeats?.joinToString(", ")



                            boardingStageAdapter = it?.result?.boardingStageSummary?.let { it1 ->
                                BoardingStagingAdapter(this, it1)
                            }

                            droppingStageAdapter = it?.result?.droppingStageSummary?.let { it1 ->
                                DroppingStagingAdapter(this, it1)
                            }


                            val recyclerView: RecyclerView = findViewById(R.id.summaryRV)

                            recyclerView.adapter = boardingStageAdapter

                            val recyclerView1: RecyclerView = findViewById(R.id.summaryRV1)
                            recyclerView1.adapter = droppingStageAdapter
                        }
                        401 -> {
                            DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )
                        }

                        else -> {
                            R.layout.layout_book_tickets
                            val bindingBookTickets = LayoutBookTicketsBinding.inflate(layoutInflater)
                            bindingBookTickets.noData.isVisible
                            bindingBookTickets.tvNoService.text = it.message.toString()
                            bindingBookTickets.tvNoService.isVisible

                        }

                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                toast("An error occurred")
            }


        }
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun onSingleButtonClick(str: String) {

    }
}


