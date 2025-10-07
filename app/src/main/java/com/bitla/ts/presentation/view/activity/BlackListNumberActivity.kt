package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.content.*
import android.os.Build
import android.text.*
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.blocked_numbers_list.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import gone
import isNetworkAvailable
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible


class BlackListNumberActivity : BaseActivity() , OnItemAdapterClickListener {
    companion object {
        val TAG: String = BlackListNumberActivity::class.java.simpleName
    }

    lateinit var binding: ActivityBlackListNumberBinding
    private val blackListNumberViewModel by viewModel<BlackListViewModel<Any>>()
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = null
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var blackListAdapter: BlackListNumberAdapter
    private var blockNumberStatus: String? = null
    private var unBlockNumberStatus: String? = null
    private var blockedOnList : String? = null
    private var blockedByList: String? = null



    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initUI() {
        getPref()
        binding = ActivityBlackListNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        blockNumberStatus = getString(R.string.blockNumberStatus)
        unBlockNumberStatus = getString(R.string.unBlockNumberStatus)
        binding.blackListToolbar.tvCurrentHeader.text = getString(R.string.black_list)
        binding.blackListToolbar.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnBlackListNumber.setOnClickListener {
            binding.includeProgress1.progressBar.visible()
            if (isNetworkAvailable()) {
                callBlockNumberApi(
                    phoneNumber = binding.etPhoneNumber.text.toString(),
                    remarks = binding.etRemarks.text.toString()
                )
            }
            else{
                noNetworkToast()
            }
          blackListAdapter.notifyDataSetChanged()
        }
        if (isNetworkAvailable()) {
            callBlockedNumbersListApi()
        }
        else{
            noNetworkToast()
        }

        setBlackListNumberApiObserver()
        setBlockedListNumberApiObserver()

    }


    override fun isInternetOnCallApisAndInitUI() {

    }

    private fun callBlockedNumbersListApi(){
        blackListNumberViewModel.blockedNumbersList(
            apiKey = loginModelPref.api_key,
            locale = locale ?: ""
        )
    }

    private fun callUnBlockNumberApi(phoneNumber:String,remarks:String) {

        blackListNumberViewModel.blackListNumberApi(
            apiKey = loginModelPref.api_key,
            phoneNumber = phoneNumber,
            locale = locale ?: "",
            remarks = remarks,
            status = unBlockNumberStatus ?: ""

        )

    }
    private fun callBlockNumberApi(phoneNumber:String,remarks:String) {

        blackListNumberViewModel.blackListNumberApi(
            apiKey = loginModelPref.api_key,
            phoneNumber = phoneNumber,
            locale = locale ?: "",
            remarks = remarks,
            status = blockNumberStatus ?: ""

        )
    }

    private fun updateSearchList(blockedNumberList: ArrayList<BlockedNumber>){
        binding.etSearchOption.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                val filteredList = if (s.isEmpty()) {
                    blockedNumberList
                } else {
                    blockedNumberList.filter {
                        it.blockedNumber.equals(
                            s.toString(),
                            ignoreCase = true
                        )
                    }
                }
                blackListAdapter.updateList(filteredList)
            }
        })
    }
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun setBlockedListNumberApiObserver() {
        blackListNumberViewModel.blackListNumberListDetails.observe(this) {
            try {
                if (it != null) {
                    binding.includeProgress.progressBar.gone()
                    binding.blacklistedNumbersCount.text =
                        (getString(R.string.et_search_option) + " (" + it.blockedNumberList?.size)+")"
                    when (it.code) {
                        200 -> {
                            if(it.blockedNumberList != null) {
                                updateSearchList(it.blockedNumberList)

                            }
                            var blockedPhoneNumber : String? = null
                            var remarks : String? = null


                            setBlackListAdapter(it.blockedNumberList)
                            blackListAdapter.notifyDataSetChanged()

                        }

                        401 -> {
                            openUnauthorisedDialog()

                        }

                        else -> {
                            toast(it.code.toString())
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (e: Exception) {
                toast(getString(R.string.opps))
            }


        }
    }
    private fun setBlackListNumberApiObserver() {
        blackListNumberViewModel.blackListDetails.observe(this) {
            try {
                if (it != null) {
                    binding.includeProgress1.progressBar.gone()

                    when (it.code) {
                        200 -> {
                            toast(it.message)
                            binding.etPhoneNumber.setText("")
                            binding.etRemarks.setText("")
                            callBlockedNumbersListApi()

                        }

                        401 -> {
                            openUnauthorisedDialog()

                        }

                        else -> {
                            toast(it.message ?: "")
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (e: Exception) {
                toast(getString(R.string.opps))
            }


        }
    }

    private fun openUnauthorisedDialog() {
        DialogUtils.unAuthorizedDialog(this,
            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
            object : DialogSingleButtonListener {
                override fun onSingleButtonClick(str: String) {
                    if (str == getString(R.string.unauthorized)) {

                        val intent = Intent(
                            this@BlackListNumberActivity,
                            LoginActivity::class.java
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }

            }
        )
    }


    private fun setBlackListAdapter(list: ArrayList<BlockedNumber>) {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvBlackListNumber.layoutManager = layoutManager
        blackListAdapter =
            BlackListNumberAdapter(this, list ,this )

        binding.rvBlackListNumber.adapter = blackListAdapter
    }


    override fun onItemClick(phoneNumber: String) {
        if (isNetworkAvailable()) {
            callUnBlockNumberApi(phoneNumber = phoneNumber, "")
        }
        else{
            noNetworkToast()
        }
    }

    override fun getBlockedNumber(blockedBy: String, blockedOn: String) {
       blockedOnList = blockedOn
        blockedByList = blockedBy
    }
}

