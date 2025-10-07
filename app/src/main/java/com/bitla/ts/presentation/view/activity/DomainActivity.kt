package com.bitla.ts.presentation.view.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.TsApplication.Companion.getAppContext
import com.bitla.ts.databinding.ActivityDomainBinding
import com.bitla.ts.domain.pojo.dynamic_domain.DynamicDomain
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.koin.appModule.ApiModule
import com.bitla.ts.koin.appModule.RepositoryModule
import com.bitla.ts.koin.appModule.ViewModelModule
import com.bitla.ts.koin.networkModule.NetworkModule
import com.bitla.ts.presentation.adapter.MyContextWrapper
import com.bitla.ts.presentation.adapter.SliderAdapter
import com.bitla.ts.presentation.viewModel.DomainViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.application.LocaleManager
import com.bitla.ts.utils.common.clearAndSave
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.edgeToEdgeFromOnlyBottom
import com.bitla.ts.utils.common.setCountryCodes
import com.bitla.ts.utils.common.updateBaseURL
import com.bitla.ts.utils.constants.STATUS_CODE_NO_API_RESPONSE
import com.bitla.ts.utils.sharedPref.PREF_BCC_ID
import com.bitla.ts.utils.sharedPref.PREF_DOMAIN
import com.bitla.ts.utils.sharedPref.PREF_EXCEPTION
import com.bitla.ts.utils.sharedPref.PREF_IS_ENCRYPTED
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_LOCALE
import com.bitla.ts.utils.sharedPref.PREF_LOGGED_IN_USER
import com.bitla.ts.utils.sharedPref.PREF_LOGO
import com.bitla.ts.utils.sharedPref.PREF_TRAVEL_DATE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import setSafeOnClickListener
import toast
import visible
import kotlin.math.abs


class DomainActivity : BaseActivity() {

    companion object {
        val TAG = DomainActivity::class.java.simpleName
    }

    private var domain: String? = null

    lateinit var sliderHandle: Handler
    lateinit var sliderRun: Runnable
    private val domainViewModel by viewModel<DomainViewModel>()
    private var logo: String? = null
    private var changeDomain: String? = null
    private var prefUserlist = mutableListOf<LoginModel>()
    lateinit var imageContainer: ViewPager2
    var adapter: SliderAdapter? = null
    var dots = mutableListOf<TextView>()
    var layout: LinearLayout? = null
    private lateinit var pager: ViewPager2
    private lateinit var binding: ActivityDomainBinding
    var itemList = arrayListOf<Int>(
        R.drawable.ic_bus,
        R.drawable.ic_hand,
        R.drawable.ic_book
    )
    private var locale: String? = ""

