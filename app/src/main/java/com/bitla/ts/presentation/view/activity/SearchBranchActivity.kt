package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.OnItemCheckedListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ActivitySearchBranchBinding
import com.bitla.ts.domain.pojo.BranchModel.Branch
import com.bitla.ts.domain.pojo.BranchModel.BranchList
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.presentation.adapter.SearchBranchAdapter
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.constants.SELECT_BRANCH_INTENT_REQUEST_CODE
import com.bitla.ts.utils.sharedPref.*
import toast
import java.util.*

class SearchBranchActivity : BaseActivity(), OnItemClickListener, OnItemCheckedListener {

    private lateinit var binding: ActivitySearchBranchBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var searchBranchAdapter: SearchBranchAdapter
    private var branchListModel: BranchList? = null
    private var filterdNamesList = mutableListOf<Branch>()
    private var list = mutableListOf<Branch>()
    private var branchList = mutableListOf<Branch>()
    private var selectedBranchId: String? = null
    private var servicesSelectedFalg: Boolean = true
    private var searchBranchFrom = ""
    private var locale: String? = ""

    override fun initUI() {
        binding = ActivitySearchBranchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        val tempList = branchListModel?.branchList
        tempList?.forEach {
            list.add(it)
        }

        list.forEach {
            if (!it.isChecked) {
                binding.chkSelectAll.isChecked = false
                binding.chkSelectAll.text = getString(R.string.select_all_branches)
            }
        }

        invalidateBranchesCount()
        setBranchAdapter()

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                search(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })

        binding.btnSelectBranches.setOnClickListener {

            val isAllUnChecked = list.all { !it.isChecked }
            if (isAllUnChecked) {
                toast(getString(R.string.pleaseSelectAtleastOneBranch))
                return@setOnClickListener
            }

            val intent = Intent()

            branchList = list
            setResult(SELECT_BRANCH_INTENT_REQUEST_CODE, intent)
            finish()

        }

        binding.toolbarImageLeft.setOnClickListener {
            this.finish()
        }

        binding.chkSelectAll.setOnClickListener {
            if (binding.chkSelectAll.isChecked) {
                list.forEach {
                    it.isChecked = true
                }
                binding.chkSelectAll.text = getString(R.string.deselect_all_branches)
                servicesSelectedFalg = true
            } else {
                list.forEach {
                    it.isChecked = false
                }
                binding.chkSelectAll.text = getString(R.string.select_all_branches)
                servicesSelectedFalg = false
            }

            if (::searchBranchAdapter.isInitialized) {
                if (filterdNamesList.size > 0) {
                    searchBranchAdapter.addData(filterdNamesList)

                } else {
                    searchBranchAdapter.addData(list)
                }
            }
            invalidateBranchesCount()
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun onItemChecked(isChecked: Boolean, view: View, position: Int) {

    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onClickOfItem(data: String, position: Int) {
        val index = list.indexOfFirst {
            it.id == position
        }
        list[index].isChecked = data == "true"
        list.forEach {
            if (!it.isChecked) {
                binding.chkSelectAll.isChecked = false
                binding.chkSelectAll.text = getString(R.string.select_all_branches)
                return@forEach
            }
        }

        invalidateBranchesCount()
    }

    private fun setBranchAdapter() {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectSearchPassenger.layoutManager = layoutManager
        searchBranchAdapter = SearchBranchAdapter(this, this)
        searchBranchAdapter.addData(list)
        binding.rvSelectSearchPassenger.adapter = searchBranchAdapter
        searchBranchAdapter.notifyDataSetChanged()

    }

    @SuppressLint("DefaultLocale")
    private fun search(text: String) {

        filterdNamesList = mutableListOf()

        for (s in list) {

            if (s.value.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                filterdNamesList.add(s)
            }
        }

        if (::searchBranchAdapter.isInitialized) {
            searchBranchAdapter.addData(filterdNamesList)
        }
    }


    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }


    private fun invalidateBranchesCount() {
        var totalCheckedItems = 0
        list.forEach {
            if (it.isChecked) {
                totalCheckedItems++
            }
        }
        binding.tvSelectBranches.text = "${getString(R.string.select_branches)} ($totalCheckedItems)"

    }
}