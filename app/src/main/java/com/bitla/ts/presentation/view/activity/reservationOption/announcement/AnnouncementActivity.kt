package com.bitla.ts.presentation.view.activity.reservationOption.announcement

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.announcement_details_method_name
import com.bitla.ts.data.announcement_method_name
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogButtonListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.response_format
import com.bitla.ts.databinding.DialogAnnouncementMsgBinding
import com.bitla.ts.databinding.LayoutActivityAnouncementBinding
import com.bitla.ts.domain.pojo.SpinnerItems
import com.bitla.ts.domain.pojo.announcement_details_model.request.AnnouncementDetailsApiRequest
import com.bitla.ts.domain.pojo.announcement_model.request.AnnouncementApiRequest
import com.bitla.ts.domain.pojo.announcement_model.request.ReqBody
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.LanguageChildAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.constants.ANNOUNCEMENT
import com.bitla.ts.utils.constants.Announcement
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class AnnouncementActivity : BaseActivity(), AdapterView.OnItemSelectedListener,
    DialogButtonListener, OnItemClickListener {

    private lateinit var binding: LayoutActivityAnouncementBinding

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var laguageChildAdapter: LanguageChildAdapter
    private lateinit var spinnerItems: SpinnerItems
    private lateinit var spinnerItemsCity: SpinnerItems
    private var cityList: ArrayList<SpinnerItems> = arrayListOf()
    private var boardingList: ArrayList<SpinnerItems> = arrayListOf()

    private var messageList: ArrayList<String> = arrayListOf()
    private var announcementLanguagesList: ArrayList<String> = ArrayList()
    private var announcementMsgList: ArrayList<String> = ArrayList()
    private var announcementLangList: ArrayList<String> = ArrayList()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String? = ""
    private var origin: String? = ""
    private var destination: String? = ""
    private var busNumber: String? = ""
    private var departureTime: String? = ""
    private var travelName: String? = ""
    private var busType: String? = ""
    private var tvAnnouncementMsg: String? = ""
    private var tvAnnouncementMsg2: String? = ""

    private var announcementLangStart: String? = ""
    private var announcementLangEnglish: String? = ""
    private var announcementLangHindi: String? = ""
    private var announcementLangKannada: String? = ""
    private var announcementLangGujarati: String? = ""

    private var tvAnnouncementMsgEnglish: String? = ""
    private var tvAnnouncementMsgHindi: String? = ""
    private var tvAnnouncementMsgKannada: String? = ""
    private var tvAnnouncementMsgGujarati: String? = ""

    private var isEnglishPlay = false
    private var isHindiPlay = false
    private var isKannadaPlay = false
    private var isGujaratiPlay = false

    private var englishAnnouncementPosition = 0
    private var hindiAnnouncementPosition = 0
    private var kannadaAnnouncementPosition = 0
    private var gujaratiAnnouncementPosition = 0

    private var returnReasonType: String? = ""
    private lateinit var player: MediaPlayer
    private var isAnnouncementOn: Boolean = true
    private var announcementSelectedLanguagesList: ArrayList<String> = ArrayList()
    var selected = 0
    private var selectedCity = 0
    private var selectedBoardingPoint = 0
    private var selectedMessages: String? = null
    var announcement: TextToSpeech? = null
    private lateinit var dialogAnnouncementMsgBinding: DialogAnnouncementMsgBinding
    private var dialogOpenCount = 0
    private var isCcccc = false
    private var locale: String? = ""
    private var audioFile: File? = null


    private var progressAnimator: ObjectAnimator? = null
    private var totalDuration = 0L
    private var currentProgress = 0  // Stores paused position
    private var isMediaPlayerReleased = true

    override fun isInternetOnCallApisAndInitUI() {
    }

    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = LayoutActivityAnouncementBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        binding.announcementToolbar.imageOptionLayout.gone()




        getPref()

        firebaseLogEvent(
        this,
        ANNOUNCEMENT,
        loginModelPref.userName,
        loginModelPref.travels_name,
        loginModelPref.role,
        ANNOUNCEMENT,
        Announcement.ANNOUNCEMENT
        )

        if (intent.getStringExtra(getString(R.string.res_id)) != null) {
            resID = intent.getStringExtra(getString(R.string.res_id))
        }
        if (intent.getStringExtra(getString(R.string.origin)) != null) {
            origin = intent.getStringExtra(getString(R.string.origin))
        }
        if (intent.getStringExtra(getString(R.string.destination)) != null) {
            destination = intent.getStringExtra(getString(R.string.destination))
        }
        if (intent.getStringExtra(getString(R.string.bus_number)) != null) {
            busNumber = intent.getStringExtra(getString(R.string.bus_number))
        }
        if (intent.getStringExtra(getString(R.string.dep_time)) != null) {
            departureTime = intent.getStringExtra(getString(R.string.dep_time))
        }
        if (intent.getStringExtra(getString(R.string.bus_type)) != null) {
            busType = intent.getStringExtra(getString(R.string.bus_type))
        }


        binding.announcementToolbar.textHeaderTitle.text = getString(R.string.announcement)
        binding.announcementToolbar.headerTitleDesc.text = busType

        announcementRequestApi(resID.toString())
        announcementApiResponseObserver()

        binding.selectReasonType.setOnClickListener {
            openActivityForResult()
        }

        binding.reasonType.setEndIconOnClickListener {
            openActivityForResult()
        }


        binding.selectionCity.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                selectedCity = cityList[position].id
            }

        binding.selectBoadingPoint.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                selectedBoardingPoint = boardingList[position].id

                binding.linearBoardingAnnouncement.visible()

                tvAnnouncementMsg2 =
                    "$travelName Service Number $busNumber, Starting From $origin to $destination, Departure Time $departureTime, bus is $returnReasonType"

                binding.tvAnnouncementMsgContext.text = "$tvAnnouncementMsg2."

            }

        binding.selectMessage.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                selectedMessages = messageList[position]

                if (announcementSelectedLanguagesList.isNotEmpty()) {
                    binding.btnAnnoumcementMsg.setBackgroundResource(R.drawable.button_selected_bg)
                } else {
                    binding.selectMessage.setText("")
                    toast("Select Languages")
                }

                binding.tvAnnouncementMsgContext.text = "$tvAnnouncementMsg2. $selectedMessages."
            }
        binding.announcementToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        binding.btnAnnoumcementMsg.setOnClickListener {

            if (selectedCity == 0
                || selectedBoardingPoint == 0
                || returnReasonType.isNullOrEmpty()
                || selectedMessages.isNullOrEmpty()
                || announcementSelectedLanguagesList.isEmpty()
            ) {
                toast("Please select all details")
            } else {

                binding.includeProgress.progressBar.visible()
                announcementDetailsApi(resID.toString())

                if (dialogOpenCount == 0) {
                    announcementDetailsResponseObserver()
                }
            }
        }

        binding.btnClearAll.setOnClickListener {

            setLanguageAdapter(announcementLanguagesList)

            binding.selectionCity.setText("")
            binding.selectBoadingPoint.setText("")
            binding.selectReasonType.setText("")
            binding.selectMessage.setText("")
            binding.linearBoardingAnnouncement.gone()
            binding.btnAnnoumcementMsg.setBackgroundResource(R.drawable.button_default_bg)

            announcementSelectedLanguagesList.clear()

            announcementLangList.clear()
            announcementMsgList.clear()
            isEnglishPlay = false
            isHindiPlay = false
            isKannadaPlay = false
            isGujaratiPlay = false

            hindiAnnouncementPosition = 0
            kannadaAnnouncementPosition = 0
            gujaratiAnnouncementPosition = 0

        }

        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }

    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
        travelName = loginModelPref.travels_name
    }

    private fun announcementRequestApi(reservationId: String) {
        if (this.isNetworkAvailable()) {

            val announcementApiRequest = AnnouncementApiRequest(
                bccId.toString(),
                format_type,
                announcement_method_name,
                ReqBody(
                    loginModelPref.api_key,
                    reservationId,
                    locale = locale
                )
            )

            /*pickUpChartViewModel.announcementRequestAPI(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                announcementApiRequest,
                announcement_method_name
            )*/
            pickUpChartViewModel.announcementRequestAPI(
                ReqBody(
                    loginModelPref.api_key,
                    reservationId,
                    locale = locale
                ),
                announcement_method_name
            )
        } else this.noNetworkToast()
    }

    private fun announcementDetailsApi(reservationId: String) {
        if (this.isNetworkAvailable()) {


            val announcementApiRequest = AnnouncementDetailsApiRequest(
                bccId.toString(),
                format_type,
                announcement_details_method_name,
                com.bitla.ts.domain.pojo.announcement_details_model.request.ReqBody(
                    loginModelPref.api_key,
                    reservationId,
                    announcementSelectedLanguagesList,
                    selectedCity,
                    selectedBoardingPoint,
                    returnReasonType.toString(),
                    selectedMessages.toString(),
                    response_format,
                    locale = locale
                )
            )

            pickUpChartViewModel.announcementDetailsRequestApi(
                com.bitla.ts.domain.pojo.announcement_details_model.request.ReqBody(
                    loginModelPref.api_key,
                    reservationId,
                    announcementSelectedLanguagesList,
                    selectedCity,
                    selectedBoardingPoint,
                    returnReasonType.toString(),
                    selectedMessages.toString(),
                    response_format,
                    locale = locale
                ),
                announcement_details_method_name
            )


        }else this.noNetworkToast()
    }

    private fun announcementApiResponseObserver() {

        pickUpChartViewModel.announcementApiResponse.observe(this) { it ->

            Timber.d("announcementApiResponse $it")

            if (it != null) {
                when (it.code) {
                    200 -> {
                        for (i in 0 until it.cityBoardingPair.size) {

                            it.cityBoardingPair.forEach {
                                spinnerItemsCity = SpinnerItems(it.cityId, it.cityName)
                                cityList.add(spinnerItemsCity)
                                Timber.d("announcementCityList ${cityList[i].id}")
                            }

                            it.cityBoardingPair[i].boardingPoint.forEach {
                                spinnerItems = SpinnerItems(it.stageId, it.stageName)
                                boardingList.add(spinnerItems)
                            }
                        }
                        for (i in 0 until it.announcementLanguages.size) {
                            announcementLanguagesList.add(it.announcementLanguages[i])
                        }

                        for (i in 0 until it.message.size) {
                            messageList.add(it.message[i])
                        }


                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }
                    else -> {
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> toast(it1) }
                        }
                    }
                }

                setLanguageAdapter(announcementLanguagesList)
                setCity()
                setBoardingPoint()
                setMessage()
//                announcementDetailsApi(resID.toString())

            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setLanguageAdapter(announcementLanguagesList: List<String>) {
        layoutManager = GridLayoutManager(applicationContext, 3)
        binding.rvLanguage.layoutManager = layoutManager
        binding.rvLanguage.setHasFixedSize(true)
        laguageChildAdapter = LanguageChildAdapter(
            this,
            announcementLanguagesList,
            announcementSelectedLanguagesList,
            isCcccc
        )
        binding.rvLanguage.adapter = laguageChildAdapter
    }

    private fun setCity() {
        binding.selectionCity.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                cityList
            )
        )
    }

    private fun setBoardingPoint() {
        binding.selectBoadingPoint.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                boardingList
            )
        )
    }

    private fun setMessage() {
        binding.selectMessage.setAdapter(
            ArrayAdapter(
                this,
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                messageList
            )
        )
    }

    private fun announcementDetailsResponseObserver() {

        pickUpChartViewModel.announcementDetailsApiResponse.observe(this) { it ->

            if (it != null) {
                if (it.code == 200) {

                    binding.includeProgress.progressBar.gone()

                    if (it.result != null) {

                        for (i in 0 until it.result.announcementMessage.size) {

                            it.result.announcementMessage[i].message?.let { it1 ->
                                announcementMsgList.add(it1)
                            }

                            announcementLangList.add(it.result.announcementMessage[i].language)

                            if (announcementLangList[i] == "English") {

                                if (!isHindiPlay && !isKannadaPlay && !isGujaratiPlay) {

                                    tvAnnouncementMsgEnglish = announcementMsgList[i]
                                    announcementLangEnglish = announcementLangList[i]
                                    announcementLangStart = announcementLangList[i]

                                    announcementMessage(tvAnnouncementMsgEnglish.toString())

                                    isEnglishPlay = true
                                    Timber.d("Announce: English if")
                                } else {
                                    englishAnnouncementPosition = i
                                    tvAnnouncementMsgEnglish = announcementMsgList[i]
                                    Timber.d("Announce: English else")
                                    isEnglishPlay = false
                                }
                            } else if (announcementLangList[i] == "Hindi") {

                                if (!isEnglishPlay && !isKannadaPlay && !isGujaratiPlay) {
                                    tvAnnouncementMsgHindi = announcementMsgList[i]
                                    announcementLangHindi = announcementLangList[i]
                                    announcementLangStart = announcementLangList[i]

                                    announcementMessage(tvAnnouncementMsgHindi.toString())
                                    isHindiPlay = true
                                    Timber.d("Announce: Hindi if")

                                } else {
                                    hindiAnnouncementPosition = i
                                    tvAnnouncementMsgHindi = announcementMsgList[i]
                                    Timber.d("Announce: Hindi else")
                                    isHindiPlay = false

                                }
                            } else if (announcementLangList[i] == "Kannada") {

                                if (!isEnglishPlay && !isHindiPlay && !isGujaratiPlay) {
                                    tvAnnouncementMsgKannada = announcementMsgList[i]
                                    announcementLangKannada = announcementLangList[i]
                                    announcementLangStart = announcementLangList[i]

                                    announcementMessage(tvAnnouncementMsgKannada.toString())
                                    Timber.d("Announce: kannada if")
                                    isKannadaPlay = true

                                } else {
                                    kannadaAnnouncementPosition = i
                                    tvAnnouncementMsgKannada = announcementMsgList[i]
                                    Timber.d("Announce: kannada else")
                                    isKannadaPlay = false

                                }
                            } else if (announcementLangList[i] == "Gujarati") {

                                if (!isEnglishPlay && !isKannadaPlay && !isHindiPlay) {
                                    tvAnnouncementMsgGujarati = announcementMsgList[i]
                                    announcementLangGujarati = announcementLangList[i]
                                    announcementLangStart = announcementLangList[i]

                                    announcementMessage(tvAnnouncementMsgGujarati.toString())
                                    Timber.d("Announce: gujarati if")
                                    isGujaratiPlay = true

                                } else {
                                    gujaratiAnnouncementPosition = i
                                    tvAnnouncementMsgGujarati = announcementMsgList[i]
                                    isGujaratiPlay = false
                                    Timber.d("Announce: gujarati else")

                                }
                            }

                        }

                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun announcementMessage(msg: String) {

        //resetVariablesAndDeletefile()


        progressAnimator = null
        totalDuration = 0L
        currentProgress = 0

        dialogOpenCount++
        val builder = AlertDialog.Builder(this).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background

        dialogAnnouncementMsgBinding =
            DialogAnnouncementMsgBinding.inflate(LayoutInflater.from(this))

        builder.setCancelable(false)
        dialogAnnouncementMsgBinding.header.text = title
        dialogAnnouncementMsgBinding.msgContext.text = "$msg."

        when (announcementLangStart) {

            "English" -> {

                convertTextToSpeechAndPlayAudio("passengers please pay attention, $msg", null)
                //speakEnglishAnnouncement(msg)
            }
            "Hindi" -> {
                convertTextToSpeechAndPlayAudio("yaatrigan kripya dhyan deejiye, $msg", Locale("hi", "IN"))
                //speakHindiAnnouncement(msg)
            }
            "Kannada" -> {
                convertTextToSpeechAndPlayAudio("Prayāṇikarē dayaviṭṭu, $msg", Locale("kn", "IN"))
                //speakKannadaAnnouncement(msg)
            }
            "Gujarati" -> {
                convertTextToSpeechAndPlayAudio("kr̥pā dhyāna āpō, $msg", Locale("gu", "IN"))
                //speakGujaratiAnnouncement(msg)
            }

//                dialogAnnouncementMsgBinding.imgAnnouncementPlay.setOnClickListener {
//
//                    if (IsAnnouncementOn){
//                        getSound(R.raw.beep_sound, "english")
//                    }
//
//                    dialogAnnouncementMsgBinding.imgAnnouncementPlay.setImageResource(R.drawable.ic_pause)
//                }
        }

        dialogAnnouncementMsgBinding.imgAnnouncementPlay.setOnClickListener {
            if(::player.isInitialized && !isMediaPlayerReleased) {

                if(player.isPlaying){
                    player.pause()
                    dialogAnnouncementMsgBinding.imgAnnouncementPlay.setImageResource(R.drawable.ic_play)
                    pauseProgressAnimation()

                } else {

                    if(currentProgress.toLong() == totalDuration || currentProgress == 0) {
                        stopProgressAnimation()
                        playBeepAudio()
                    } else {
                        player.start()
                        dialogAnnouncementMsgBinding.imgAnnouncementPlay.setImageResource(R.drawable.ic_pause)

                        resumeProgressAnimation()
                    }
                }
            }
        }

        dialogAnnouncementMsgBinding.btnStopPlaying.setOnClickListener {

            announcementLangList.clear()
            announcementMsgList.clear()
            isEnglishPlay = false
            isHindiPlay = false
            isKannadaPlay = false
            isGujaratiPlay = false

            hindiAnnouncementPosition = 0
            kannadaAnnouncementPosition = 0
            gujaratiAnnouncementPosition = 0

            isMediaPlayerReleased = true

            dialogAnnouncementMsgBinding.imgAnnouncementPlay.setImageResource(R.drawable.ic_play)

            try {
                announcement?.stop()
                isAnnouncementOn = true
                announcement?.shutdown()
                player.release()
                isMediaPlayerReleased = true

            } catch (e: Exception) {
                Timber.d("$e")
            }
            builder.cancel()
        }

        //resetVariablesAndDeletefile()

        builder.setView(dialogAnnouncementMsgBinding.root)
        builder.show()
    }

    private fun resetVariablesAndDeletefile() {
        val previousAudioFile = File(getExternalFilesDir(null), "tts_audio.wav")

        if (previousAudioFile.exists()) {
            previousAudioFile.delete()
        }

        audioFile = null
        progressAnimator = null
        totalDuration = 0L
        currentProgress = 0
        announcement = null

    }
    private fun convertTextToSpeechAndPlayAudio(tvAnnouncementMsgEnglish: String, locale: Locale?) {
        audioFile = File(getExternalFilesDir(null), "tts_audio.wav")

        announcement = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {

                if(locale != null) {
                    announcement!!.language = locale
                } else {
                    announcement!!.setLanguage(Locale.ENGLISH)
                }

                announcement?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        runOnUiThread {
                            playBeepAudio()
                        }
                    }

                    override fun onError(utteranceId: String?) {}
                })

                val params = Bundle()
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TTS_AUDIO")

                val result = announcement?.synthesizeToFile(tvAnnouncementMsgEnglish, params, audioFile, "TTS_AUDIO")

                if (result == TextToSpeech.ERROR) {
                    toast(getString(R.string.opps))
                }
            }
        }
    }

    private fun playBeepAudio() {

        player = MediaPlayer.create(this, R.raw.beep_sound)
        player.setOnCompletionListener {
            player = MediaPlayer().apply {
                setDataSource(audioFile?.absolutePath)
                prepare()
                start()

                setOnCompletionListener {
                    dialogAnnouncementMsgBinding.imgAnnouncementPlay.setImageResource(R.drawable.ic_play)
                }
            }

        }
        player.start()

        totalDuration = getTotalDuration(audioFile).toLong()
        resumeProgressAnimation()

        dialogAnnouncementMsgBinding.imgAnnouncementPlay.setImageResource(R.drawable.ic_pause)
        isMediaPlayerReleased = false

    }

    private fun getTotalDuration(file: File?): Int {

        val tempBeepPlayer = MediaPlayer.create(this, R.raw.beep_sound)

        val tempAudioPlayer = MediaPlayer().apply {
            setDataSource(file?.absolutePath)
            prepare()
        }

        val totalDuration = tempBeepPlayer.duration + tempAudioPlayer.duration

        tempBeepPlayer.release()
        tempAudioPlayer.release()

        return totalDuration
    }

    private fun startProgressAnimation() {
        progressAnimator?.cancel()  // Cancel any previous animation
        dialogAnnouncementMsgBinding.progressBar.max = this@AnnouncementActivity.totalDuration.toInt()
        progressAnimator = ObjectAnimator.ofInt(dialogAnnouncementMsgBinding.progressBar, "progress", currentProgress, this@AnnouncementActivity.totalDuration.toInt()).apply {
            duration = this@AnnouncementActivity.totalDuration // Ensure duration is in milliseconds
            start()
        }
    }

    private fun pauseProgressAnimation() {
        progressAnimator?.pause()
        currentProgress = dialogAnnouncementMsgBinding.progressBar.progress  // Save current progress
    }

    private fun resumeProgressAnimation() {
        progressAnimator?.resume() ?: startProgressAnimation()
    }

    private fun stopProgressAnimation() {
        progressAnimator?.cancel()
        progressAnimator = null

        currentProgress = 0
        dialogAnnouncementMsgBinding.progressBar.max = 0
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                returnReasonType =
                    result.data?.getStringExtra(getString(R.string.select_reason_type)).toString()
                binding.selectReasonType.setText(returnReasonType)

                tvAnnouncementMsg =
                    "$travelName Service Number $busNumber, Starting From $origin to $destination, Departure Time $departureTime, bus is $returnReasonType"

//                binding.tvAnnouncementMsgContext.text = tvAnnouncementMsg

            }
        }

    private fun openActivityForResult() {
        val intent = Intent(this, ReasonTypeListActivity::class.java)
        intent.putExtra(getString(R.string.res_id), resID)
        resultLauncher.launch(intent)
    }

    override fun onLeftButtonClick() {
    }

    override fun onRightButtonClick() {
        announcementMsgList.clear()

    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        cityList.clear()
        boardingList.clear()
        announcementSelectedLanguagesList.clear()
    }
}