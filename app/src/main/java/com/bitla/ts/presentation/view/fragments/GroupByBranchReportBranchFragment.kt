package com.bitla.ts.presentation.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.R
import com.bitla.ts.databinding.FragmentGroupByBranchTabBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.group_by_branch_report_data.group_by_branch_report_response.GroupByBranchReportBranchData
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.GroupByBranchReportBranchAdapter
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gone
import visible

class GroupByBranchReportBranchFragment : Fragment() {
    private var _binding: FragmentGroupByBranchTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var branchList: ArrayList<GroupByBranchReportBranchData>
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var currency: String? = null
    private var currencyFormat: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupByBranchTabBinding.inflate(inflater, container, false)
        getPref()
        return binding.root
    }

    private fun getPref() {
        if (PreferenceUtils.getPrivilege() != null) {
            privilegeResponseModel = PreferenceUtils.getPrivilege()!!
            if (privilegeResponseModel != null) {
                currency = privilegeResponseModel?.currency
                    ?: requireContext().getString(R.string.rupess_symble)
                currencyFormat =
                    getCurrencyFormat(requireContext(), privilegeResponseModel?.currencyFormat)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(ARG_BRANCH_DATA)?.let {
            val type = object : TypeToken<ArrayList<GroupByBranchReportBranchData>>() {}.type
            branchList = Gson().fromJson(it, type)

            binding.apply {
                if (branchList.isNullOrEmpty()) {
                    rvBranch.gone()
                    NoResult.visible()
                } else {
                    rvBranch.visible()
                    NoResult.gone()
                    setupRecyclerView()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvBranch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = GroupByBranchReportBranchAdapter(requireContext(), branchList, currency, currencyFormat)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_BRANCH_DATA = "branch_data"

        fun newInstance(branchList: ArrayList<GroupByBranchReportBranchData>): GroupByBranchReportBranchFragment {
            return GroupByBranchReportBranchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_BRANCH_DATA, Gson().toJson(branchList))
                }
            }
        }
    }
}