package com.bitla.ts.presentation.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.focus.*
import androidx.lifecycle.*
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.account_info.response.*
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking.PayGayType
import com.bitla.ts.domain.pojo.booking_custom_request.*
import com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.response.*
import com.bitla.ts.domain.pojo.coupon.*
import com.bitla.ts.domain.pojo.coupon.request.*
import com.bitla.ts.domain.pojo.custom_applied_coupons.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.fare_breakup.response.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.passenger_details_result.*
import com.bitla.ts.domain.pojo.passenger_history.*
import com.bitla.ts.domain.pojo.photo_block_tickets.request.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.child_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.service_details_response.Body
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import kotlinx.coroutines.flow.*
import timber.log.*
import java.text.*
import java.util.*
import kotlin.math.*

class PassengerDetailsViewModel<T : Any?> : ViewModel() {

    var showShimmer by mutableStateOf(false)
    var toolbarSubTitleInfo by mutableStateOf("")
    var phoneBookingCardColor by mutableStateOf(PHONE_BOOKING_NOT_SELECTED)
    var selectedCityName by mutableStateOf("")
    var selectedCityId by mutableIntStateOf(0)
    var onBehalfOfAgentName by mutableStateOf("")
    var onBehalfOfAgentId by mutableIntStateOf(0)
    var selectedBranchName by mutableStateOf("")
    var selectedBranchId by mutableIntStateOf(0)
    var selectedUserId by mutableIntStateOf(0)

    var branchRoleDiscountType by mutableStateOf("")
    var discountType by mutableStateOf("")
    var discountValue by mutableStateOf("")
    var branchDiscountValue by mutableStateOf("")
    var roleDiscountValue by mutableStateOf("")
    var applyRoleOrBranchDiscountAtTimeOfBooking by mutableStateOf(false)

    var selectedUserName by mutableStateOf("")
    var bookingReferenceNo by mutableStateOf("")
    var amountPaidOffline by mutableStateOf(false)
    private var _phoneBlockTime by mutableStateOf<ResourceProvider.TextResource?>(null)

    // booking types
    var isBookingTypeCardExpanded by mutableStateOf(false)
    var isStatusBookingCardExpanded by mutableStateOf(false)
    val phoneBlockTime get() = _phoneBlockTime
    var bookingAgentList: MutableList<SpinnerItems> = mutableStateListOf()
    var userList: MutableList<SpinnerItems> = mutableStateListOf()
    var branchList: MutableList<SpinnerItems> = mutableStateListOf()

//    var privileges: PrivilegeResponseModel?= null


    // payment sub options
    val paymentSubOptionsList = mutableStateListOf<SearchModel>()
    var selectedSubPaymentOptionId by mutableStateOf("QR")
    var selectedSubPaymentOption by mutableStateOf(ResourceProvider.TextResource.fromStringId(R.string.pay_via_qr))
    // payment options
    val paymentOptionsList = mutableStateListOf<SearchModel>()
    var selectedPaymentOptionId by mutableIntStateOf(1)
    var selectedPaymentOption by mutableStateOf(ResourceProvider.TextResource.fromStringId(R.string.cash))
    var isPaymentOptionCardVisible by mutableStateOf(true)
    var isPaymentOptionClicked by mutableStateOf(false)
    var phoneBlock by mutableStateOf(false)
    val otherPaymentOptions = mutableStateListOf<PayGayType>()
    var walletPaymentOptions = mutableStateListOf<WalletPaymentOption>()
    var isCancelledClicked by mutableStateOf(false)
    var isHandlerRunning by mutableStateOf(false)
    var delay by mutableIntStateOf(1500)
    var selectedWalletUpiOptionName by mutableStateOf<String?>(null)
    var selectedWalletOrUpi by mutableStateOf<String?>(null)
    var selectedWalletUpiOptionId by mutableStateOf<Int?>(null)
    var selectedOtherPaymentOption by mutableStateOf<String?>(null)
    var walletMobileNo by mutableStateOf<String>("")
    var creditDebitCardNo by mutableStateOf<String?>(null)
    var selectedSeatNo by mutableStateOf("")
    var isPhoneBlocking by mutableStateOf(false)
    var isPhoneBlockTicket by mutableStateOf(false)
    var selectedGenderMale by mutableStateOf(false)
    var selectedGenderFeMale by mutableStateOf(false)

    var selectedRadioWalking by mutableStateOf(true)
    var selectedRadioPhoneBooking by mutableStateOf(false)
    var selectedRadioSubAgentBooking by mutableStateOf(false)
    var selectedRadioOnlineAgent by mutableStateOf(false)
    var selectedRadioOfflineAgent by mutableStateOf(false)
    var selectedRadioBranch by mutableStateOf(false)


    private var _isBookingTypeCardVisible by mutableStateOf(false)
    val isBookingTypeCardVisible get() = _isBookingTypeCardVisible

    private var _isPromotionCouponVisible by mutableStateOf(true)
    val isPromotionCouponVisible get() = _isPromotionCouponVisible

    private var _isPhoneBookingVisible by mutableStateOf(false)
    val isPhoneBookingVisible get() = _isPhoneBookingVisible

    private var _isOnlineViewVisible by mutableStateOf(false)
    val isOnlineViewVisible get() = _isOnlineViewVisible

    private var _isOfflineViewVisible by mutableStateOf(false)
    val isOfflineViewVisible get() = _isOfflineViewVisible


    private var _isSubAgentViewVisible by mutableStateOf(false)
    val isSubAgentViewVisible get() = _isSubAgentViewVisible


    private var _isBranchViewVisible by mutableStateOf(false)
    val isBranchViewVisible get() = _isBranchViewVisible


    private var _isPhoneDialogVisible by mutableStateOf(false)
    val isPhoneDialogVisible get() = _isPhoneDialogVisible

    private var _isStatusCardVisible by mutableStateOf(false)
    val isStatusCardVisible get() = _isStatusCardVisible

    private var _isPhoneBlockDateTimeVisible by mutableStateOf(false)
    val isPhoneBlockDateTimeVisible get() = _isPhoneBlockDateTimeVisible

    private var _roleType by mutableStateOf<String?>(null)
    val roleType get() = _roleType

    private var _selectedBookingType by mutableStateOf<ResourceProvider.TextResource?>(null)
    val selectedBookingType get() = _selectedBookingType

    private var _selectedBookingTypeId by mutableIntStateOf(0)
    val selectedBookingTypeId get() = _selectedBookingTypeId

    private var _selectedStatusType by mutableStateOf<ResourceProvider.TextResource?>(null)
    val selectedStatusType get() = _selectedStatusType

    private var _isAgentLogin by mutableStateOf(false)
    val isAgentLogin get() = _isAgentLogin

    var privilegeResponseModel by mutableStateOf<PrivilegeResponseModel?>(null)
    var passengerHistoryModel by mutableStateOf<PassengersHistory?>(null)
    private var serviceDetailsModel by mutableStateOf<Body?>(null)
    var couponResponse by mutableStateOf<CouponResponse?>(null)
    var discountParams by mutableStateOf<DiscountParams?>(null)

    var loginModelPref by mutableStateOf<LoginModel?>(null)
    var bccId by mutableIntStateOf(0)
    var source by mutableStateOf("")
    var destination by mutableStateOf("")
    var sourceId by mutableStateOf("")
    var destinationId by mutableStateOf("")
    var resId by mutableLongStateOf(0L)
    var locale by mutableStateOf("")
    var travelDate by mutableStateOf("")
    var routeId by mutableStateOf<Int?>(null)
    var isBima by mutableStateOf<Boolean?>(null)
    var isOwnRoute by mutableStateOf(false)
    var busType by mutableStateOf<String?>(null)
    var deptTime by mutableStateOf<String?>(null)
    var serviceNumber by mutableStateOf<String?>(null)
    var arrDate by mutableStateOf<String?>(null)
    var arrTime by mutableStateOf<String?>(null)
    var deptDate by mutableStateOf<String?>(null)
    var droppingPoint by mutableStateOf<String?>(null)
    var droppingId by mutableStateOf<Int?>(null)
    var boardingPoint by mutableStateOf<String?>(null)
    var boardingId by mutableStateOf<Int?>(null)
    var primaryMobileNo by mutableStateOf("")
    var alternateMobileNo by mutableStateOf("")
    var primaryCountryCode by mutableStateOf("")
    var alternateCountryCode by mutableStateOf("")
    var emailId by mutableStateOf("")
    var emergencyName by mutableStateOf("")
    var amountCurrency by mutableStateOf<String?>("")
    var currencyFormat by mutableStateOf<String>("#,##,###.00")
    var isExtraSeats by mutableStateOf(false)
    var extraSeatFirstPosition by mutableStateOf<Int?>(0)
    var noOfSeats by mutableStateOf<String?>("0")
    var totalNetAmount by mutableStateOf<String?>("")
    var totalFare by mutableDoubleStateOf(0.0)
    var individualDiscountAmount by mutableIntStateOf(0)
    var selectedExtraSeatDetails = java.util.ArrayList<SeatDetail>()
//    private var selectedSeatDetails = mutableStateListOf<SeatDetail>()

    // privileges
    var mobileNoPrivilege: MutableState<String> = mutableStateOf("")
    var alternateNoPrivilege: MutableState<String> = mutableStateOf("")
    var phoneValidationCountPrivilege: MutableState<Int?> = mutableStateOf(null)
    var emailPrivilege: MutableState<String> = mutableStateOf("")
    var namePrivilege: MutableState<String> = mutableStateOf("")
    var firstNamePrivilege: MutableState<String> = mutableStateOf("")
    var lastNamePrivilege: MutableState<String> = mutableStateOf("")
    var agePrivilege: MutableState<String> = mutableStateOf("")
    var genderPrivilege: MutableState<String> = mutableStateOf("")
    var idTypePrivilege: MutableState<String> = mutableStateOf("")
    var idNumberPrivilege: MutableState<String> = mutableStateOf("")
    var isAdditionalFarePrivilege: MutableState<Boolean?> = mutableStateOf(false)
    var isDiscountPrivilege: MutableState<Boolean?> = mutableStateOf(false)

    // meal
    var isMealNoType: MutableState<Boolean?> = mutableStateOf(false)
    var isMealRequired: MutableState<Boolean?> = mutableStateOf(null)
    var isFreezeMealSelection: MutableState<Boolean?> = mutableStateOf(null)
    var isSelectedMealTypes: MutableState<Any?> = mutableStateOf(null)

    //  insurance
    val isInsuranceChecked: MutableState<Boolean> = mutableStateOf(false)
    var allowQoalaInsurance: MutableState<Boolean?> = mutableStateOf(false)
    var insuranceMandatoryForBookings: MutableState<Boolean?> = mutableStateOf(false)
    var enableInsuranceCheckboxForBooking: MutableState<Boolean?> = mutableStateOf(false)
    var hasPassengerData: MutableState<Boolean?> = mutableStateOf(false)

    val items = MutableLiveData<MutableList<PassengerDetailsResult>>(mutableListOf())
    var passengerDataList = mutableStateListOf<PassengerDetailsResult>()
    var selectedSeatDetails = mutableListOf(SeatDetail())
    var idTypeList: MutableList<SpinnerItems> = mutableListOf()
    val mealList = mutableStateListOf<SpinnerItems>()
    private val contactList = mutableStateListOf<ContactDetail>()
    private val _listPassengerFlow = MutableStateFlow(passengerDataList)
    val listPassengerFlow: StateFlow<List<PassengerDetailsResult>> get() = _listPassengerFlow
    val checkedCopyState = mutableStateOf(false)

    var walkinId by mutableIntStateOf(0) //fixed
    private var phoneBookingId by mutableIntStateOf(4) //fixed
    private var onlineAgentId by mutableIntStateOf(1) //fixed
    private var offlineAgentId by mutableIntStateOf(2) //fixed
    private var branchId by mutableIntStateOf(3) //fixed
    private var statusPhoneBookingId by mutableIntStateOf(5) //fixed
    private var statusConfirmBookingId by mutableIntStateOf(6) //fixed


    private val _bookingTypes = mutableStateListOf<SpinnerItems>()
    val bookingTypes = _bookingTypes

    private val _bookingStatusTypes = mutableStateListOf<SpinnerItems>()
    val bookingStatusTypes = _bookingStatusTypes
    var bookingCustomRequest by mutableStateOf<BookingCustomRequest?>(null)
    var agentType by mutableStateOf("")

    var isCouponExpand by mutableStateOf(false)
    var boardingStageDetail by mutableStateOf<StageDetail?>(null)
    var droppingStageDetail by mutableStateOf<StageDetail?>(null)
    var isExtraSeat by mutableStateOf(false)

