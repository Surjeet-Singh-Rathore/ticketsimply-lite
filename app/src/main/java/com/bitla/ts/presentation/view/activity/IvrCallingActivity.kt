package com.bitla.ts.presentation.view.activity

import android.content.*
import android.os.Build
import android.widget.*
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.ivr_call.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.view_reservation.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import org.koin.androidx.viewmodel.ext.android.*
import toast

class IvrCallingActivity : BaseActivity() {
    private lateinit var binding: ActivityIvrCallingBinding
    private var resID: String? = null
    private val ivrCallingViewModel by viewModel<IvrCallingViewModel<Any>>()
    private lateinit var respHash: ArrayList<RespHash>
    private var selectedBoardingPointId = ""
    private lateinit var currentRespHash:RespHash
    private var loginModelPref: LoginModel = LoginModel()

    override fun initUI() {
        setContentView(R.layout.activity_ivr_calling)


        binding = ActivityIvrCallingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()
        if (intent.hasExtra("resp_hash")){
            val temp = intent.getStringExtra("resp_hash")
            currentRespHash = stringToJson(temp)
        }

        binding.layoutToolbar1.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
        binding.layoutToolbar1.textHeaderTitle.text = PreferenceUtils.getString("ViewReservation_name")
        binding.layoutToolbar1.headerTitleDesc.text = "BP: ${currentRespHash.name.substringBefore("-")}"

        getIvrDetailsObserver()



        binding.icArrowDown1.setOnClickListener {
            /*if (binding.summaryRV1.visibility == View.VISIBLE) {
                binding.summaryRV1.gone()
                binding.summaryImg1.animate().rotation(0f).setDuration(500)
            } else {
                binding.summaryRV1.visible()
                binding.summaryImg1.animate().rotation(-180.0f).setDuration(500)

            }*/
        }
        val spinnerItemsList = ArrayList<SpinnerItems>()
        respHash.forEach { item->
            val spinnerItem = SpinnerItems(
                id = item.id,
                value = item.name
            )
            spinnerItemsList.add(spinnerItem)
        }
        binding.timeInMinsTv1.setAdapter(

            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                spinnerItemsList
            )
        )
        binding.myButton1.setOnClickListener {
            callIvrCallApi(boardingId = currentRespHash.id.toString(),option = "1")

        }

        binding.myButton3.setOnClickListener {
            callIvrCallApi(boardingId = selectedBoardingPointId,option = "3")
        }

        binding.timeInMinsTv1.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                selectedBoardingPointId = spinnerItemsList.get(position).id.toString()
                  //toast(selectedBoardingPointId)
            }

    }


    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        PreferenceUtils.getBoarding()
        resID = PreferenceUtils.getPreference(
            PREF_RESERVATION_ID, 0
        ).toString()
        respHash = PreferenceUtils.getRespHashBoardingList() ?: arrayListOf()
    }

    private fun callIvrCallApi(boardingId:String,option:String){
        val ivrCallRequest = IvrCallRequest(
            resId = resID ?: "" ,
            apiKey = loginModelPref.api_key,
            boardingId = boardingId ,
           option = option
        )
        ivrCallingViewModel.ivrCallingDetailsApi(
//            resId = resID ?: "" ,
//            apiKey = loginModelPref.api_key,
//            boardingId = boardingId ,
//            option = option

         ivrCallRequest

        )


    }

    private fun getIvrDetailsObserver(){
      ivrCallingViewModel.ivrCallingDetails.observe(this){
          if(it!=null){
              try {
                  if (it != null) {
                      when (it.status) {
                          200 -> {


                             toast(it.msg)

                          }

                          401 -> {
                              openUnauthorisedDialog()

                          }

                          else -> {
                              toast(it.msg.toString())
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
      }


private fun openUnauthorisedDialog() {
    DialogUtils.unAuthorizedDialog(this,
        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
        object : DialogSingleButtonListener {
            override fun onSingleButtonClick(str: String) {
                if (str == getString(R.string.unauthorized)) {

                    val intent = Intent(
                        this@IvrCallingActivity,
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


    override fun isInternetOnCallApisAndInitUI() {
    }
}