    override fun initUI() {
        binding = ActivityDomainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (PreferenceUtils.getlang() == "in") {
            PreferenceUtils.putString(PREF_LOCALE, "id")
        } else {
            PreferenceUtils.putString(PREF_LOCALE, "en")
        }
        changeLanguage(PreferenceUtils.getlang())
//        binding.carousel.registerLifecycle(this)
//        val carouselList = mutableListOf<CarouselItem>()
//        carouselList.add(CarouselItem(imageDrawable = R.drawable.carosal_1))
//        carouselList.add(CarouselItem(imageDrawable = R.drawable.carosal_1))
//        carouselList.add(CarouselItem(imageDrawable = R.drawable.carosal_1))
//        binding.carousel.setData(carouselList)
        locale = PreferenceUtils.getlang()

        clickListener()
        if (!PreferenceUtils.getPreference(PREF_DOMAIN, "").isNullOrEmpty()) {
            binding.etDomain.setText(PreferenceUtils.getPreference(PREF_DOMAIN, ""))
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(getString(R.string.CHANGE_DOMAIN)))
            changeDomain = intent.getStringExtra(getString(R.string.CHANGE_DOMAIN))
        binding.etDomain.setOnFocusChangeListener { view, b ->
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            WindowCompat.setDecorFitsSystemWindows(window, false) // Enables edge-to-edge
            edgeToEdgeFromOnlyBottom(binding.root)

        }



        binding.etDomain.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
//                binding.nestedscroll?.post {
//                    binding.nestedscroll?.fullScroll(View.FOCUS_DOWN)
//                }
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count == 0) {
                    binding.buttonProceed.setBackgroundResource(R.drawable.button_default_bg)
                } else {
                    binding.buttonProceed.setBackgroundResource(R.drawable.button_selected_bg)
                }
            }
        })


        if (PreferenceUtils.getPreference(
                PREF_DOMAIN,
                getString(R.string.empty)
            ) != null && changeDomain == null
        ) {
            val domain = PreferenceUtils.getPreference(PREF_DOMAIN, getString(R.string.empty))
            if (domain != null && domain.isNotEmpty()) {
                //intent = Intent(this, LoginActivity::class.java)
                //  intent.putExtra(getString(R.string.logo), it.logo_url)
                //startActivity(intent)
                //finish()
            }
        }

        domainViewModel.loadingState.observe(this, Observer { it ->
           if (it != null)
           {
               when (it) {
                   LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                   LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                   else -> {
                       it.msg?.let { it1 -> toast(it1) }
                       binding.includeProgress.progressBar.gone()
                   }
               }
           }

        })

        domainViewModel.dataDomain.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                if (it.bcc_id != null) {
                    PreferenceUtils.setPreference(PREF_DOMAIN, domain)
                    PreferenceUtils.setPreference(PREF_BCC_ID, it.bcc_id)
                    PreferenceUtils.setPreference(PREF_LOGO, it.logo_url)
                    if (it.dailing_code != null) {
                        setCountryCodes(it.dailing_code)
                    } else {
                        val dialingCode = ArrayList<Int>()
                        dialingCode.add(91)
                        setCountryCodes(dialingCode)
                    }


                } else {
                    if (it.message != null)
                        toast(it.message)
                }
            }
        }


        domainViewModel.dataDynamicDomain.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                if (it.code == 200) {
                    PreferenceUtils.setPreference(PREF_DOMAIN, domain)
                    PreferenceUtils.setPreference(PREF_BCC_ID, it.result?.bccId)
                    if(it.result?.logoUrl != null){
                        PreferenceUtils.setPreference(PREF_LOGO, it.result.logoUrl)
                    }
                    PreferenceUtils.setPreference(PREF_IS_ENCRYPTED, it.result?.isEncrypted ?:false)



                    if (it.result?.dailingCode != null) {
                        setCountryCodes(it.result.dailingCode)
                        //PreferenceUtils.putObject(it,PREF_COUNTRY_CODE)
                    } else {
                        val dialingCode = ArrayList<Int>()
                        dialingCode.add(91)
                        setCountryCodes(dialingCode)
                    }
                   /* if (it?.result?.mbaUrl != null && it?.result?.mbaUrl.isNotEmpty())
                        PreferenceUtils.setUpdatedApiUrlAddress(
                            it?.result?.mbaUrl?.replace(
                                "http:",
                                ""
                            )!!.replace("/", "")
                        )
                    else {
                        val baseUrl = changeBaseUrl(domain, it?.result?.mbaUrl)
                        PreferenceUtils.setUpdatedApiUrlAddress(baseUrl)
                    }*/
                    PreferenceUtils.setUpdatedApiUrlAddress(domain?:"")
                    updateBaseURL(domain?:"")
                    PreferenceUtils.setPreference(PREF_LOGO, it.result?.logoUrl?:"")

                    checkHttpsAndSetupClient(it)

                    intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (it.message != null)
                        toast(it.message)
                }
            } else {
                if (!PreferenceUtils.getString(PREF_EXCEPTION).isNullOrEmpty()) {
                    toast(PreferenceUtils.getString(PREF_EXCEPTION))
                }
                else
                    toast(getString(R.string.server_error))

                PreferenceUtils.putObject(null, PREF_LOGGED_IN_USER)
                PreferenceUtils.removeKey(PREF_TRAVEL_DATE)
                clearAndSave(this)
                PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            }
        }

        lifecycleScope.launch {
            domainViewModel.messageSharedFlow.collect{ message ->
                if (message.isNotEmpty()){
                    if(message.contains(STATUS_CODE_NO_API_RESPONSE)) {
                        val filteredMessage = message.replace(STATUS_CODE_NO_API_RESPONSE, "")
                        showToast(filteredMessage)
                    } else
                        showToast(message)
                }
            }
        }



        domainViewModel.validationData.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it.isNotEmpty())
                toast(it)
            else {
                domain?.let { it1 -> PreferenceUtils.setUpdatedApiUrlAddress(it1) }
                if (isNetworkAvailable()) {
                    // domainViewModel.domainApi(domain!!)
                    /*val domainRequest =
                        DomainRequest(domain!!, format_type, domain_method_name, ReqBody())
                    domainViewModel.domainApi(domainRequest)*/
//                    checkHttpsSupport()
                    domainViewModel.initDynamicDomain()

                } else
                    noNetworkToast()
            }
        }


        imageContainer = findViewById(R.id.image_container)
        layout = findViewById(R.id.dots_container)

        sliderItems()