    var countryList: MutableList<Int> = mutableStateListOf()
    var isRetrieveClicked: MutableState<Boolean> = mutableStateOf(false)
    val showDialog: MutableState<Boolean> = mutableStateOf(false)
    var checkedPassengerList = mutableStateListOf<PassengerHistoryModel>()
    var passengerHistoryList = mutableListOf<PassengerHistoryModel>()
    val isRoundTrip by mutableStateOf(false) //fixed
    val returnBoardingPoint by mutableStateOf("") //fixed
    val returnDroppingPoint by mutableStateOf("") //fixed

    // fare breakup request
    var useSmartMiles by mutableStateOf("false") //fixed
    val offerCoupon by mutableStateOf("") //fixed
    val promoCoupon by mutableStateOf("") //fixed
    val privilegeCardNo by mutableStateOf("") //fixed
    var totalFareString by mutableStateOf("0.0") //fixed
    var isFreeBookingAllowed by mutableStateOf("1") // fixed
    var vipTicket by mutableStateOf("1") // fixed
    var isMatchPrepostponeAmount by mutableStateOf("false") // fixed
    var allowPrePostPoneOtherBranch by mutableStateOf("false") // fixed
    var corpCompanyId by mutableStateOf("") // fixed
    var discountOnTotalAmount by mutableStateOf("0")
    var country by mutableStateOf("")

    // book ticket request
    var isUpiPayment by mutableStateOf(false) //fixed
    var isRoundTripSeat by mutableStateOf(false) //fixed
    var isPermanentPhoneBooking by mutableStateOf(false) //fixed
    var blockedFlag by mutableStateOf("1") //fixed
    var terminalId by mutableStateOf("")
    var isSendSmsOnBooking: MutableState<Boolean> = mutableStateOf(false)
    var sendWhatsAppOnBooking: MutableState<Boolean> = mutableStateOf(false)
    var isFromBusOptApp by mutableStateOf("true") // fixed
    var isRapidBooking by mutableStateOf("false") // fixed
    var allowWalletBooking by mutableStateOf(true) // fixed
    var rapidBookingSkip by mutableStateOf(true) // fixed
    var rapidBookingType by mutableStateOf(RAPID_TYPE_DEFAULT)

    var pnrNumber by mutableStateOf("")
    var isRemarkMandatory by mutableStateOf(false)
    var isFareBreakupBottomSheetVisible by mutableStateOf(false)
    private var _fareBreakupDetails = mutableStateListOf<FareBreakUpHash>()
    val fareBreakupDetails get() = _fareBreakupDetails
    var remarks by mutableStateOf("")
    var isRemarksCardVisible by mutableStateOf(false)
    var bookTicketTotalFare by mutableStateOf("0.0")
    var bookTicketPnr by mutableStateOf("")
    var isPhoneBlockedWallet by mutableStateOf("true")

    // full partial payment
    val paymentTypes = mutableStateListOf<SearchModel>()
    val partialPaymentTypes = mutableStateListOf<SearchModel>()
    var selectedPaymentType by mutableStateOf(ResourceProvider.TextResource.fromStringId(R.string.full_payment))
    var partialPaymentOption by mutableStateOf("1") // 1 for full payment
    var selectedPartialPayment by mutableStateOf(ResourceProvider.TextResource.fromStringId(R.string.do_not_release))
    var isFullPartialCardVisible by mutableStateOf(false)
    var isPartialPaymentInfoVisible by mutableStateOf(false)
    var partialPercentValue by mutableStateOf("")
    var partialAmount by mutableDoubleStateOf(0.0)
    var pendingAmount by mutableDoubleStateOf(0.0)
    var isPartialPayment by mutableStateOf(false)

    var partialBlockingDate by mutableStateOf("")
    var partialBlockingTimeHours by mutableStateOf("")
    var partialBlockingTimeMins by mutableStateOf("")
    var isPartialHoursDropdownExpanded by mutableStateOf(false)
    var isPartialTimeDropdownExpanded by mutableStateOf(false)
    var isShowReleaseDate by mutableStateOf(false)
    var isShowReleaseTime by mutableStateOf(false)
    var partialType by mutableStateOf("1") // 1 for "Do not release"


    var fullPartialTotalAmount by mutableStateOf("")
    var fullPartialRemainingAmount by mutableStateOf("")

    var focusRequesterPrimaryMobile by mutableStateOf(FocusRequester())

    /*    var focusRequesterPrimaryMobile by mutableStateOf(FocusRequester())
        var focusRequesterEmailId by mutableStateOf(FocusRequester())
        var focusRequesterFirstName by mutableStateOf(FocusRequester())
        var focusRequesterLastName by mutableStateOf(FocusRequester())
        var focusRequesterName by mutableStateOf(FocusRequester())
        var focusRequesterAge by mutableStateOf(FocusRequester())*/

    // Additional options (Optional)
    var appliedCouponList = mutableListOf<AppliedCoupon>()
    var isExpendAdditionalOffer: MutableState<Boolean> = mutableStateOf(false)
    val isCouponCodeChecked: MutableState<Boolean> = mutableStateOf(false)
    val isPromotionCouponChecked: MutableState<Boolean> = mutableStateOf(false)
    var couponCode by mutableStateOf("")
    var promotionCouponCode by mutableStateOf("")
    val isPrePostponeTicketChecked: MutableState<Boolean> = mutableStateOf(false)
    var prePostponeTicket by mutableStateOf("")
    val isPrivilegeCardChecked: MutableState<Boolean> = mutableStateOf(false)
    var privilegeCardNumber by mutableStateOf("")
    var privilegeMobileNumber by mutableStateOf("")
    val isApplySmartMilesChecked: MutableState<Boolean> = mutableStateOf(false)
    var applySmartMilesMobileNo by mutableStateOf("")
    var smartMilesOtp by mutableStateOf("")
    var smartMilesOtpKey by mutableStateOf("")
    var isSmartMilesOtpVisible: MutableState<Boolean> = mutableStateOf(false)
    var checkedOfferTypeResId by mutableStateOf<ResourceProvider.TextResource?>(null)
    var promotionOfferTypeResId by mutableStateOf<ResourceProvider.TextResource?>(null)
    var isAppliedCoupon: MutableState<Boolean> = mutableStateOf(false)
    val isDiscountAmountChecked: MutableState<Boolean> = mutableStateOf(false)
    var discountAmount by mutableStateOf("")
    val isQuotePreviousPNRChecked: MutableState<Boolean> = mutableStateOf(false)
    var quotePNRNumber by mutableStateOf("")
    var quotePhoneNumber by mutableStateOf("")
    val isGSTDetailsChecked: MutableState<Boolean> = mutableStateOf(false)
    var gstNumber by mutableStateOf("")
    var gstCompanyName by mutableStateOf("")
    val isCouponCodeEnable: MutableState<Boolean> = mutableStateOf(true)
    val isPromotionCouponEnable: MutableState<Boolean> = mutableStateOf(true)
    val isPrePostponeTicketEnable: MutableState<Boolean> = mutableStateOf(true)
    val isPrivilegeCardEnable: MutableState<Boolean> = mutableStateOf(true)
    val isApplySmartMilesEnable: MutableState<Boolean> = mutableStateOf(true)
    val isDiscountAmountEnable: MutableState<Boolean> = mutableStateOf(true)
    val isQuotePreviousPNREnable: MutableState<Boolean> = mutableStateOf(true)
    val isGSTDetailsEnable: MutableState<Boolean> = mutableStateOf(true)
    val isFreeTicketEnable: MutableState<Boolean> = mutableStateOf(true)
    val isVIPTicketEnable: MutableState<Boolean> = mutableStateOf(true)
    val returnResId by mutableStateOf("") //fixed (Optional)
    val returnResSeatsCount by mutableStateOf("0") //fixed
    val connectingResSeatsCount by mutableStateOf("") //fixed
    var isVIPTicketChecked: MutableState<Boolean> = mutableStateOf(false)
    var isExposedVIPTicketDropdown: MutableState<Boolean> = mutableStateOf(false)
    var selectedVIPTicketId by mutableStateOf("")
    val isFreeTicketChecked: MutableState<Boolean> = mutableStateOf(false)
    val vipCategoryList = mutableStateListOf<SpinnerItems>()
    val isCouponCodeVisible: MutableState<Boolean> = mutableStateOf(false)
    val isVIPTicketVisible: MutableState<Boolean> = mutableStateOf(false)
    var isGstVisible: MutableState<Boolean> = mutableStateOf(false)
    var isPrePostponeVisible: MutableState<Boolean> = mutableStateOf(false)
    var isApplySmartMilesVisible: MutableState<Boolean> = mutableStateOf(false)
    var isDiscountVisible: MutableState<Boolean> = mutableStateOf(false)
    var isQuotePreviousPnrVisible: MutableState<Boolean> = mutableStateOf(false)
    var isFreeTicketVisible: MutableState<Boolean> = mutableStateOf(false)
    var isAllowPrivilegeCardBookings: MutableState<Boolean> = mutableStateOf(false)
    var isAllowPromotionOfferCouponInBookingPage by mutableStateOf(false)
    val isTickIconVisible: MutableState<Boolean> = mutableStateOf(false)
    val isDisableAdditionalOfferCard: MutableState<Boolean> = mutableStateOf(false)
    var isEditButtonVisible: MutableState<Boolean> = mutableStateOf(false)
    var isSmartMilesOtpApi: MutableState<Boolean> = mutableStateOf(false)
    var mandatoryMap = mutableMapOf<Int, String>()

    //    var paxMandatoryMap = mutableMapOf<Int, String>()
    var exNoOfSeats by mutableStateOf("0")
    var userId by mutableStateOf("")
    var ticket: MutableState<Ticket> = mutableStateOf<Ticket>(Ticket(""))
    var isFareBreakupApiCalled by mutableStateOf(false)
    var isAllMandatoryFieldsFilled = mutableMapOf<Int, Boolean>()
    var visibleTextField: MutableState<Boolean> = mutableStateOf(true)
    val bookExtraSeatNoList = mutableStateListOf<String>()
    var isAdditionalOfferCardVisible by mutableStateOf(false)
    var isInsuranceCardVisible by mutableStateOf(false)
    var isSeatWiseDiscountEdit by mutableStateOf(false)
    var getAvailableBalance by mutableStateOf("")
    var paxPosition by mutableIntStateOf(0) //fixed
    var finalEditedFare by mutableStateOf("")

    var removePreSelectionOptionInTheBooking by mutableStateOf(false)
    var calculatedHours by mutableLongStateOf(0L)
    var calculatedMinutes by mutableLongStateOf(0L)
    var checkAMOrPM by mutableStateOf("")
    private var phoneBlockReleaseTime by mutableStateOf("")
    private var releaseTimePoliciesOptions by mutableStateOf("")
    var isPhoneBlockedDateChanged by mutableStateOf(false)
    var selectedDate by mutableStateOf("")
    private var oldHour by mutableIntStateOf(0)
    private var isApplyBPDPFare: String? = "false"

    var expandedMealType by mutableStateOf(false)
    var selectedMealTypeText by mutableStateOf("")
    var isAdditionalFareValueChanged by mutableStateOf(false)
    var isBookingTypeValueChanged by mutableStateOf(false)
    var isDiscountAmountChanged by mutableStateOf(false)
    var animateScrollToItemPosition by mutableStateOf(2)
    var isExtraSeatChanged by mutableStateOf(false)
    var isAllowToApplyDiscountOnBookingPageWithPercentage by mutableStateOf(false)
    var allowToApplyCurrentUserRoleBranchDiscount by mutableStateOf(false)
    var isEnableCampaignPromotions by mutableStateOf(false)
    var isEnableCampaignPromotionsChecked by mutableStateOf(false)
    var perSeatDiscountList = mutableStateListOf<PerSeatDiscount?>()
    var perBookingDiscountValue by mutableDoubleStateOf(0.0)
    var perBookingEditedDiscountValue by mutableStateOf("0.0")
    var isPassengerAgeChanged by mutableStateOf(false)
    var isEnableCampaignPromotionsPerBookingChecked by mutableStateOf(false)
    var isPerBookingDiscountAmountChanged by mutableStateOf(false)
    var isExpandPerBookingDiscount by mutableStateOf(false)
    var isPerBookingDiscountEditButtonVisible by mutableStateOf(true)
    var showAgentDiscountPerBookingCard by mutableStateOf(false)
    var isDeletePassengerClicked by mutableStateOf(false)
    var isExtraSeatBooking by mutableStateOf(false)

    //    var allowBimaInTs by mutableStateOf(false)
    var isAllowPhoneBlockingTicketOnbehalfOnlineAgentInBima by mutableStateOf(false)
    var isAllowPhoneBlockingInBima by mutableStateOf(false)
    private var isAllowOnlineAgentBookingInBima by mutableStateOf(false)
    private var isAllowOfflineAgentBookingInBima by mutableStateOf(false)
    private var isAllowBranchBookingInBima by mutableStateOf(false)
    var parentTravelId by mutableStateOf("")

//    private val _showValidationMessage by lazy { MutableSharedFlow<String>() }
//    val showValidationMessage = _showValidationMessage.asSharedFlow()

