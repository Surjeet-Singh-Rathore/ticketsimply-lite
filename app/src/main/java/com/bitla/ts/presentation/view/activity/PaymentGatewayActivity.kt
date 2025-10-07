package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bitla.ts.R
import com.bitla.ts.databinding.ActivityPaymentGatewayBinding
import com.bitla.ts.domain.pojo.phonepe.HtmlResponse
import com.bitla.ts.utils.common.edgeToEdge
import gone


class  PaymentGatewayActivity : AppCompatActivity() {
    private var isPayBitla: Boolean = false
    private var amount: String = ""
    private val TAG: String = PaymentGatewayActivity::class.java.simpleName

    private var htmlBody = ""
    private var pnrNumber: String = ""
    private var isPhonePe: Boolean = false
    private lateinit var binding: ActivityPaymentGatewayBinding

    var intentCheck = ""
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentGatewayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }


        binding.toolbarI.imgBack.setOnClickListener {
            binding.webView.destroy()
            val intent = Intent(this,InstantRechargeActivity::class.java)
            intent.putExtra("pnr_number",pnrNumber)
            intent.putExtra("amount",amount)
            startActivity(intent)
            finish()
        }

        binding.toolbarI.tvCurrentHeader.text  = getString(R.string.payment)

        binding.toolbarI.imgSearch.gone()

        if(intent.hasExtra("is_pay_bitla")){
            isPayBitla = intent.getBooleanExtra("is_pay_bitla",false)
        }


        if(intent.hasExtra("url") || intent.hasExtra(getString(R.string.HTML_BODY))){
            var url = intent.getStringExtra("url")
            if(intent.hasExtra(getString(R.string.HTML_BODY))) {
                var htmlResponse: HtmlResponse =
                    intent.getSerializableExtra(getString(R.string.HTML_BODY)) as HtmlResponse
                url = htmlResponse?.htmlBody!!
            }
            pnrNumber = intent.getStringExtra("pnr_number")?:""
            amount = intent.getStringExtra("amount")?:""

            // Enable JavaScript (optional)
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.settings.loadWithOverviewMode = true
            binding.webView.settings.useWideViewPort = true
            binding.webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            binding.webView.settings.domStorageEnabled = true
//            binding.webView.settings.setAppCacheEnabled(true)
            binding.webView.settings.loadsImagesAutomatically = true
            binding.webView.isScrollbarFadingEnabled = true
            binding.webView.isVerticalScrollBarEnabled = true
            binding.webView.addJavascriptInterface("test","android")

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptThirdPartyCookies(binding.webView,true)
            }

            // Load URL
           // binding.webView.loadUrl(url!!)
            binding.webView.loadDataWithBaseURL("",url!!,"text/html","UTF-8",null)


            // Set WebViewClient to handle page navigation

            binding.webView.webViewClient = object : WebViewClient() {
                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {

                    Log.e("redirect url "," $url")

                    // your code here
                    if(url!!.contains("do_conpay_for_agent")){
                        val intent = Intent(this@PaymentGatewayActivity,InstantRechargeActivity::class.java)
                        intent.putExtra("payment_done",true)
                        intent.putExtra("pnr_number",pnrNumber)
                        intent.putExtra("amount",amount)
                        intent.putExtra("new_booking",true)
                        intent.putExtra("is_pay_bitla",isPayBitla)
                        startActivity(intent)
                        this@PaymentGatewayActivity.finish()
                    }



                    super.doUpdateVisitedHistory(view, url, isReload)
                }
            };
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.webView.destroy()
        val intent = Intent(this,InstantRechargeActivity::class.java)
        intent.putExtra("pnr_number",pnrNumber)
        intent.putExtra("amount",amount)
        startActivity(intent)
        finish()
    }









}
