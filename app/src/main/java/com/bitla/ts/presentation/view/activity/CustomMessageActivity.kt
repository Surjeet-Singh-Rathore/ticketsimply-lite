package com.bitla.ts.presentation.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityCustomMessageBinding
import com.bitla.ts.domain.pojo.sms_types.SmsInputMode
import com.bitla.ts.domain.pojo.sms_types.SmsTemplate
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.sharedPref.PREF_SMS_TEMPLATE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import toast

class CustomMessageActivity : BaseActivity() {

    companion object {
        val tag: String = CustomMessageActivity::class.java.simpleName
    }

    private val smsId = 9 // fixed
    private var smsType: String? = null
    private val smsInputMode = mutableListOf<SmsInputMode>()
    private lateinit var binding: ActivityCustomMessageBinding


    override fun initUI() {
        binding = ActivityCustomMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setToolbarTitle()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
    }

    private fun setToolbarTitle() {
        binding.toolbar.tvCurrentHeader.text = getString(R.string.custom_message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clickListener()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener(this)
        binding.btnConfirmMessage.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.img_back -> onBackPressed()
            R.id.btnConfirmMessage -> {
                if (binding.etCustomMsg.text!!.isEmpty()) {
                    toast(getString(R.string.validate_message))
                } else {
                    val smsContent = binding.etCustomMsg.text.toString()
                    smsType = getString(R.string.custom_message)
                    val smsTemplate = SmsTemplate(smsContent, smsId, smsInputMode, smsType!!)
                    PreferenceUtils.putObject(smsTemplate, PREF_SMS_TEMPLATE)
                    val intent = Intent(this, SmsNotificationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}