    private val _showValidationMessage by lazy { MutableLiveData<String>() }
    val showValidationMessage: LiveData<String>
        get() = _showValidationMessage


    // humsafar
    var isBoardingPointCardExpanded by mutableStateOf(false)
    var isDroppingPointCardExpanded by mutableStateOf(false)
    var boardingList = mutableStateListOf<StageDetail>()
    var droppingList = mutableStateListOf<StageDetail>()
    private val _boardingSpinnerList = mutableStateListOf<SpinnerItems>()
    val boardingSpinnerList = _boardingSpinnerList

    private val _droppingSpinnerList = mutableStateListOf<SpinnerItems>()
    val droppingSpinnerList = _droppingSpinnerList
    var isAllowedEditFare by mutableStateOf(false)
    var isAllowedEditFareForOtherRoute by mutableStateOf(false)
    var isAllowUpiForDirectPgBookingForAgents by mutableStateOf(false)
    var payableAmount by mutableDoubleStateOf(0.0)
    var agentPayViaPhoneNumberSMS by mutableStateOf("")
    var agentPayViaVPA by mutableStateOf("")
    var isShowAgentSubPaymentDialog by mutableStateOf(false)
    var isAgentSubPaymentSelected by mutableStateOf(false)

    var isAllowUpiForDirectPgBookingForUsers by mutableStateOf(false)
    var isAllowEasebuzzInTs by mutableStateOf(false)
    var isAllowPhonePeV2InTsApp by mutableStateOf(false)
    var userPayViaPhoneNumberSMS by mutableStateOf("")
    var userPayViaVPA by mutableStateOf("")
    var isShowUserSubPaymentDialog by mutableStateOf(false)
    var isUserSubPaymentSelected by mutableStateOf(false)
    var showPhonePeV2PendingDialog by mutableStateOf(false)

    var shouldPhoneBlock: Boolean = false
    var shouldTicketConfirm: Boolean = false
    var pinSize = 0
    var shouldExtraSeatBooking: Boolean = false
    var excludeTicketConfirmation = mutableListOf<ExcludeTicketConfirmation>()
    var fareHashData = mutableListOf<FareHash>()
    var allowAutoDiscount by mutableStateOf(false)
    var maxChar by mutableIntStateOf(55)
    var privilegesLiveData = MutableLiveData<PrivilegeResponseModel?>()
    var editFareMandatoryForAgentUser: Boolean = false
    var isEnableCopyPassengerCheckbox: Boolean = true

    var enableSelectBtn: Boolean = false
    var isCashEnabled: Boolean = false

    fun updatePrivileges(privileges: PrivilegeResponseModel?) {
        privilegesLiveData.value = privileges
    }

    init {
        getPref()
        getBookingInfo()
        getBookingRequest()
        idTypeList = getIdTypesList()
        routeId = retrieveRouteId()
        setFullPartialPaymentTypes()
        setPartialPaymentTypes()
        getSeatDetails()
        getPassengersList()

        if (!isAgentLogin)
            isGstVisible.value = retrieveGstApplicable()



        if (isBima == true) {
            travelDate = getDateDMY(travelDate).toString()
            // Timber.d("travel_date = $travelDate")
            rapidBookingType = RAPID_TYPE_HIDE
            rapidBookingSkip = false
            isRapidBooking = "false"
        } else {

            if (selectedSeatDetails.any { it.isExtraSeat }
//            || selectedSeatDetails[0].isEditFareApply == true
                || PreferenceUtils.getPrivilege()?.allowRapidBookingFlow == null
                || !PreferenceUtils.getPrivilege()?.allowRapidBookingFlow!!
                || !PreferenceUtils.getPrivilege()?.country.equals("india", true)
                || !PreferenceUtils.getPrivilege()?.allowRapidBookingFlowBySelectingSeats!!
            ) {
                rapidBookingType = RAPID_TYPE_HIDE
                rapidBookingSkip = false
                isRapidBooking = "false"
            } else {
                rapidBookingType = PreferenceUtils.getRapidBookingType()
                rapidBookingSkip = rapidBookingType == RAPID_TYPE_DEFAULT
            }
        }


        if (selectedSeatDetails.isNotEmpty()) {
            val editedFare = selectedSeatDetails[0].editFareMap.toString()
            finalEditedFare = editedFare
                .removePrefix("{")
                .removeSuffix("}")
                .replace("=", ":")
                .replace(" ", "")
                .replace(".0", "")
        }
    }

    private fun initPassengerList() {

        contactList.add(
            ContactDetail(
                mobileNumber = "",
                alternateMobileNumber = "",
                email = ""
            )
        )

        selectedSeatDetails.forEach {
            val fare = if (it.editFare != null && it.editFare.toString().isNotEmpty())
                it.editFare
            else
                it.baseFareFilter

            passengerDataList.add(
                PassengerDetailsResult(
                    expand = true,
                    isPrimary = true,
                    seatNumber = it.number,
                    fare = "$fare",
                    contactDetail = contactList,
                    discountAmount = "",
                    additionalFare = "${it.additionalFare ?: "0"}",
                    isExtraSeat = it.isExtraSeat
                )
            )
        }
    }

    private fun setFullPartialPaymentTypes() {
        val fullPayment = SearchModel()
        fullPayment.id = 1
        fullPayment.paymentType = ResourceProvider.IdTextResource(R.string.full_payment)
        paymentTypes.add(fullPayment)

        val partialPayment = SearchModel()
        partialPayment.id = 2
        partialPayment.paymentType = ResourceProvider.IdTextResource(R.string.partial_payment)
        paymentTypes.add(partialPayment)
    }

    private fun setPartialPaymentTypes() {
        val doNotRelease = SearchModel()
        doNotRelease.id = 1
        doNotRelease.paymentType = ResourceProvider.IdTextResource(R.string.do_not_release)
        partialPaymentTypes.add(doNotRelease)

        val release = SearchModel()
        release.id = 2
        release.paymentType = ResourceProvider.IdTextResource(R.string.release)
        partialPaymentTypes.add(release)
    }

    fun setRemarksCardVisibility(hide: String, mandatory: String) {
        if (privilegeResponseModel?.appPassengerDetailConfig?.remarks != null) {
            val remarkOption = privilegeResponseModel?.appPassengerDetailConfig?.remarks?.option

            if (remarkOption.equals(hide,true)
            ) {
                isRemarksCardVisible = false
            } else {
                isRemarksCardVisible = true
                isRemarkMandatory =
                    remarkOption.equals(mandatory,true)
            }
        }
    }

    fun setAdditionalOfferCardVisibility() {
        isAdditionalOfferCardVisible = (passengerDataList.any { it.isExtraSeat }
                && selectedExtraSeatDetails.isEmpty())
                || isBima == true
                || (privilegeResponseModel?.isPrePostpone != null
                && !privilegeResponseModel?.isPrePostpone!!
                && privilegeResponseModel?.freeTicket == false
                && isAgentLogin)
    }

    fun setInsuranceInfo() {
        if (allowQoalaInsurance.value == true
            && !selectedSeatDetails.any { it.isExtraSeat }
        ) {
            isInsuranceCardVisible = true
            if (insuranceMandatoryForBookings.value == true) {
                isInsuranceChecked.value = true
            } else {
                if (hasPassengerData.value == false) {
                    isInsuranceChecked.value =
                        enableInsuranceCheckboxForBooking.value == true
                }
            }
        } else
            isInsuranceCardVisible = false
    }

    private fun getBookingRequest() {
        bookingCustomRequest = retrieveBookingCustomRequest()
    }

    private fun getPref() {
        locale = if (PreferenceUtils.getlang() == "in" || PreferenceUtils.getlang() == "id")
            "id"
        else
            PreferenceUtils.getlang()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()
        isApplyBPDPFare = PreferenceUtils.getObject<String>(IS_APPLY_BP_DP_FARE).toString()


        if (PreferenceUtils.getPreference("is_bima", false) != null) {
            isBima = PreferenceUtils.getPreference("is_bima", false) ?: false
        } else {
            isBima = false
        }

        parentTravelId = PreferenceUtils.getString("parent_travel_id") ?: ""


        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            deptTime = result?.dep_time
            busType = result?.bus_type
            serviceNumber = result?.number
        }

        bccId = PreferenceUtils.getBccId()
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()
        loginModelPref = PreferenceUtils.getLogin()
        isEnableCampaignPromotions =
            PreferenceUtils.getPreference(PREF_ENABLE_CAMPAIGN_PROMOTIONS, defautlValue = false)
                ?: false

        PreferenceUtils.getPrivilege()?.let { privilegeResponseModel ->
            shouldPhoneBlock = privilegeResponseModel.pinBasedActionPrivileges?.phoneBlocking ?: false
            shouldTicketConfirm = privilegeResponseModel.pinBasedActionPrivileges?.ticketConfirmation ?: false
            excludeTicketConfirmation = privilegeResponseModel.pinBasedActionPrivileges?.excludeTicketConfirmation ?: mutableListOf<ExcludeTicketConfirmation>()
            shouldExtraSeatBooking = privilegeResponseModel.pinBasedActionPrivileges?.extraSeatBook ?: false
            pinSize = privilegeResponseModel.pinCount ?: 6
            editFareMandatoryForAgentUser = privilegeResponseModel?.tsPrivileges?.editFareMandatoryForAgentUser ?: false
        }


        if (!PreferenceUtils.getPrivilege()?.country.isNullOrEmpty()) {
            country = PreferenceUtils.getPrivilege()?.country ?: ""
        }

        if (!country.equals("india", true)) {
            rapidBookingType = RAPID_TYPE_HIDE
        }

