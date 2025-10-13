package com.bitla.ts.presentation.view.activity

import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bitla.ts.R
import com.bitla.ts.databinding.ActivitySelfAuditFormBinding
import com.bitla.ts.domain.pojo.self_audit_question.response.Result
import com.bitla.ts.domain.pojo.submit_self_audit_form.request.Answer
import com.bitla.ts.domain.pojo.submit_self_audit_form.request.BpQuestion
import com.bitla.ts.domain.pojo.submit_self_audit_form.request.NormalQuestion
import com.bitla.ts.domain.pojo.submit_self_audit_form.request.SubmitSelfAuditFormRequest
import com.bitla.ts.presentation.adapter.sellfAuditFormAdapters.SelfAuditQuestionsAdapter
import com.bitla.ts.presentation.viewModel.SelfAuditViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.edgeToEdgeFromOnlyBottom
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import dpToPx
import gone
import isNetworkAvailable
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible

class SelfAuditFormActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySelfAuditFormBinding
    private val viewModel by viewModel<SelfAuditViewModel>()
    private var apiKey = ""
    private var resID = ""
    private var normalTypeMap= mapOf<String, String>()
    private var normalTypeList= arrayListOf<NormalQuestion>()
    private var bpTypeList= arrayListOf<BpQuestion>()
    private var ratingSelectedOption=""
    private var ratingQuestionId=""
    private var bpTypeMap= mapOf<String, ArrayList<Answer>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivitySelfAuditFormBinding.inflate(layoutInflater)
        setObservers()




        binding.btnSubmit.setOnClickListener {
            normalTypeList.clear()
            bpTypeList.clear()
            normalTypeMap.forEach { key, value ->
                normalTypeList.add(NormalQuestion(question_id = key, answer_id = value))
            }
            bpTypeMap.forEach { key, value ->
                bpTypeList.add(BpQuestion(answer_list = value, question_id = key))

            }

            val submitFormRequest= SubmitSelfAuditFormRequest(
                api_key = apiKey,
                res_id = resID,
                normal_questions = normalTypeList,
                bp_questions = bpTypeList,
                rating =  com.bitla.ts.domain.pojo.submit_self_audit_form.request.Rating(answer_id = ratingSelectedOption, rating_id = ratingQuestionId),
                remarks =binding.remarksET.text.toString()
            )

//            Log.d("GauravTest00: " , submitFormRequest.toString())
            submitFormAPi(submitFormRequest)

//            onBackPressed()
        }
        getIntentData()
        setContentView(binding.root)

        initUI()
    }

    private fun getIntentData() {
        apiKey = PreferenceUtils.getLogin().api_key
        resID= intent.extras?.get("pickUpResid").toString()
    }

    private fun initUI() {
        binding.toolbarHeading.text = getString(R.string.self_audit)

        WindowCompat.setDecorFitsSystemWindows(window, false)


       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            //edgeToEdge(binding.root)
            ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(
                    systemBars.left + dpToPx(16, view.context),
                    systemBars.top + dpToPx(10, view.context),
                    systemBars.right + dpToPx(16, view.context),
                    systemBars.bottom + dpToPx(16, view.context)
                )
                insets
            }
      //  }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        getSelfAuditQuestions(apiKey,resID)
    }
    private fun getSelfAuditQuestions(apiKey: String, resId:String) {
        if (isNetworkAvailable()) {
            binding.progressBarLayout.visible()
            viewModel.getSelfAuditListApi(apiKey, resId)
        } else {
            toast(getString(R.string.network_not_available))
        }
    }


    private fun setQuestionAdapter(questionData: Result) {


        val adapter=SelfAuditQuestionsAdapter(this, questionData,
            {optionId, stageId, questionId, questionType ->

                if (bpTypeMap.containsKey(questionId)){
                    val answerData= bpTypeMap.get(questionId)
                    var stageList= arrayListOf<String>()
                    answerData?.forEach {
                        stageList.add(it.boarding_id)
                    }
                    if (stageList.contains(stageId)){
                        answerData?.get(stageList.indexOf(stageId))?.answer_id= optionId
                    }else{
                        answerData?.add(Answer(boarding_id = stageId, answer_id = optionId))

                    }


                }else{
                    bpTypeMap += Pair(questionId, arrayListOf(Answer(boarding_id = stageId, answer_id = optionId)))
                }
            },
            {optionId, questionId, questionType ->

            normalTypeMap+= Pair<String, String>(questionId,optionId)

        })
        binding.questionRV.adapter=adapter
    }


    private fun setRatingView(questionData: Result) {
        if (questionData.ratings != null) {

            binding.ratingCL.visible()
            binding.dropdownRatingExpand.setOnClickListener {
                binding.dropdownRatingExpand.gone()
                binding.dropdownRatingCollapse.visible()
                binding.ratingRadioGroup.visible()
            }
            binding.dropdownRatingCollapse.setOnClickListener {
                binding.dropdownRatingCollapse.gone()
                binding.dropdownRatingExpand.visible()
                binding.ratingRadioGroup.gone()
            }
            binding.ratingTitleTV.text = questionData.ratings.rating_title
            val list = questionData.ratings.rating_options.distinctBy { it }

            list.forEachIndexed { index, option ->
                val radioButton = RadioButton(this)
                radioButton.id = option.id.toInt() // Unique ID for each RadioButton

                // Replace <Star> with an image in the text
                val spannableString = SpannableString(option.title)
                val starStart = option.title.indexOf("<star>")
                if (starStart != -1) {
                    val drawable = getDrawable(R.drawable.star_icon)!! // Your star drawable resource
                    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                    val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(
                        imageSpan,
                        starStart,
                        starStart + "<star>".length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                radioButton.text = spannableString
                radioButton.setPadding(0, 6, 0, 6)
                radioButton.gravity = Gravity.TOP

                radioButton.setOnClickListener {
                    ratingSelectedOption = option.id
                }
                binding.ratingRadioGroup.addView(radioButton)
            }
        }
    }



    private fun setObservers() {

        viewModel.selfAuditListResponse.observe((this)) {
            binding.progressBarLayout.gone()
                    if (it != null) {
                        when (it.code) {
                            200 -> {
                                ratingQuestionId= it.result.ratings.id
                                binding.emailValueTV.text=it.result?.email
                                binding.dateValueTV.text=it.result?.date
                                binding.tripIdValueTV.text=it.result?.trip_id
                                binding.operatorValueTV.text=it.result?.operator_name
                                binding.routeValueTV.text= it.result?.route_name

                                setRatingView(it.result)
                                setQuestionAdapter(it.result)
                            }
                            else -> {
                                toast("$it")
                            }
                        }
                    } else {
                        toast(getString(R.string.server_error))
                    }
        }


        viewModel.selfAuditFormSubmitResponse.observe(this){
            binding.progressBarLayout.gone()
            if (it != null) {
                when (it.code) {
                    200 -> {
                        toast(it.message)
                        onBackPressed()
                    }
                    else -> {
                        toast(it.result.message)
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }


    private fun submitFormAPi( selfAuditFormSubmitRequest: SubmitSelfAuditFormRequest
    ){
            if (isNetworkAvailable()) {
                binding.progressBarLayout.visible()
                viewModel.selfAuditFormApi(selfAuditFormSubmitRequest)
            } else {
                toast(getString(R.string.network_not_available))
            }
        }
}