//         setSlider2(itemList)
//        imageContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//
//        })
    }

    private fun checkHttpsAndSetupClient(it: DynamicDomain) {
        if (it.result?.isHttpsSupport == true) {
            PreferenceUtils.setIsHttpsSupport(true)
        } else {
            PreferenceUtils.setIsHttpsSupport(false)
        }

        stopKoin()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(getAppContext())
            modules(listOf(RepositoryModule, ViewModelModule, NetworkModule, ApiModule))
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun sliderItems() {
        val strs = arrayListOf<String>(
            getString(R.string.manage_all_your_scheduled_services_on_the_go),
            getString(R.string.booking_blocking_seats_made_simple),
            getString(R.string.download_your_key_reports_in_no_time)
        )
        adapter = SliderAdapter(imageContainer, itemList, strs)
        /*imageContainer.apply {
            adapter = adapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }*/

        imageContainer.setAdapter(adapter)

        imageContainer.clipToPadding = false
        imageContainer.clipChildren = false
        imageContainer.offscreenPageLimit = 3
        imageContainer.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        setIndicators()
        val comPosPageTran = CompositePageTransformer()
        comPosPageTran.addTransformer(MarginPageTransformer(40))
        comPosPageTran.addTransformer { page, position ->
            val r: Float = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        imageContainer.setPageTransformer(comPosPageTran)
        sliderHandle = Handler()
        sliderRun = Runnable {
            imageContainer.currentItem = imageContainer.currentItem + 1
        }
        imageContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectedDots(position)
                super.onPageSelected(position)
                sliderHandle.removeCallbacks(sliderRun)
                sliderHandle.postDelayed(sliderRun, 3000)
            }
        })

    }

    override fun onPause() {
        super.onPause()
        sliderHandle.removeCallbacks(sliderRun)
    }

    override fun onResume() {
        super.onResume()
        sliderHandle.postDelayed(sliderRun, 2000)

    }
    private fun clickListener() {
        // button_proceed.setOnClickListener(this)
        binding.buttonProceed.setSafeOnClickListener {
            domain = binding.etDomain.text.toString().trim()
            if(domain!!.isEmpty()){
                toast(getString(R.string.please_enter_valid_domain))
            }else if(domain!!.startsWith("http") || domain!!.startsWith("https") || domain!!.startsWith("www")){
                toast(getString(R.string.please_enter_domain_without_http_https_www))
            }else{
                PreferenceUtils.setUpdatedApiUrlAddress(domain!!)
                //val newHostDomain = domain!!.substringAfter("ticketsimply")
                //Timber.d("newHostDomain - $newHostDomain")

                //PreferenceUtils.setUpdatedApiUrlAddress("mba.ticketsimply$newHostDomain")
                //domainViewModel.validation(domain!!)

                if (isNetworkAvailable()) {
                    domainViewModel.initDynamicDomain()
                } else
                    noNetworkToast()
            }
        }
    }


    private fun selectedDots(position: Int) {

        val m = position % 3
        for (i in 0 until 3) {
            if (i == m) {
                dots[i].setTextColor(resources.getColor(R.color.button_secondary_bg))
            } else {
                dots[i].setTextColor(resources.getColor(R.color.white))
            }
        }
    }

    private fun setIndicators() {
        for (i in 0 until 3) {
            dots.add(i, TextView(this))
            dots[i].text = Html.fromHtml("&#9679;\t")
            dots[i].textSize = 10F
            layout!!.addView(dots[i])
        }
    }

    private fun changeLanguage(language: String) {
        setNewLocale(language)
    }

    private fun setNewLocale(language: String?) {
        if (language != null) {
            LocaleManager.setNewLocale(this, language)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val newsBase = newBase!!
        val lang: String = PreferenceUtils.getlang()
        super.attachBaseContext(MyContextWrapper.wrap(newsBase, lang))
    }
}