        resId = if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) ?: 0L
        else
            PreferenceUtils.getString("reservationid")?.toLong() ?: 0L

        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            busType = result?.bus_type
            deptTime = result?.dep_time
            arrTime = result?.arr_time
            deptDate = result?.dep_date
            arrDate = result?.arr_date
            serviceNumber = result?.number
        }

        try {
            if (getCountryCodes().isNotEmpty())
                countryList = getCountryCodes()
        } catch (e: Exception) {
            Timber.d("countryCode - $e")
        }

        if (travelDate.isNotEmpty()) {
            toolbarSubTitleInfo = if (!serviceNumber.isNullOrEmpty())
                "$serviceNumber | ${getDateDMYY(travelDate)} $deptTime | $busType"
            else
                "${getDateDMYY(travelDate)} $deptTime | $busType"
        }

        if (PreferenceUtils.getPreference(PREF_BRANCH_ROLE_DISCOUNT_TYPE, "") != null) {
            branchRoleDiscountType =
                PreferenceUtils.getPreference(PREF_BRANCH_ROLE_DISCOUNT_TYPE, "") ?: ""
        }

        if (PreferenceUtils.getPreference(PREF_DISCOUNT_VALUE, "") != null) {
            discountValue =
                PreferenceUtils.getPreference(PREF_DISCOUNT_VALUE, "")?.toDouble()?.toInt()
                    .toString()
        }
        if (branchRoleDiscountType != "NONE") {
            if (PreferenceUtils.getPreference(PREF_DISCOUNT_TYPE, "") != null) {
                discountType = PreferenceUtils.getPreference(PREF_DISCOUNT_TYPE, "") ?: ""
            }
        }

        if (PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false) != null)
            isOwnRoute = PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false) ?: false

        if(PreferenceUtils.getPreference(PREF_EXTRA_ENABLE_COPY_PASSENGER, false) != null)
            isEnableCopyPassengerCheckbox = PreferenceUtils.getPreference(PREF_EXTRA_ENABLE_COPY_PASSENGER, true) ?: true
    }

    private fun getSeatDetails() {

        selectedSeatDetails = retrieveSelectedSeats()
        selectedExtraSeatDetails = retrieveSelectedExtraSeats()
        exNoOfSeats = selectedExtraSeatDetails.size.toString()

        isExtraSeats = (selectedExtraSeatDetails.size > 0)
        noOfSeats = if (isExtraSeats) selectedExtraSeatDetails.size.toString()
        else
            selectedSeatDetails.size.toString()

        selectedSeatDetails.forEach {
            if (it.editFare != null && it.editFare.toString().isNotEmpty() && it.editFare != "null") {
                totalFare = totalFare.plus(it.editFare?.toString()?.toDouble() ?: 0.0)
            } else {
                if (it.baseFareFilter != null) {
                    totalFare = totalFare.plus(it.baseFareFilter?.toString()?.toDouble() ?: 0.0)
                }
            }
        }
        totalNetAmount = "$totalFare"
    }

    private fun getPassengersList() {
        if (isInsuranceChecked.value && !passengerDataList.any { it.isExtraSeat }) {
            passengerDataList.forEach {
                it.name = "${it.firstName} ${it.lastName}"
            }
        }

        if (passengerDataList.isNotEmpty()) {

            totalFare = 0.0
            passengerDataList.forEach {
                totalFare += it.fare?.toDouble() ?: 0.0
                individualDiscountAmount += it.discountAmount?.toInt() ?: 0
                bookExtraSeatNoList.add(it.seatNumber.toString())
            }
        }

        if (!passengerDataList.any { it.isExtraSeat }) {
            selectedSeatNo = retrieveSelectedSeatNumber().toString()
        }

        if (passengerDataList.any { it.isExtraSeat }) {
            isExtraSeat = true
            val commaSeparatedExtraSeats = android.text.TextUtils.join(",", bookExtraSeatNoList)
            selectedSeatNo = commaSeparatedExtraSeats
        }


        if (passengerDataList.isNotEmpty()) {
            selectedSeatDetails.forEach {
                it.additionalFare = passengerDataList[0].additionalFare?.toDouble()
            }

            passengerDataList.forEach {
                it.additionalFare = passengerDataList[0].additionalFare
            }
        }
    }

    fun setPrivilegeData(privilegeResponseModel: PrivilegeResponseModel) {
        this.privilegeResponseModel = privilegeResponseModel

        privilegeResponseModel.appPassengerDetailConfig?.apply {
            mobileNoPrivilege.value = phoneNumber?.option ?: ""
            alternateNoPrivilege.value = alternateNo?.option ?: ""
            emailPrivilege.value = email?.option ?: ""
            firstNamePrivilege.value = firstName?.option ?: ""
            lastNamePrivilege.value = lastName?.option ?: ""
            namePrivilege.value = name?.option ?: ""
            agePrivilege.value = age?.option ?: ""
            genderPrivilege.value = title?.option ?: ""
            idTypePrivilege.value = iDType?.option ?: ""
            idNumberPrivilege.value = iDNumber?.option ?: ""
        }
        phoneValidationCountPrivilege.value = privilegeResponseModel.phoneNumValidationCount
        amountCurrency = privilegeResponseModel.currency?.takeIf { it != null }?.toString() ?: "₹"
        currencyFormat = if (!privilegeResponseModel.currencyFormat.isNullOrEmpty()) privilegeResponseModel.currencyFormat!! else "#,##,###.00"
        isAdditionalFare(privilegeResponseModel)
        isDiscountAmount(privilegeResponseModel)
        isFreezeMealSelection.value = privilegeResponseModel.freezeMealSelection
        allowQoalaInsurance.value = privilegeResponseModel.allowQoalaInsurance
        insuranceMandatoryForBookings.value = privilegeResponseModel.insuranceMandatoryForBookings
//        Timber.d("allowQoalaInsurance_check - ${privilegeResponseModel.freezeMealSelection}${privilegeResponseModel.allowQoalaInsurance}  - ${privilegeResponseModel.insuranceMandatoryForBookings}")
        enableInsuranceCheckboxForBooking.value = privilegeResponseModel.enableInsuranceCheckboxForBooking
        isAllowToApplyDiscountOnBookingPageWithPercentage = privilegeResponseModel.allowToApplyDiscountOnBookingPageWithPercentage ?: false
        allowToApplyCurrentUserRoleBranchDiscount = privilegeResponseModel.allowToApplyCurrentUserRoleBranchDiscount ?: false
        allowAutoDiscount = privilegeResponseModel.availableAppModes?.allowAutoDiscount ?: false

        if (privilegeResponseModel?.isPermanentPhoneBooking != null) {
            isPermanentPhoneBooking = privilegeResponseModel.isPermanentPhoneBooking
        }
        if (privilegeResponseModel?.removePreSelectionOptionInTheBooking != null) {
            removePreSelectionOptionInTheBooking = privilegeResponseModel.removePreSelectionOptionInTheBooking
        }
        if (privilegeResponseModel?.phoneBlockReleaseTime != null) {
            phoneBlockReleaseTime = privilegeResponseModel.phoneBlockReleaseTime.toString()
        }
        if (privilegeResponseModel?.releaseTimePoliciesOptions != null) {
            releaseTimePoliciesOptions = privilegeResponseModel.releaseTimePoliciesOptions
        }

        //Timber.d("calculatedHoursMinutes $calculatedHours  $calculatedMinutes $checkAMOrPM --- $selectedDate")

        isCouponCodeVisible.value = privilegeResponseModel.allowToDoOpenTicketCoupon && !isAgentLogin
        isPrePostponeVisible.value = privilegeResponseModel.isPrePostpone
        isAllowPrivilegeCardBookings.value = privilegeResponseModel.allowPrivilegeCardBookings && !isAgentLogin
        isApplySmartMilesVisible.value = privilegeResponseModel.applySmartMiles && !isAgentLogin
        isAllowedEditFare = privilegeResponseModel.isAllowedToEditFare
        isAllowedEditFareForOtherRoute = privilegeResponseModel.isAllowedToEditFareForOtherRoute
        isAllowPromotionOfferCouponInBookingPage = privilegeResponseModel.tsPrivileges?.allowPromotionOfferCouponInBookingPage ?: false

        if (isAgentLogin || (agentType != null && agentType == "1" || agentType == "2") || isBima == true || (isOwnRoute && privilegeResponseModel.isAllowedToEditFare) || (!isOwnRoute && privilegeResponseModel.isAllowedToEditFareForOtherRoute))
            isDiscountVisible.value = false
        else {
            isDiscountVisible.value = ((privilegeResponseModel.isDiscountOnTotalAmount != null
                    && privilegeResponseModel.isDiscountOnTotalAmount == true
                    && privilegeResponseModel.isAllowDiscountWhileBooking && isOwnRoute)
                    || (privilegeResponseModel.isDiscountOnTotalAmount != null
                    && privilegeResponseModel.isDiscountOnTotalAmount == true
                    && privilegeResponseModel.isAllowDiscountWhileBookingForOtherRoute
                    && !isOwnRoute))
        }
        isQuotePreviousPnrVisible.value =
            privilegeResponseModel.applyQuotePreviousPnrDiscount && !isAgentLogin
        isVIPTicketVisible.value =
            privilegeResponseModel.isVipBooking && !isAgentLogin && agentType != "1" && agentType != "2"
        isFreeTicketVisible.value = privilegeResponseModel.freeTicket == true
        sendWhatsAppOnBooking.value =
            privilegeResponseModel.allowToShowWhatsappCheckboxInBookingPage

        setVIPTicketCategory(privilegeResponseModel)

        if (privilegeResponseModel.applyRoleOrBranchDiscountAtTimeOfBooking != null) {
            applyRoleOrBranchDiscountAtTimeOfBooking =
                privilegeResponseModel.applyRoleOrBranchDiscountAtTimeOfBooking!!
        }

        initPassengerList()


        if (isFreezeMealSelection.value == true) {
            passengerDataList.forEach {
                it.isMealSelected = true
                it.mealRequired = true
            }
        }

        if (privilegeResponseModel?.chartSharedPrivilege?.isNotEmpty() == true) {
            if (privilegeResponseModel.chartSharedPrivilege?.get(0)?.privileges?.is_allow_online_agent_booking == true)
                isAllowOnlineAgentBookingInBima = true

            if (privilegeResponseModel.chartSharedPrivilege?.get(0)?.privileges?.is_allow_offline_agent_booking == true)
                isAllowOfflineAgentBookingInBima = true

            if (privilegeResponseModel.chartSharedPrivilege?.get(0)?.privileges?.is_allow_branch_booking == true)
                isAllowBranchBookingInBima = true

            if (privilegeResponseModel.chartSharedPrivilege?.get(0)?.privileges?.is_allow_phone_blocking_in_bima == true)
                isAllowPhoneBlockingInBima = true

            if (privilegeResponseModel.chartSharedPrivilege?.get(0)?.privileges?.allow_phone_blocking_ticket_onbehalf_online_agent == true)
                isAllowPhoneBlockingTicketOnbehalfOnlineAgentInBima = true
        }

        isAllowUpiForDirectPgBookingForAgents = privilegeResponseModel.allowUpiForDirectPgBookingForAgents
        isAllowUpiForDirectPgBookingForUsers = privilegeResponseModel.allowUpiForDirectPgBookingForBranchUser
        isAllowEasebuzzInTs = privilegeResponseModel.tsPrivileges?.allowEasebuzzInTs ?: false
        isAllowPhonePeV2InTsApp = privilegeResponseModel.tsPrivileges?.allowPhonePeV2InTsApp ?: false
    }

    private fun checkBpDpTime() {
        try {
            if (privilegeResponseModel?.phoneBlockReleaseTime != null
                && !phoneBlockReleaseTime.isNullOrEmpty()
                && country.equals("india", true)
            ) {
                if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
                    val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
                    deptTime = result?.dep_time
                }

                val selectedBoardingTime = boardingStageDetail?.time
                val selectedDroppingTime = deptTime

                val boardingHour = selectedBoardingTime.toString().substringBefore(":")
                val droppingHour = selectedDroppingTime.toString().substringBefore(":")

                val hours: Int = phoneBlockReleaseTime.toInt() / 60
                val minutes: Int = phoneBlockReleaseTime.toInt() % 60

                val phoneBlockReleaseConvertTime = "$hours:$minutes"
                val arrConvertTime = "${
                    selectedBoardingTime?.replace(" ", "")?.removeSuffix("AM")?.removeSuffix("PM")
                }"
                val depConvertTime = "${
                    selectedDroppingTime?.replace(" ", "")?.removeSuffix("AM")?.removeSuffix("PM")
                }"

                val dateBPDP: Date?
                val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dateReleaseTime: Date? = simpleDateFormat.parse(phoneBlockReleaseConvertTime)

                if (releaseTimePoliciesOptions == "boarding_time_wise") {
                    dateBPDP = simpleDateFormat.parse(arrConvertTime)
                    checkAMOrPM = if (selectedBoardingTime?.contains("AM") == true) {
                        "AM"
                    } else {
                        "PM"
                    }


                    val travelDate = boardingStageDetail?.travelDate
                        ?: PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)?.travelDate

                    if (!travelDate.isNullOrEmpty()) {
                        selectedDate = inputFormatToOutput(
                            travelDate,
                            DATE_FORMAT_D_M_Y_SLASH,
                            DATE_FORMAT_D_M_Y
                        )
                    }



                    oldHour = boardingHour.toInt()
                } else {
                    dateBPDP = simpleDateFormat.parse(depConvertTime)
                    checkAMOrPM = if (selectedDroppingTime?.contains("AM") == true) {
                        "AM"
                    } else {
                        "PM"
                    }


                    selectedDate = inputFormatToOutput(
                        boardingStageDetail?.travelDate.toString(),
                        DATE_FORMAT_D_M_Y_SLASH,
                        DATE_FORMAT_D_M_Y
                    )
                    oldHour = droppingHour.toInt()
                }

                // Calculating the difference in milliseconds
                val differenceInMilliSeconds =
                    abs((dateBPDP?.time ?: 0) - (dateReleaseTime?.time ?: 0))

                //Timber.d("testAbs - ${dateBPDP?.time} --- ${dateReleaseTime?.time}-->>>> $differenceInMilliSeconds")
                // Calculating the difference in Hours
                calculatedHours = (differenceInMilliSeconds / (60 * 60 * 1000) % 24)
                // Calculating the difference in Minutes
                calculatedMinutes = differenceInMilliSeconds / (60 * 1000) % 60

                val isDateChanged = (calculatedHours.toInt() == 11
                        || calculatedHours.toInt() == 10
                        || calculatedHours.toInt() == 9
                        || calculatedHours.toInt() == 8)

                if ((checkAMOrPM == "AM" && oldHour == 12 && isDateChanged)
                    || (checkAMOrPM == "AM" && oldHour == 1 && isDateChanged)
                    || (checkAMOrPM == "AM" && oldHour == 2 && isDateChanged)
                    || (checkAMOrPM == "AM" && oldHour == 3 && isDateChanged)
                ) {
                    calculatedHours += 12
                    selectedDate = getPreviousssDate(selectedDate)
                    //Timber.d("test >> x")
                } else if ((checkAMOrPM == "PM" && oldHour == 12 && isDateChanged)
                    || (checkAMOrPM == "PM" && oldHour == 1 && isDateChanged)
                    || (checkAMOrPM == "PM" && oldHour == 2 && isDateChanged)
                    || (checkAMOrPM == "PM" && oldHour == 3 && isDateChanged)
                ) {
                    selectedDate = getPreviousssDate(selectedDate)
                    //Timber.d("test >> y")
                } else if ((checkAMOrPM == "PM" && calculatedHours.toInt() != 0 && !isDateChanged)
                    || (checkAMOrPM == "PM" && calculatedHours.toInt() == 12 && !isDateChanged)
                    || (checkAMOrPM == "PM" && isDateChanged)
                ) {
                    calculatedHours += 12
                    //Timber.d("test >> z")
                }

                //-----------------check am/pm---------------

                if (calculatedHours > 12) {
                    calculatedHours -= 12
                    checkAMOrPM = "PM"
                    //Timber.d("test >> 1")
                } else if (checkAMOrPM == "AM" && calculatedHours.toInt() == 0) {
                    calculatedHours = 12
                    checkAMOrPM = "AM"
                    //Timber.d("test >> 2")
                } else if (checkAMOrPM == "PM" && calculatedHours.toInt() == 0) {
                    calculatedHours = 12
                    checkAMOrPM = "PM"
                    //Timber.d("test >> 3")
                } else {
                    checkAMOrPM = "AM"
                    //Timber.d("test >> 4")
                }
            } else {
                selectedDate = travelDate
            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    private fun isAdditionalFare(privilegeResponseModel: PrivilegeResponseModel): Boolean {

        if (privilegeResponseModel.isAdditionalFare != null) {
            if (isAgentLogin) {
                isAdditionalFarePrivilege.value = false

            } else if (isBima == true || (agentType != null && agentType == "1" || agentType == "2")
                || (isOwnRoute && privilegeResponseModel.isAllowedToEditFare)
                || selectedSeatDetails.any { it.isExtraSeat }
                || (!isOwnRoute && privilegeResponseModel.isAllowedToEditFareForOtherRoute)
            ) {
                isAdditionalFarePrivilege.value = false
            } else {
                isAdditionalFarePrivilege.value = privilegeResponseModel.isAdditionalFare
            }
        } else {
            isAdditionalFarePrivilege.value = false
        }

        return isAdditionalFarePrivilege.value!!
    }

    private fun isDiscountAmount(privilegeResponseModel: PrivilegeResponseModel): Boolean? {

        if (privilegeResponseModel.isDiscountOnTotalAmount != null) {
            if (isAgentLogin) {
                isDiscountPrivilege.value = true
            } else if (isBima == true && (agentType != null && agentType == "1" || agentType == "2")
                || (isOwnRoute && privilegeResponseModel.isAllowedToEditFare)
                || (!isOwnRoute && privilegeResponseModel.isAllowedToEditFareForOtherRoute)
            ) {
                isDiscountPrivilege.value = true
            } else if (isOwnRoute) {
                if (!privilegeResponseModel.isAllowedToEditFare) {
                    if (privilegeResponseModel.isAllowDiscountWhileBooking) {
                        isDiscountPrivilege.value =
                            (privilegeResponseModel.isDiscountOnTotalAmount == true
                                    || privilegeResponseModel.isDiscountOnTotalAmount == null)
                    } else
                        isDiscountPrivilege.value = true
                } else
                    isDiscountPrivilege.value = true
            } else {
                if (!privilegeResponseModel.isAllowedToEditFareForOtherRoute) {
                    if (privilegeResponseModel.isAllowDiscountWhileBookingForOtherRoute) {
                        isDiscountPrivilege.value =
                            (privilegeResponseModel.isDiscountOnTotalAmount == true || privilegeResponseModel.isDiscountOnTotalAmount == null)
                    } else {
                        isDiscountPrivilege.value = true
                    }
                } else {
                    isDiscountPrivilege.value = true
                }
            }
        } else {
            isDiscountPrivilege.value = true
        }

        return isDiscountPrivilege.value
    }

    private fun setVIPTicketCategory(privilegeResponseModel: PrivilegeResponseModel) {

        try {
            privilegeResponseModel.vipCategoryList.forEach {
                val spinnerItems =
                    SpinnerItems(it.id.toString().toDouble().toInt(), it.name.toString())
                vipCategoryList.add(spinnerItems)
            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    fun setPaxHistory(passengerHistory: PassengersHistory) {
        this.passengerHistoryModel = passengerHistory

        this.passengerHistoryList =
            passengerHistory.body.distinctBy { it.name } as MutableList<PassengerHistoryModel>
        showDialog.value = true
    }

    fun setCouponDetails(couponResponseX: CouponResponse) {
        this.couponResponse = couponResponseX
    }

    fun setDiscountParam(discountParams: DiscountParams) {
        this.discountParams = discountParams
    }






    fun setMobile(data: String) {
        this.mobileNoPrivilege.value = data
    }
    fun getMobile():String {
       return this.mobileNoPrivilege.value
    }
    fun getMobileAlternate():String {
       return this.alternateNoPrivilege.value
    }

    fun checkUncheckCoupons(code: String) {
        val couponCodeResId = ResourceProvider.TextResource.fromStringId(R.string.coupon_code)
        val promotionCouponCodeResId = ResourceProvider.TextResource.fromStringId(R.string.promotion_coupon)
        val prePostponeTicketResId =
            ResourceProvider.TextResource.fromStringId(R.string.pre_postpone_ticket)
        val privilegeCardResId = ResourceProvider.TextResource.fromStringId(R.string.privilege_card)
        val applySmartMilesResId =
            ResourceProvider.TextResource.fromStringId(R.string.apply_smart_miles)
        val quotePreviousPnrResId =
            ResourceProvider.TextResource.fromStringId(R.string.quote_previous_pnr)
        val discountAmountResId =
            ResourceProvider.TextResource.fromStringId(R.string.discount_amount)

        when (checkedOfferTypeResId) {

            couponCodeResId -> {
                if (code == "200") {
                    addAppliedCoupon(couponCode)
                    isPrePostponeTicketEnable.value = false
                    isPromotionCouponEnable.value = false
                    isEditButtonVisible.value = true
                } else {
                    isCouponCodeChecked.value = false
                }
            }
            promotionCouponCodeResId -> {
                if (code == "200") {
                    addAppliedCoupon(promotionCouponCode)
                    isPrePostponeTicketEnable.value = false
                    isCouponCodeEnable.value = false
                    isPrivilegeCardEnable.value = false
                    isApplySmartMilesEnable.value = false
                    isQuotePreviousPNREnable.value = false
                    isDiscountAmountEnable.value = false
                    isFreeTicketEnable.value = false
                    isVIPTicketEnable.value = false
                    isEditButtonVisible.value = true
                } else {
                    isPromotionCouponChecked.value = false
                }
            }

            prePostponeTicketResId -> {
                if (code == "200") {
                    addAppliedCoupon(prePostponeTicket)
                    isCouponCodeEnable.value = false
                    isPromotionCouponEnable.value = false
                    isEditButtonVisible.value = true
                } else {
                    isPrePostponeTicketChecked.value = false
                }
            }

            privilegeCardResId -> {
                if (code == "200") {
                    addAppliedCoupon(privilegeCardNumber)
                    isCouponCodeEnable.value = false
                    isPromotionCouponEnable.value = false
                    isQuotePreviousPNREnable.value = false
                    isDiscountAmountEnable.value = false
                    isApplySmartMilesEnable.value = false
                    isPromotionCouponEnable.value = false
                    isEditButtonVisible.value = true
                } else {
                    isPrivilegeCardChecked.value = false
                }
            }

            quotePreviousPnrResId -> {

                if (code == "200") {
                    addAppliedCoupon(quotePNRNumber)
                    isPrivilegeCardEnable.value = false
                    isDiscountAmountEnable.value = false
                    isApplySmartMilesEnable.value = false
                    isEditButtonVisible.value = true
                } else {
                    isQuotePreviousPNRChecked.value = false
                }
            }

            applySmartMilesResId -> {

                if (code == "200") {
                    addAppliedCoupon(applySmartMilesMobileNo)
                    isPrivilegeCardEnable.value = false
                    isDiscountAmountEnable.value = false
                    isQuotePreviousPNREnable.value = false
                    isEditButtonVisible.value = true
                    isSmartMilesOtpVisible.value = false

                } else {
                    isApplySmartMilesChecked.value = false
                    isSmartMilesOtpApi.value = false
                }
            }

            discountAmountResId -> {
//                addAppliedCoupon(discountAmount)
//                isPrivilegeCardEnable.value = false
//                isQuotePreviousPNREnable.value = false
//                isApplySmartMilesEnable.value = false
//                Timber.d("testCoupon-applySmartMiles - $discountAmount")
//                isEditButtonVisible.value = true
            }

            else -> {}
        }
    }

    fun addAppliedCoupon(couponCode: String) {
        val appliedCoupon = AppliedCoupon(couponCode, couponTypeResource = checkedOfferTypeResId)
        removeAppliedCoupon(checkedOfferTypeResId!!)
        appliedCouponList.add(appliedCoupon)
        isFareBreakupApiCalled = true
        visibleTextField.value = false
//        Timber.d("appliedCoupon_add- list=$appliedCouponList")
    }

    fun removeAppliedCoupon(couponType: ResourceProvider.TextResource) {
        val index = appliedCouponList.indexOfFirst { it.couponTypeResource == couponType }
        if (index != null && index != -1) {
            appliedCouponList.removeAt(index)
            isFareBreakupApiCalled = true
            visibleTextField.value = false
        }
//        Timber.d("appliedCouponList_remove- list = $appliedCouponList index = $index")
    }

    fun setSmartMilesOtp(otp: String, otpKey: String) {
        isSmartMilesOtpVisible.value = true
        smartMilesOtpKey = otpKey
//        smartMilesOtp = otp
    }

    private fun getBookingInfo() {
        bookingCustomRequest = retrieveBookingCustomRequest()
        agentType = bookingCustomRequest?.selected_booking_id.toString()

        if (agentType == "12" || agentType == "0")
            agentType = "3"
    }

    fun setMealInfoData(serviceDetailsModel: Body) {
        this.serviceDetailsModel = serviceDetailsModel

        isMealRequired.value = serviceDetailsModel.isMealRequired
        isMealNoType.value = serviceDetailsModel.isMealNoType
        isSelectedMealTypes.value = serviceDetailsModel.selectedMealTypes


        if (isSelectedMealTypes.value != null) {
            val mealOriginalList = isSelectedMealTypes.value as List<List<Any?>?>?
            if (!mealOriginalList.isNullOrEmpty()) {
                for (i in 0..mealOriginalList.size.minus(1)) {
                    val id: Int = mealOriginalList[i]!![0].toString().toDouble().toInt()
                    val meal: String = mealOriginalList[i]!![1].toString()
                    mealList.add(SpinnerItems(id, meal))
                }
            }
        }

        /*if (isMealRequired.value == true && !isExtraSeats)
        {
            passengerDataList.forEach {
                it.mealRequired = true
            }
        }*/
    }

    fun isExpand(index: Int, item: Boolean) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(expand = item)
            passengerDataList[index].expand = item
        }
    }

    fun setFare(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(fare = item)
            passengerDataList[index].fare = item
        }
    }

    fun setFirstName(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(firstName = item)
            passengerDataList[index].firstName = item
        }
    }

    fun setLastName(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(lastName = item)
            passengerDataList[index].lastName = item
        }
    }

    fun setName(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(name = item)
            passengerDataList[index].name = item
        }
    }

    fun setAge(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(age = item)
            passengerDataList[index].age = item
        }
    }

    fun setGender(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(sex = item)
            passengerDataList[index].sex = item
        }
    }

    fun setIdType(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(idType = item)
            passengerDataList[index].idCardType = item
        }
    }

    fun setIdCardTypeId(index: Int, item: Int) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(idCardTypeId =  item)
            passengerDataList[index].idCardTypeId = item
        }
    }

    fun setIdNumber(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(idnumber = item)
            passengerDataList[index].idCardNumber = item
        }
    }

    fun setAdditionalFare(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(additionalFare = item)
            passengerDataList[index].additionalFare = item
        }
    }

    fun setDiscount(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(discountAmount = item)
            passengerDataList[index].discountAmount = item
        }
    }

    fun setExtraSeatFare(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(fare = item)
            passengerDataList[index].fare = item
        }
    }

    fun setExtraSeatNo(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(seatNumber = item)
            passengerDataList[index].seatNumber = item
        }
    }

    fun setIsMealSelected(index: Int, item: Boolean) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(isMealSelected = item)
            passengerDataList[index].isMealSelected = item
        }
    }

    fun setSelectedMealTypeText(index: Int, item: String) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(selectedMealType = item)
            passengerDataList[index].selectedMealType = item
        }
    }

    fun setSelectedMealTypeId(index: Int, id: Int) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(selectedMealTypeId = id)
            passengerDataList[index].selectedMealTypeId = id
        }
    }

    fun isMandatoryFieldsFilled(index: Int, item: Boolean) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(isFilled = item)
            passengerDataList[index].isFilled = item
        }
    }

    fun isSelectedGenderMale(index: Int, item: Boolean) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(isSelectedGenderMale = item)
            passengerDataList[index].isSelectedGenderMale = item
        }
    }

    fun isSelectedGenderFemale(index: Int, item: Boolean) {
        if (index != -1) {
            passengerDataList[index] = passengerDataList[index].copy(isSelectedGenderFemale = item)
            passengerDataList[index].isSelectedGenderFemale = item
        }
    }

    fun customBookingTypes(
        phoneBooking: String,
        walkin: String,
        confirmBooking: String,
        onlineAgent: String,
        offlineAgent: String,
        branch: String,
        subAgent: String
    ) {
        _bookingTypes.clear()
        var itemWalkin = SpinnerItems(walkinId, walkin)
        if (privilegeResponseModel?.allowToSwitchSinglePageBooking != null && privilegeResponseModel?.allowToSwitchSinglePageBooking!!) {

            itemWalkin = SpinnerItems(walkinId, confirmBooking)
            val itemPhone = SpinnerItems(phoneBookingId, phoneBooking)
            if (privilegeResponseModel?.isPhoneBooking == true)
                _bookingTypes.add(itemPhone)
        }

        val itemOnlAgent = SpinnerItems(onlineAgentId, onlineAgent)
        val itemOfflineAgent = SpinnerItems(offlineAgentId, offlineAgent)
        val itemBranch = SpinnerItems(branchId, branch)
        _bookingTypes.add(itemWalkin)

        /*  checking below privilege
          "is_allow_online_agent_booking": true,
           "is_allow_offline_agent_booking": true,
           "is_allow_branch_booking": true,*/
        if (privilegeResponseModel != null) {

            if (isBima != null && isBima == true && privilegeResponseModel?.chartSharedPrivilege?.isNotEmpty() == true && privilegeResponseModel?.chartSharedPrivilege?.get(
                    0
                )?.parent_travel_id == parentTravelId.toInt()
            ) {
                if (privilegeResponseModel?.chartSharedPrivilege?.get(0)?.privileges?.is_allow_online_agent_booking == true)
                    bookingTypes.add(itemOnlAgent)

                if (privilegeResponseModel?.chartSharedPrivilege?.get(0)?.privileges?.is_allow_offline_agent_booking == true)
                    bookingTypes.add(itemOfflineAgent)

                if (privilegeResponseModel?.chartSharedPrivilege?.get(0)?.privileges?.is_allow_branch_booking == true)
                    bookingTypes.add(itemBranch)

            } else {
                if ((!isOwnRoute && privilegeResponseModel?.isAllowOnlineAgentBookingForOtherRoutes == true)
                    || (isOwnRoute && privilegeResponseModel?.isAllowOnlineAgentBooking == true)
                )
                    _bookingTypes.add(itemOnlAgent)
                if (privilegeResponseModel?.isAllowOfflineAgentBooking == true)
                    _bookingTypes.add(itemOfflineAgent)
                if (privilegeResponseModel?.isAllowBranchBooking == true)
                    _bookingTypes.add(itemBranch)
            }

            if (isAgentLogin) {
                if (privilegeResponseModel?.allowToDoPhoneBlocking == true) {
                    val itemPhone = SpinnerItems(phoneBookingId, phoneBooking)
                    _bookingTypes.add(itemPhone)
                }

                if (PreferenceUtils.getSubAgentRole() != "true") {
                    val subAgentBookingType = SpinnerItems(33, subAgent)
                    _bookingTypes.add(subAgentBookingType)
                }
            }

            setBookingTypeText()
        }
    }

    fun bookingStatus(
        confirmBooking: String,
        phoneBooking: String,
    ) {
        _bookingStatusTypes.clear()

        val itemConfirmBooking = SpinnerItems(statusConfirmBookingId, confirmBooking)
        _bookingStatusTypes.add(itemConfirmBooking)

        if (privilegeResponseModel != null) {
            if (isBima != null && isBima == true && privilegeResponseModel?.chartSharedPrivilege?.get(
                    0
                )?.parent_travel_id == parentTravelId.toInt()
            ) {
                if (privilegeResponseModel?.chartSharedPrivilege?.get(0)?.privileges?.is_allow_phone_blocking_in_bima == true) {
                    val itemPhoneBookingId = SpinnerItems(statusPhoneBookingId, phoneBooking)
                    _bookingStatusTypes.add(itemPhoneBookingId)
                }
            } else {
                if (privilegeResponseModel?.isPhoneBooking == true) {
                    val itemPhoneBookingId = SpinnerItems(statusPhoneBookingId, phoneBooking)
                    _bookingStatusTypes.add(itemPhoneBookingId)
                }
            }
        }

        _selectedStatusType = ResourceProvider.TextResource.fromStringId(R.string.confirm)

    }

    fun setBookingTypeText() {
        _selectedBookingType =
            if (privilegeResponseModel != null && privilegeResponseModel?.allowToSwitchSinglePageBooking != null && privilegeResponseModel?.allowToSwitchSinglePageBooking!!) {
                ResourceProvider.TextResource.fromStringId(R.string.confirmBooking)
            } else {
                ResourceProvider.TextResource.fromStringId(R.string.walkin)
            }
        _selectedBookingTypeId = walkinId
    }

    fun setBookingType(bookingType: ResourceProvider.TextResource) {
        _selectedBookingType = bookingType
    }

    fun setBookingTypeId(bookingTypeId: Int) {
        _selectedBookingTypeId = bookingTypeId
    }

    fun setStatusType(statusType: ResourceProvider.TextResource) {
        _selectedStatusType = statusType
    }


    fun setAgentLogin(isAgentLogin: Boolean) {
        _isAgentLogin = isAgentLogin
    }

    fun setRoleType(role: String) {
        _roleType = role
    }

    fun setBookingCardVisibility(isVisible: Boolean) {
        _isBookingTypeCardVisible = isVisible
    }
    fun setPromotionCouponVisibility(isVisible: Boolean) {
        _isPromotionCouponVisible = isVisible
    }

    fun setPhoneBookingVisibility(isVisible: Boolean) {
        _isPhoneBookingVisible = isVisible
    }


    fun setOnlineViewVisibility(isVisible: Boolean) {
        _isOnlineViewVisible = isVisible
    }

    fun setOfflineViewVisibility(isVisible: Boolean) {
        _isOfflineViewVisible = isVisible
    }

    fun setBranchViewVisible(isVisible: Boolean) {
        _isBranchViewVisible = isVisible
    }

    fun setSatusViewVisible(isVisible: Boolean) {
        _isStatusCardVisible = isVisible
    }

    fun setPhoneDialogViewVisible(isVisible: Boolean) {
        _isPhoneDialogVisible = isVisible
    }

    fun setSubAgentViewVisible(isVisible: Boolean) {
        _isSubAgentViewVisible = isVisible
    }

    fun setPhoneBlockDateTimeVisible(isVisible: Boolean) {
        _isPhoneBlockDateTimeVisible = isVisible
    }

    fun setPhoneBlockTime(time: ResourceProvider.TextResource) {
        _phoneBlockTime = time
    }

    fun setPaymentOptions() {
        paymentOptionsList.clear()
        //Timber.d("_selectedBookingTypeId $_selectedBookingTypeId -- $onlineAgentId -- $offlineAgentId -- $branchId")
        if (privilegeResponseModel != null) {

            if (privilegeResponseModel?.allowCashCreditOptionsInBooking != null
                && privilegeResponseModel?.allowCashCreditOptionsInBooking == true
            ) {
                isCashEnabled = true
                val cash = SearchModel()
                cash.id = "1"
                cash.paymentType = ResourceProvider.TextResource.fromStringId(R.string.cash)

                val creditDebitCard = SearchModel()
                creditDebitCard.id = "2"
                creditDebitCard.paymentType =
                    ResourceProvider.TextResource.fromStringId(R.string.credit_debit)

                paymentOptionsList.add(cash)

                if (selectedExtraSeatDetails.size > 0) {
                    paymentOptionsList.add(creditDebitCard)
                } else if (!isExtraSeat) {
                    paymentOptionsList.add(creditDebitCard)
                }
            } else {
                isCashEnabled = false
            }

            if (privilegeResponseModel?.allowBimaInTs == null
                || privilegeResponseModel?.allowBimaInTs == false
            ) {
                if (privilegeResponseModel?.allowWalletAndUpiOptionsInBookingPage != null
                    && privilegeResponseModel?.allowWalletAndUpiOptionsInBookingPage==true
                    && _selectedBookingTypeId != onlineAgentId
                    && _selectedBookingTypeId != offlineAgentId
                    && _selectedBookingTypeId != branchId
                ) {
                    val walletUpi = SearchModel()
                    walletUpi.id = "4"
                    walletUpi.paymentType =
                        ResourceProvider.TextResource.fromStringId(R.string.wallet_upi)

                    if (selectedExtraSeatDetails.size > 0) {
                        paymentOptionsList.add(walletUpi)
                    } else if (!isExtraSeat) {
                        paymentOptionsList.add(walletUpi)
                    }
                }
            }


            if (privilegeResponseModel?.allowToConfigurePaymentOptionsInBookingPage != null
                && privilegeResponseModel?.allowToConfigurePaymentOptionsInBookingPage!!
            ) {
                    val others = SearchModel()
                    others.id = "3"
                    others.paymentType = ResourceProvider.TextResource.fromStringId(R.string.others)

                    if (selectedExtraSeatDetails.size > 0) {
                        paymentOptionsList.add(others)
                    } else if (!isExtraSeat) {
                        paymentOptionsList.add(others)
                    }



                if (privilegeResponseModel?.allowBimaInTs == null
                    && privilegeResponseModel?.allowBimaInTs == false
                ) {
                    if (privilegeResponseModel?.allowDepositOptionsInBooking != null
                        && privilegeResponseModel?.allowDepositOptionsInBooking!!
                    ) {
                        val deposit = SearchModel()
                        deposit.id = "158"
                        deposit.paymentType =
                            ResourceProvider.TextResource.fromStringId(R.string.deposit)

                        if (selectedExtraSeatDetails.size > 0) {
                            paymentOptionsList.add(deposit)
                        } else if (!isExtraSeat) {
                            paymentOptionsList.add(deposit)
                        }
                    }

                    if (privilegeResponseModel?.isEzetapEnabledInTsApp!!) {
                        //paymentOptionsList.clear()

                        //Ezetap Radio Option
                        val ezetap = SearchModel()
                        ezetap.id = "14"
                        ezetap.paymentType =
                            ResourceProvider.TextResource.fromStringId(R.string.ezetap)
                        if(paymentOptionsList.size > 1 && paymentOptionsList.any { it.paymentType == ResourceProvider.TextResource.fromStringId(R.string.paytm)}) {
                            paymentOptionsList.add(1, ezetap)
                        }else
                            paymentOptionsList.add(0, ezetap)

                    }
                }


                if (privilegeResponseModel?.isPaytmPosEnabled!!) {
                   // paymentOptionsList.clear()

                    //Paytm Radio Option
                    val paytm = SearchModel()
                    paytm.id = "14"
                    paytm.paymentType =
                        ResourceProvider.TextResource.fromStringId(R.string.paytm)
                    selectedPaymentOption=ResourceProvider.TextResource.fromStringId(R.string.paytm)
                    selectedPaymentOptionId=14
                    if(paymentOptionsList.size > 1 && paymentOptionsList.any { it.paymentType == ResourceProvider.TextResource.fromStringId(R.string.ezetap)}) {
                        paymentOptionsList.add(1, paytm)
                    }else
                        paymentOptionsList.add(0, paytm)
                }
            }

            // As discussed with Naresh & Faraz, easebuzz pg is renamed as UPI
            if (isAllowUpiForDirectPgBookingForUsers && (isAllowEasebuzzInTs || isAllowPhonePeV2InTsApp)) {
                val easebuzz = SearchModel()
                easebuzz.id = "11"
                easebuzz.paymentType = ResourceProvider.TextResource.fromStringId(R.string.upi_caps)
                selectedPaymentOption = ResourceProvider.TextResource.fromStringId(R.string.upi_caps)
                selectedPaymentOptionId = 20
                paymentOptionsList.add(easebuzz)
            }
        }
    }

    fun setPaymentOptionsAgents() {
        if (privilegeResponseModel != null) {
            val availablePaymentOptionsListForAgent=privilegeResponseModel?.agentPaymentOptions

            if(availablePaymentOptionsListForAgent != null){
                paymentOptionsList.clear()


                if ("CASH" in availablePaymentOptionsListForAgent) {
                    isCashEnabled = true
                    val cash = SearchModel().apply {
                        id = "1"
                        paymentType = ResourceProvider.TextResource.fromStringId(R.string.cash)
                    }
                    paymentOptionsList.add(cash)
                } else {
                    isCashEnabled = false
                }


                // Only add wallet if user is not a sub-agent
                if ("PAY_FROM_WALLET" in availablePaymentOptionsListForAgent && PreferenceUtils.getSubAgentRole() != "true") {
                    val walletAgent = SearchModel().apply {
                        id = "15"
                        paymentType = ResourceProvider.TextResource.fromStringId(R.string.wallet)
                    }
                    paymentOptionsList.add(walletAgent)
                }

                if ("PAY_NET_AMOUNT" in availablePaymentOptionsListForAgent) {
                    val netAmtAgent = SearchModel().apply {
                        id = "16"
                        paymentType = ResourceProvider.TextResource.fromStringId(R.string.net_amt_less_off_comm)
                    }
                    paymentOptionsList.add(netAmtAgent)
                }

                if ("PAY_FULL_AMOUNT" in availablePaymentOptionsListForAgent) {
                    val fullAmountAgent = SearchModel().apply {
                        id = "17"
                        paymentType = ResourceProvider.TextResource.fromStringId(R.string.full_amount)
                    }
                    paymentOptionsList.add(fullAmountAgent)
                }


            }



                }
    }

    fun setSubPaymentOptionsAgents() {
        paymentSubOptionsList.clear()
        val availableAgentSubPaymentOptions = privilegeResponseModel?.agentSubPaymentOptions

        if (availableAgentSubPaymentOptions != null) {
            if (PaymentTypes.QR in availableAgentSubPaymentOptions) {
                val agentSubPaymentTypeQR = SearchModel()
                agentSubPaymentTypeQR.id = "QR"
                agentSubPaymentTypeQR.paymentType =
                    ResourceProvider.TextResource.fromStringId(R.string.pay_via_qr)
                paymentSubOptionsList.add(agentSubPaymentTypeQR)
            }

            if (PaymentTypes.SMS in availableAgentSubPaymentOptions) {
                val agentSubPaymentTypeSMS = SearchModel()
                agentSubPaymentTypeSMS.id = "SMS"
                agentSubPaymentTypeSMS.paymentType =
                    ResourceProvider.TextResource.fromStringId(R.string.pay_via_sms)
                paymentSubOptionsList.add(agentSubPaymentTypeSMS)
            }

            if (PaymentTypes.VPA in availableAgentSubPaymentOptions) {
                val agentSubPaymentTypeUPI = SearchModel()
                agentSubPaymentTypeUPI.id = "VPA"
                agentSubPaymentTypeUPI.paymentType =
                    ResourceProvider.TextResource.fromStringId(R.string.pay_via_upi)
                paymentSubOptionsList.add(agentSubPaymentTypeUPI)
            }

            if (PaymentTypes.UPI_INTENT in availableAgentSubPaymentOptions) {
                val agentSubPaymentTypeUPIIntent = SearchModel()
                agentSubPaymentTypeUPIIntent.id = "UPI INTENT"
                agentSubPaymentTypeUPIIntent.paymentType =
                    ResourceProvider.TextResource.fromStringId(R.string.pay_via_upi_intent)
                paymentSubOptionsList.add(agentSubPaymentTypeUPIIntent)
            }

            if (PaymentTypes.PHONEPE_V2 in availableAgentSubPaymentOptions) {
                val agentSubPaymentTypePhonePeV2 = SearchModel()
                agentSubPaymentTypePhonePeV2.id = PaymentTypes.PHONEPE_V2
                agentSubPaymentTypePhonePeV2.paymentType =
                    ResourceProvider.TextResource.fromStringId(R.string.phonepe_v2)
                paymentSubOptionsList.add(agentSubPaymentTypePhonePeV2)
            }
        }
    }

    fun setSubPaymentOptionsUsers() {
        paymentSubOptionsList.clear()
        val upiSubPaymentOptions = privilegeResponseModel?.tsPrivileges?.upiSubPaymentOptions

        if (upiSubPaymentOptions != null) {
            if (isAllowEasebuzzInTs) {
//                if (privilegeResponseModel?.easebuzzSubPaymentOptions?.payViaQr != null) {
                if (PaymentTypes.QR in upiSubPaymentOptions) {
                    val userSubPaymentTypeQR = SearchModel()
                    userSubPaymentTypeQR.id = PaymentTypes.QR
                    userSubPaymentTypeQR.paymentType =
                        ResourceProvider.TextResource.fromStringId(R.string.pay_via_qr_user)
                    paymentSubOptionsList.add(userSubPaymentTypeQR)
                }

//                if (privilegeResponseModel?.easebuzzSubPaymentOptions?.payViaSms != null) {
                if (PaymentTypes.SMS in upiSubPaymentOptions) {
                    val userSubPaymentTypeSMS = SearchModel()
                    userSubPaymentTypeSMS.id = PaymentTypes.SMS
                    userSubPaymentTypeSMS.paymentType =
                        ResourceProvider.TextResource.fromStringId(R.string.pay_via_sms_user)
                    paymentSubOptionsList.add(userSubPaymentTypeSMS)
                }

//                if (privilegeResponseModel?.easebuzzSubPaymentOptions?.payViaUpi != null) {
                if (PaymentTypes.VPA in upiSubPaymentOptions) {
                    val userSubPaymentTypeUPI = SearchModel()
                    userSubPaymentTypeUPI.id = PaymentTypes.VPA
                    userSubPaymentTypeUPI.paymentType =
                        ResourceProvider.TextResource.fromStringId(R.string.pay_via_upi_user)
                    paymentSubOptionsList.add(userSubPaymentTypeUPI)
                }


//                if (privilegeResponseModel?.easebuzzSubPaymentOptions?.payViaUpiIntent != null) {
                    if (PaymentTypes.UPI_INTENT in upiSubPaymentOptions) {
                    val userSubPaymentTypeUPIIntent = SearchModel()
                    userSubPaymentTypeUPIIntent.id = PaymentTypes.UPI_INTENT
                    userSubPaymentTypeUPIIntent.paymentType =
                        ResourceProvider.TextResource.fromStringId(R.string.pay_via_upi_intent)
                    paymentSubOptionsList.add(userSubPaymentTypeUPIIntent)
                }
            }

            if (isAllowPhonePeV2InTsApp) {
                if (PaymentTypes.PHONEPE_V2 in upiSubPaymentOptions) {
                    val userSubPaymentTypePhonePeV2 = SearchModel()
                    userSubPaymentTypePhonePeV2.id = PaymentTypes.PHONEPE_V2
                    userSubPaymentTypePhonePeV2.paymentType =
                        ResourceProvider.TextResource.fromStringId(R.string.phonepe_v2)
                    paymentSubOptionsList.add(userSubPaymentTypePhonePeV2)
                }
            }
        }
    }

    fun setOtherPaymentOptionList() {
        otherPaymentOptions.clear()
        if (!privilegeResponseModel?.othersPaymentOption.isNullOrEmpty()) {
            privilegeResponseModel?.othersPaymentOption?.forEach {
                val payGayType = PayGayType()
                payGayType.payGayTypeName = it.label
                payGayType.payGayTypeId = it.id.toString()
                otherPaymentOptions.add(payGayType)
            }
        }
    }

    fun setWalletOptions() {
        if (privilegeResponseModel?.allowToShowUpiPaymentOptionInBookingPage != null
            && privilegeResponseModel?.allowToShowUpiPaymentOptionInBookingPage!!
        ) {
            if (!walletPaymentOptions.contains(WalletPaymentOption("UPI", 2, ""))) {
                walletPaymentOptions.add(0, WalletPaymentOption("UPI", 2, ""))
            }
        }
    }

    fun setFareBreakupDetails(fareBreakupInfo: MutableList<FareBreakUpHash>) {
        _fareBreakupDetails = fareBreakupInfo.toMutableStateList()
    }

    fun setWalletAndUpiOptions() {
        walletPaymentOptions.clear()
        if (privilegeResponseModel?.walletPaymentOptions != null) {

            privilegeResponseModel?.walletPaymentOptions?.forEach {
                walletPaymentOptions.add(it)
            }

            val upiIndex = walletPaymentOptions.indexOfFirst {
                (it.type).equals("upi", true)
            }
            // removing UPI options
            if (upiIndex != -1)
                walletPaymentOptions.removeAt(upiIndex)
        }
    }

    fun getPartialPercent(): Double {
        var percent = 0.0
        if (privilegeResponseModel?.partialPaymentLimitValue != null) {
            percent =
                (totalFare / 100.0f) * privilegeResponseModel?.partialPaymentLimitValue?.toDouble()!!
        }
        return percent
    }

    fun setAvailableBalance(it: AgentAccountInfoRespnse) {
        val availableBalance = it.available_balance
        if (availableBalance != null && availableBalance.isNotEmpty()) {
            if (privilegeResponseModel != null && privilegeResponseModel?.isAgentLogin == true && !privilegeResponseModel!!.country.equals(
                    "india",
                    true
                )
            ) {
                getAvailableBalance = if (availableBalance.isNotEmpty()) " $amountCurrency ${
                    availableBalance.toDouble().convert(currencyFormat)
                }" else " $amountCurrency $availableBalance"
            }
            else if(privilegeResponseModel?.isAgentLogin == true && isAllowUpiForDirectPgBookingForAgents) {
                getAvailableBalance = if (availableBalance.isNotEmpty()) " $amountCurrency ${
                    availableBalance.toDouble().convert(currencyFormat)
                }" else " $amountCurrency $availableBalance"
            }
        }
    }

    fun setBoardingDroppingData(role: String?, agentRoleName: String) {
        //Timber.d("role: $role privilegeResponseModel?.allowBookingForAllotedServices ${privilegeResponseModel?.allowBookingForAllotedServices}")

        if (role == agentRoleName && privilegeResponseModel?.allowBookingForAllotedServices == true) {
            if (PreferenceUtils.getObject<DropOffDetail>(AGENT_SELECTED_DROPPING_DETAIL) != null) {
                val selectedDropping =
                    PreferenceUtils.getObject<DropOffDetail>(AGENT_SELECTED_DROPPING_DETAIL)!!
                droppingStageDetail = StageDetail()
                droppingStageDetail?.address = selectedDropping.address
                droppingStageDetail?.id = selectedDropping.id.toInt()
                droppingStageDetail?.landmark = selectedDropping.landmark
                droppingStageDetail?.name = selectedDropping.name
                droppingStageDetail?.time = selectedDropping.time

                droppingPoint = droppingStageDetail?.name ?: ""
                droppingId = droppingStageDetail?.id ?: 0
            }

            if (PreferenceUtils.getObject<BoardingPointDetail>(AGENT_SELECTED_BOARDING_DETAIL) != null) {
                val selectedBoarding =
                    PreferenceUtils.getObject<BoardingPointDetail>(AGENT_SELECTED_BOARDING_DETAIL)!!
                boardingStageDetail = StageDetail()
                boardingStageDetail?.address = selectedBoarding.address
                boardingStageDetail?.id = selectedBoarding.id.toInt()
                boardingStageDetail?.landmark = selectedBoarding.landmark
                boardingStageDetail?.name = selectedBoarding.name
                boardingStageDetail?.time = selectedBoarding.time

                boardingPoint = boardingStageDetail?.name ?: ""
                boardingId = boardingStageDetail?.id ?: 0
            }
        } else {
            if (isApplyBPDPFare == "true") {
                if (PreferenceUtils.getObject<StageDetail>(SELECTED_DROPPING_DETAIL) != null) {
                    droppingStageDetail =
                        PreferenceUtils.getObject<StageDetail>(SELECTED_DROPPING_DETAIL)!!
                    droppingPoint = droppingStageDetail?.name ?: ""
                    droppingId = droppingStageDetail?.id ?: 0
                }

                if (PreferenceUtils.getObject<StageDetail>(SELECTED_BOARDING_DETAIL) != null) {
                    boardingStageDetail =
                        PreferenceUtils.getObject<StageDetail>(SELECTED_BOARDING_DETAIL)!!
                    boardingPoint = boardingStageDetail?.name ?: ""
                    boardingId = boardingStageDetail?.id ?: 0
                }
            } else {
                if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
                    droppingStageDetail =
                        PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)!!
                    droppingPoint = droppingStageDetail?.name ?: ""
                    droppingId = droppingStageDetail?.id ?: 0
                }

                if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
                    boardingStageDetail =
                        PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)!!
                    boardingPoint = boardingStageDetail?.name ?: ""
                    boardingId = boardingStageDetail?.id ?: 0
                }
            }
        }

        checkBpDpTime()
    }

    fun setCampaignsAndPromotionsDiscountData(response: CampaignsAndPromotionsDiscountResponse?) {
        if (response != null) {
            if (response.perSeatDiscount?.isNotEmpty() == true) {
                perSeatDiscountList = response.perSeatDiscount.toMutableStateList()
                perBookingDiscountValue = 0.0
                perBookingEditedDiscountValue = "0.0"
                isEnableCampaignPromotionsPerBookingChecked = false
                showAgentDiscountPerBookingCard = false
            } else {
                perBookingDiscountValue = response.discountValue ?: 0.0
                perBookingEditedDiscountValue = response.discountValue.toString() ?: "0.0"
                isEnableCampaignPromotionsPerBookingChecked = true
                showAgentDiscountPerBookingCard = true
                perSeatDiscountList.clear()
            }
        } else {
            perBookingDiscountValue = 0.0
            perBookingEditedDiscountValue = "0.0"
            isEnableCampaignPromotionsPerBookingChecked = false
            showAgentDiscountPerBookingCard = false
            perSeatDiscountList.clear()
            isEnableCampaignPromotionsChecked = false
        }

        if (perSeatDiscountList.isNotEmpty()) {
            passengerDataList.forEachIndexed { index, passengerItem ->
                perSeatDiscountList.forEach { perSeatDiscount ->
                    if (perSeatDiscount?.seatNo.equals(passengerItem.seatNumber, true)) {
                        passengerDataList.get(index).discountAmount =
                            "${perSeatDiscount?.discountValue ?: ""}"
                    }
                }
            }
        } else {
            passengerDataList.forEach {
                it.discountAmount = ""
            }
        }
    }


    fun setDefaultBranchRoleDiscount(
        paxIndex: Int,
        bookingTypeId: Int,
        none: String,
        fixed: String,
        percentage: String,
        roleDiscountType: String,
        branchDiscountType: String,
    ) {

        if (!rapidBookingSkip) {

            if (applyRoleOrBranchDiscountAtTimeOfBooking || isSeatWiseDiscountEdit) {
                if (!selectedSeatDetails.any { it.isExtraSeat }) {

//                Timber.d("checkAutoDiscountValue==" +
//                        "$selectedBookingTypeId " +
//                        "$branchRoleDiscountType " +
//                        "$branchDiscountValue " +
//                        "$discountType "+
//                        "${passengerDataList[paxIndex].discountAmount}"
//                )

                    if (isAllowToApplyDiscountOnBookingPageWithPercentage && bookingTypeId == 0) {
                        if (discountValue.isNotEmpty() && discountValue != "null") {

                            if (allowAutoDiscount || isSeatWiseDiscountEdit) {
                                setDiscount(paxIndex, discountValue)
                            } else {
                                discountAmount = "0"
                                setDiscount(paxIndex, discountValue)
                            }
                        }
                    }
                    else {
                        if (branchRoleDiscountType.isNotEmpty()) {
                            if (branchRoleDiscountType != none) {

                                if (bookingTypeId == 0) {
                                    if (discountType == percentage
                                        && discountValue.isNotEmpty()
                                        && discountValue != "null"
                                    ) {
                                        val calculateDiscountValue = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * discountValue.toDouble()
                                        discountAmount = "$calculateDiscountValue"
                                        if (!isSeatWiseDiscountEdit) {
                                            setDiscount(paxIndex, discountAmount)
                                        }
                                    } else if (discountType == fixed
                                        && discountValue.isNotEmpty()
                                        && discountValue != "null"
                                    ) {
                                        discountAmount = discountValue
                                        if (!isSeatWiseDiscountEdit) {
                                            setDiscount(paxIndex, discountAmount)
                                        }
                                    }
                                } else if (bookingTypeId == 1 || bookingTypeId == 2) {
                                    discountAmount = "0"
                                    if (!isSeatWiseDiscountEdit) {
                                        setDiscount(paxIndex, discountAmount)
                                    }
                                } else if (branchRoleDiscountType == branchDiscountType) {

                                    if (discountType == percentage && branchDiscountValue.isNotEmpty()) {
                                        if (!isAllowToApplyDiscountOnBookingPageWithPercentage) {
                                            val calculateBranchDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * branchDiscountValue.toDouble()
                                            discountAmount = "$calculateBranchDiscount"

                                        } else {
                                            discountAmount = branchDiscountValue
                                        }
//                                if (passengerDataList[paxIndex].discountAmount?.isEmpty() == true && !isSeatWiseDiscountEdit) {
//                                    setDiscount(paxIndex, discountAmount)
//                                }
                                        if (!isSeatWiseDiscountEdit) {
                                            setDiscount(paxIndex, discountAmount)
                                        }
                                    } else if (discountType == fixed && branchDiscountValue.isNotEmpty()) {
//                                    if (allowToApplyCurrentUserRoleBranchDiscount && !applyRoleOrBranchDiscountAtTimeOfBooking) {
//                                        discountAmount = branchDiscountValue
//                                    } else {
//                                        val calculateBranchDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * branchDiscountValue.toDouble()
//                                        discountAmount = "$calculateBranchDiscount"
//                                    }
                                        discountAmount = branchDiscountValue

                                        if (!isSeatWiseDiscountEdit) {
                                            setDiscount(paxIndex, discountAmount)
                                        }
                                    }
                                } else if (branchRoleDiscountType == roleDiscountType) {
                                    if (discountType == percentage && roleDiscountValue.isNotEmpty()) {
                                        if (!isAllowToApplyDiscountOnBookingPageWithPercentage) {
                                            val calculateRoleDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * roleDiscountValue.toDouble()
                                            discountAmount = "$calculateRoleDiscount"
                                        } else {
                                            discountAmount = roleDiscountValue
                                        }

                                        if (!isSeatWiseDiscountEdit) {
                                            setDiscount(paxIndex, discountAmount)
                                        }
                                    } else if (discountType == fixed && roleDiscountValue.isNotEmpty()) {
//                                    if (allowToApplyCurrentUserRoleBranchDiscount && !applyRoleOrBranchDiscountAtTimeOfBooking) {
//                                        discountAmount = roleDiscountValue
//                                    } else {
//                                        val calculateRoleDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * roleDiscountValue.toDouble()
//                                        discountAmount = "$calculateRoleDiscount"
//                                    }

                                        discountAmount = roleDiscountValue

                                        if (!isSeatWiseDiscountEdit) {
                                            setDiscount(paxIndex, discountAmount)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                discountAmount = "0"
                if (!isSeatWiseDiscountEdit) {
                    setDiscount(paxIndex, discountAmount)
                }
            }

        } else {
            discountAmount = "0"
            if (!isSeatWiseDiscountEdit) {
                setDiscount(paxIndex, discountAmount)
            }
        }

        Timber.d("checkAutoDiscountValue==" +
                "$selectedBookingTypeId " +
                "$branchRoleDiscountType " +
                "$branchDiscountValue " +
                "$discountType "+
                "${passengerDataList[paxIndex].discountAmount}"
        )
    }

    fun setBranchRoleDiscountConfiguration(
        paxIndex: Int,
        none: String,
        fixed: String,
        percentage: String,
        bookingTypeId: Int,
        roleDiscountType: String,
        branchDiscountType: String,
        allowedMaxDiscountMessage: String,
        allowedBranchRoleMaxDiscountMessage: String,
    ) {
        if (isAllowToApplyDiscountOnBookingPageWithPercentage && bookingTypeId == 0) {
            try {
                if (discountValue.toDouble() == 0.0 || discountValue.toInt() == 0) {
                    val perSeatFare = passengerDataList[paxIndex].fare.toString().toDouble()

                    if (discountAmount.toDouble() > perSeatFare && branchRoleDiscountType != none) {
                        discountAmount = discountValue
                        setDiscount(paxIndex, getValidatedNumber(discountAmount))
                        _showValidationMessage.postValue("$allowedMaxDiscountMessage $perSeatFare")
                    }
                } else {
                    if (discountAmount.toDouble() > discountValue.toDouble() && branchRoleDiscountType != none) {
                        discountAmount = discountValue
                        setDiscount(paxIndex, getValidatedNumber(discountAmount))
                        _showValidationMessage.postValue("$allowedMaxDiscountMessage $discountAmount")
                    }
                }

            } catch (_: Exception) {
            }
        } else {
            if (branchRoleDiscountType.isNotEmpty()) {
                if (branchRoleDiscountType != none) {
                    if (selectedBookingTypeId == 0) {
                        if (discountType == percentage && discountValue.isNotEmpty()) {
                            val calculateDiscountValue = passengerDataList[paxIndex].fare.toString()
                                .toDouble() / 100.0f * discountValue.toDouble()
                            try {
                                if (discountAmount.toDouble() > calculateDiscountValue) {
                                    discountAmount = "$calculateDiscountValue"
                                    setDiscount(paxIndex, getValidatedNumber(discountAmount))
                                    _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $calculateDiscountValue")
                                }
                            } catch (_: Exception) {
                            }

                        } else if (discountType == fixed && discountValue.isNotEmpty()) {
                            try {
                                if (discountAmount.toDouble() > discountValue.toDouble()) {
                                    discountAmount = discountValue
                                    setDiscount(paxIndex, getValidatedNumber(discountAmount))
                                    _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $discountValue")
                                }
                            } catch (_: Exception) {
                            }
                        }
                    } else if (branchRoleDiscountType == branchDiscountType) {

                        if (discountType == percentage && branchDiscountValue.isNotEmpty()) {
                            if (!isAllowToApplyDiscountOnBookingPageWithPercentage) {
                                val calculateBranchDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * branchDiscountValue.toDouble()
                                try {
                                    if (discountAmount.toDouble() > calculateBranchDiscount) {
                                        discountAmount = "$calculateBranchDiscount"
                                        setDiscount(paxIndex, getValidatedNumber(discountAmount))
                                        _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $calculateBranchDiscount")
                                    }
                                } catch (_: Exception) {
                                }
                            } else {
                                try {
                                    if (discountAmount.toDouble() > branchDiscountValue.toDouble()) {
                                        discountAmount = "$branchDiscountValue"
                                        setDiscount(paxIndex, getValidatedNumber(discountAmount))
                                        _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $branchDiscountValue")
                                    }
                                } catch (_: Exception) {
                                }
                            }

                        } else if (discountType == fixed && branchDiscountValue.isNotEmpty()) {
//                            if (allowToApplyCurrentUserRoleBranchDiscount && !applyRoleOrBranchDiscountAtTimeOfBooking) {
//                                try {
//                                    if (discountAmount.toDouble() > branchDiscountValue.toDouble()) {
//                                        discountAmount = branchDiscountValue
//                                        setDiscount(paxIndex, discountAmount)
//                                        _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $branchDiscountValue")
//                                    }
//                                } catch (_: Exception) {
//                                }
//                            } else {
//                                val calculateBranchDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * branchDiscountValue.toDouble()
//
//                                try {
//                                    if (discountAmount.toDouble() > calculateBranchDiscount) {
//                                        discountAmount = "$calculateBranchDiscount"
//                                        setDiscount(paxIndex, discountAmount)
//                                        _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $calculateBranchDiscount")
//                                    }
//                                } catch (_: Exception) {
//                                }
//                            }

                            try {
                                if (discountAmount.toDouble() > branchDiscountValue.toDouble()) {
                                    discountAmount = branchDiscountValue
                                    setDiscount(paxIndex, getValidatedNumber(discountAmount))
                                    _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $branchDiscountValue")
                                }
                            } catch (_: Exception) {
                            }

                        }
                    } else if (branchRoleDiscountType == roleDiscountType) {
                        if (discountType == percentage && roleDiscountValue.isNotEmpty()) {
                            if (!isAllowToApplyDiscountOnBookingPageWithPercentage) {
                                val calculateRoleDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * roleDiscountValue.toDouble()
                                try {
                                    if (discountAmount.toDouble() > calculateRoleDiscount) {
                                        discountAmount = "$calculateRoleDiscount"
                                        setDiscount(paxIndex, getValidatedNumber(discountAmount))
                                        _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $calculateRoleDiscount")
                                    }
                                } catch (_: Exception) {
                                }
                            } else {
                                try {
                                    if (discountAmount.toDouble() > roleDiscountValue.toDouble()) {
                                        discountAmount = "$roleDiscountValue"
                                        setDiscount(paxIndex, getValidatedNumber(discountAmount))
                                        _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $roleDiscountValue")
                                    }
                                } catch (_: Exception) {
                                }
                            }
                        } else if (discountType == fixed && roleDiscountValue.isNotEmpty()) {
//                            if (allowToApplyCurrentUserRoleBranchDiscount && !applyRoleOrBranchDiscountAtTimeOfBooking) {
//                                try {
//                                    if (discountAmount.toDouble() > roleDiscountValue.toDouble()) {
//                                        discountAmount = roleDiscountValue
//                                        setDiscount(paxIndex, discountAmount)
//                                        _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $roleDiscountValue")
//                                    }
//                                } catch (_: Exception) {
//                                }
//                            } else {
//                                val calculateRoleDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * roleDiscountValue.toDouble()
//
//                                try {
//                                    if (discountAmount.toDouble() > calculateRoleDiscount) {
//                                        discountAmount = "$calculateRoleDiscount"
//                                        setDiscount(paxIndex, discountAmount)
//                                        _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $calculateRoleDiscount")
//                                    }
//                                } catch (_: Exception) {
//                                }
//                            }

                            try {
                                if (discountAmount.toDouble() > roleDiscountValue.toDouble()) {
                                    discountAmount = roleDiscountValue
                                    setDiscount(paxIndex, getValidatedNumber(discountAmount))
                                    _showValidationMessage.postValue("$allowedBranchRoleMaxDiscountMessage $roleDiscountValue")
                                }
                            } catch (_: Exception) {
                            }

                        }
                    }
                }
            }
        }
    }

    // humsafar
    fun setBoardingPointText() {
        if (boardingSpinnerList.isNotEmpty()) {
            boardingPoint =
                boardingSpinnerList[0].value
            boardingId = boardingSpinnerList[0].id
        }
    }

    fun setDroppingPointText() {
        if (droppingSpinnerList.isNotEmpty()) {
            droppingPoint =
                droppingSpinnerList[0].value
            droppingId = droppingSpinnerList[0].id
        }
    }
}