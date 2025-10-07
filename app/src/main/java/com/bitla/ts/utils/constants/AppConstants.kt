package com.bitla.ts.utils.constants

//eazzbuzz
object EasebuzzStatus {
    const val EASEBUZZ_PAYMENT_ERROR="payment_failed"
    const val EASEBUZZ_PAYMENT_SUCCESS_STATUS="payment_successfull"
    const val EASEBUZZ_PAYMENT_CANCELLED="user_cancelled"
}
const val EASEBUZZ_REQUEST_CODE = 111
const val DATE_FORMAT_D_M_Y = "dd-MM-yyyy"
const val DATE_FORMAT_D_M_Y_SLASH = "dd/MM/yyyy"
const val DATE_FORMAT_YYYY = "yyyy"
const val DATE_FORMAT_Y_M_D = "yyyy-MM-dd"
const val DATE_FORMAT_Y_M_D_SLASH = "yyyy/MM/dd"

//network request codes
const val STATUS_CODE_NO_API_RESPONSE = "600"

//const val DATE_FORMAT_y_M_D = "yyyy--dd"
const val DATE_FORMAT_D_MONTH = "dd MMMM"
const val DATE_FORMAT_D_MON = "dd MMM"
const val DATE_FORMAT_D_MON2 = "d MMM"
const val DATE_FORMAT_D_MON3 = "MMM d"
const val DATE_FORMAT_D_MON4 = "MMM dd"
const val DATE_FORMAT_E_D_M_Y = "EEE, dd MMMM yyyy"
const val DATE_FORMAT_E_D_MMM_Y = "EEE, dd MMM yyyy"
const val DATE_FORMAT_DD_MMM_HH_MM = "dd MMM-HH:MM"
const val DATE_FORMAT_YMDTHMS = "yyyy-MM-dd'T'HH:mm:ss"
const val DATE_FORMAT_E_D_M_YY = "EEE, dd MMMM`yy"
const val DATE_FORMAT_D_M_YY = "dd MMM yyyy"
const val DATE_FORMAT_EEE_DD_MMM = "EEE dd MMM"
const val DATE_FORMAT_DD_MMM = "dd MMM"
const val DATE_FORMAT_MMM_DD_EEE_YYYY = "MMM dd EEE yyyy"
const val DATE_FORMAT_MMMM_DD_EEEE_YYYY = "MMMM dd EEEE yyyy"
const val DATE_FORMAT_MMM_DD = "dd MMM"
const val DATE_FORMAT_D_MMMM_Y = "dd MMMM yyyy"
const val DATE_FORMAT_D_MMM_Y = "dd MMM yyyy"
const val DATE_FORMAT_12 = "hh:mm a"
const val DATE_FORMAT_24 = "HH:mm"
const val DATE_FORMAT_HH_MM_24 = "HH:MM"
const val DATE_FORMAT_D_M_Y_H_M = "dd-MM-yyyy hh:mm"
const val DATE_FORMAT_D_M_Y_H_M_S = "dd-mm-yyyy HH:mm:ss"
const val DATE_FORMAT_EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY = "EEE MMM dd HH:mm:ss zzz yyyy"
const val DATE_FORMAT_HH_MM_12_24_DD_MM_YYYY = "HH:mm a'T'dd-MM-yyyy"
const val DATE_FORMAT_HH_MM__DD_MM_YYYY = "HH:mm'T'dd-MM-yyyy"
const val DATE_FORMAT_D_M_Y_H_M_AP = "dd/MM/yyyy hh:mm a"
const val DATE_FORMAT_D_M_Y_NEW_LINE_H_M_AP = "dd/MM/yyyy\nhh:mm a"


const val RESULT_CODE_SOURCE = 1001
const val RESULT_CODE_DESTINATION = 1002
const val RESULT_CODE_SELECT_SERVICE = 1003
const val RESULT_CODE_SEARCH_AGENT = 1004
const val RESULT_CODE_SEARCH_BRANCH = 1005
const val RESULT_CODE_SEARCH_USER = 1006
const val STORAGE_PERMISSION_CODE = 1007
const val REQUEST_ENABLE_BT = 1008
const val PERMISSION_BLUETOOTH = 1009
const val PERMISSION_BLUETOOTH_ADMIN = 1010
const val PERMISSION_BLUETOOTH_CONNECT = 1011
const val PERMISSION_BLUETOOTH_SCAN = 1012
const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 1013
const val REQUEST_CAMERA_PERMISSION = 1014
const val IMAGE_CAPTURE_CODE = 1015
const val REQUEST_WRITE_PERMISSION = 1016
const val CALL_PHONE_PERMISSION = 1017
const val REQUEST_READ_PERMISSION = 1018  // Don't use this variable in other activities as it is used in BaseActivity
const val RAPID_TYPE_DEFAULT = 0
const val RAPID_TYPE_OPTIONAL = 1
const val RAPID_TYPE_HIDE = 2
const val DELAY_MILLIS_24 = 2400L
const val DELAY_MILLIS_18 = 1800L
const val ADDRESS_PATTERN_CHECK = ".*[A-Za-z].*"
const val ANDROID_NATIVE_TYPE = 10

object Pagination{
const val INITIAL_PAGE=1
const val PER_PAGE=20
}

object AgentPaymentOptions{
    const val PAY_VIA_QR = 15
    const val PAY_VIA_SMS = 16
    const val PAY_VIA_UPI = 17
    const val PHONEPE_V2 = 18
}

object EasebuzzPaymentOptions{
    const val PAY_VIA_QR = 20
    const val PAY_VIA_ONLINE_LINK = 21
    const val PAY_VIA_UPI = 22
}

const val PAYMENT_TYPE_EZETAP = 14
const val PAYMENT_TYPE_PAYTM = 14
const val PAYMENT_TYPE_PINELAB_QR = 6
const val PAYMENT_TYPE_PINELAB_CREDIT_DEBIT = 6
const val PAYMENT_TYPE_CASH = 1


object ReportDateType{
    const val ISSUE_DATE=1
    const val TRAVEL_DATE=2
}



//Remote config const
const val isUpdate = "is_update"
const val updateAppUrl = "update_url"
const val tsPackageName = "ts_package_name"
const val versionCode = "version_code"
const val isUpdateCritical = "is_critical"
const val updateTitle = "title"
const val updateDescription = "description"
const val isDestinationPairCacheEnable = "is_destination_pair_cache"
const val location_popup_string = "location_popup_string"
const val destinationPairTime = "destination_pair_minutes"
const val sendErrorReport = "send_error_report"
const val manualUpdatePopup = "manual_update_popup"
const val inAppUpdateTS = "in_app_update_ts"
const val updateCountryList = "update_country_list"
const val isSentryEnabled = "is_sentry_enabled"
const val sentryForCountry = "enable_sentry_for_country"
const val sentryPerformanceForCountry = "enable_sentry_performance_for_country"
const val locationApiInterval = "location_api_interval"


const val IS_FROM_AGENT = "is_from_agent"
const val PREVIOUS_SCREEN = "Previous Screen"

// Hameburger Tab
//--------Change user option------------
const val OPERATOR_NAME = "operator_name"
const val LOGIN_ID = "login_id"
const val ROLE_NAME = "role_name"
const val COUNTRY_CODE = "country_code"
const val API_CRASH = "api_crash"

//--------Recharge------------
const val RECHARGE_FOR = "recharge_for"
const val PAYMENT_TYPE = "payment_type"
const val TRANSACTION_TYPE = "transaction_type"
const val RECHARGE_BUTTON = "recharge_button"

//--------Coach layout------------
const val COACH_LAYOUT_SELECTION = "coach_layout_type"

//--------Coach layout------------
const val FINGERPRINT_OPTION = "fingerprint_option"













const val BASE_URL = "generate_download_report_clicks"
const val SEARCH_COACH_REQUEST_CODE = 22

//Dashboard Fragment Intent
const val TOTAL_SELECTED_SERVICES_INTENT = "TOTAL_SELECTED_SERVICES"
const val SELECT_SERVICE_INTENT_REQUEST_CODE = 2
const val SELECT_BRANCH_INTENT_REQUEST_CODE = 4
const val RESTART_OCCUPANCY_GRID_ACTIVITY_REQUEST_CODE = 6

const val PERMISSION_LOCATION_REQUEST_CODE = 1
const val PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE = 2

const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"

const val MAX_PERCENTAGE_FARE = 3
const val MAX_FIXED_FARE = 7

const val DASHBOARD_CHART_LIMIT = 1000
const val CUSTOM_ELLIPSIS = ".."
const val SUBSTRING_START = 0
const val SUBSTRING_END = 15
const val DEVICE_LANG_LOG_FILENAME = "device_lang.txt"


const val PARTIAL_CONFIRM_BTN = "partial confirm btn"
const val PARTIAL_RELEASE_BTN = "partial release btn"
const val PARTIAL_PAYMENT_OPTION = "partial payment option"
const val SELECTED_MEAL_INFO = "selected meal info"



const val NOTIFICATION_SILENT = "silent"
const val NOTIFICATION_SYSTEM_SOUND = "system_sound"
const val NOTIFICATION_DEFAULT_SOUND = "default_sound"
const val get_coach_details = "get_coach_details"
const val get_coach_documents = "get_coach_documents"
const val EDITED_FARE_VALUE = 2
const val APPLIED_VALUE = 3
const val PHONE_BOOKING_SELECTED = "phone booking selected"
const val PHONE_BOOKING_NOT_SELECTED = "phone booking not selected"
const val FARE_DETAILS = "fare details"
const val BOOK_TICKET = "book ticket"
const val DEFAULT_TEXT_SIZE = "default size"
const val SMALL_TEXT_SIZE = "small size"
const val LARGE_TEXT_SIZE = "large size"
const val XLARGE_TEXT_SIZE = "x large size"

//file storage permission
const val STORAGE_PERMISSION = 1234
const val REDIRECT_FROM = "redirect from"

const val AGENT_FROM_ALL_DROPPING_POINTS = "all dropping points"
const val FREQUENT_SEARCH = "frequent search"
const val DROPPING_SELECTION = "dropping selection"

//pinelab
const val IS_PINELAB_DEVICE = false
const val PAYMENT_QR = 6
const val PAYMENT_DEBIT_CREDIT = 7

const val ACTIVE_SERVICE = "active_service"
const val INACTIVE_SERVICE = "inactive_service"

const val DB_NAME = "users_database.db"
const val USERS_TABLE_NAME = "users_table"

const val INDIA = "India"

//souce destination dropdown
const val SOURCE = 1
const val DESTINATION = 2
const val SERVICE = 3
const val HUB = 4
const val COACH_TYPE = 5

// update details (crew details) dropdown
const val COACH_UPDATE_DETAILS = 1
const val DRIVER_1_UPDATE_DETAILS = 2
const val DRIVER_2_UPDATE_DETAILS = 3
const val DRIVER_3_UPDATE_DETAILS = 4
const val CLEANER_UPDATE_DETAILS = 5
const val CONDUCTOR_UPDATE_DETAILS = 6
const val CHECKING_INSPECTOR_UPDATE_DETAILS = 7

const val PHONE_VALIDATION_COUNT= 100




object BookingIcon {
    const val BOOKING_ICON = "Phone blocked"
}




object MultistationBooking {
    const val MULTISTATION_BOOKINGS = "Multistation booking"
}

object BoardingPoints {
    const val BOARDING_POINTS = "Boarding points"
}

object DroppingPoints {
    const val DROPPING_POINTS = "Dropping points"
}


object BookedBy {
    const val BOOKED_BY = "Booked By"
}


object BookingTicketNo {
    const val BOOKINGS_WITH_TICKET_NO = "Bookings with Ticket No"
}


object CollectionPerTicket {
    const val COLLECTION = "Collection"
}


object ReleasedTickets {
    const val RELEASED_TICKETS = "Released Tickets"
}


object UpdateDetailsBookClick {
    const val UPDATE_DETAILS = "Update details"
}

object CallOptionClicks {
    const val BOOK_TICKET_COACH_FRAGMENT = "Call Option Clicks - Book ticket Coach Fragment"
}

object CrewToolkitChecklist {
    const val CREW_TOOLKIT_CHECKLIST = ""
}


object RetrivePassanger {
    const val LANGUAGE_SELECTION = "language selection"
}









//Below mentioned constants are used in the app for the purpose of analytics
const val APP_LOGIN = "app_login"
const val ONE_TOUCH_LOGIN = "one_touch_login"
const val RESET_PASSWORD = "reset_password"
const val DASHBOARD_ICON = "dashboard_icon"
const val VIEW_DETAILS = "view_details"
const val ALL_SERVICES = "all_services"
const val OCCUPANCY = "occupancy "
const val REVENUE = "revenue"
const val BOOKING_TRENDS = "booking_trends"
const val SERVICE_WISE_BOOKING = "service_wise_booking"
const val SCHEDULES_SUMMARY = "schedules_summary"
const val PHONE_BLOCKED = "phone_blocked"
const val PENDING_QUOTA = "pending_quota"
const val SORT_BTN = "sort_btn"
const val CHANGE_GRAPH = "change_graph"
const val OCC_CALENDAR = "occ_calendar"
const val BOOKING_ICON = "booking_icon"
const val MY_BOOKINGS = "my_bookings"
const val BOOK_TICKETS = "book_tickets"
const val TODAY = "today"
const val TOMORROW = "tomorrow"
const val RAPID_BOOK = "rapid_book"
const val BOOKINGPG_SEND_SMS = "bookingpg_send_sms"
const val BOOKINGPG_UPDATE_DETAILS = "bookingpg_update_details"
const val BLOCK_RESERVATION = "block_reservation"
const val BOOK_EXTRA_SEATS = "book_extra_seats"
const val MULTISTATION_BOOKING = "multistation_booking"
const val BOARDING_POINTS = "boarding_points"
const val DROPPING_POINTS = "dropping_points"
const val BOOKED_BY = "booked_by"
const val UPDATE_DETAILS_BOOKCLICK = "update_details_bookclick"
const val BOOKING_TICKETS_NO = "booking_tickets_no"
const val COLLECTION_PER_TICKET = "collection_per_ticket"
const val FREQUENT_TRAVELLER = "frequent_traveller"
const val RELEASED_TICKETS = "released_tickets"
const val SERVICE_SUMMARY = "service_summary"
const val AMENITIES = "amenities"
const val CANCELLATION = "cancellation"
const val SUMMARY = "summary"
const val BOOKING_SUCCESS = "booking_success"
const val BOOKING_FAILED = "booking_failed"
const val PICK_UP_ICON = "pick_up_icon"
const val EDIT_CHART = "edit_chart"
const val ANNOUNCEMENT = "announcement"
const val CREW_TOOLKIT_CHECKLIST = "crew_toolkit_checklist"
const val YET_TO_BOARD = "yet_to_board"
const val STATUS = "status"
const val MODIFY = "modify"
const val MODIFY_DETAILS = "modify_details"
const val VIEW_TICKET = "view_ticket"
const val CLOSE_CHART = "close_chart"
const val SHOW_ALL = "show_all"
const val APPLY_FILTER = "apply_filter"
const val DOWNLOAD_ICON = "download_icon"
const val PASSENGER_LIST = "passenger_list"
const val VIEW_OPTION = "view_option"
const val TICKET_DETAILS_CALL_ICON = "ticket_details_call_icon"
const val VIEW_PASSENGER = "view_passenger"
const val EMAIL_ICON = "email_icon"
const val WHATSAPP_ICON = "whatsapp_icon"
const val TICKET_SHIFT = "ticket_shift"
const val BOOKING_HISTORY = "booking_history"
const val MENU = "menu"
const val FROM_CITY = "from_city"
const val TO_CITY = "to_city"
const val APPLY = "apply"
const val REPORT = "report"
const val FROM_DATE = "from_date"
const val TO_DATE = "to_date"
const val ADD_REPORT_STARRED_TAB = "add_report_starred_tab"
const val ALL_REPORTS = "all_reports"
const val DOWNLOAD_REPORT = "download_report"
const val MAIN_MENU = "main_menu"
const val APP_LOGOUT = "app_logout"
const val SETTINGS = "settings"
const val ADD_FINGERPRINT = "add_fingerprint"
const val LANGUAGE_OPTION = "language_option"
const val PRINTER_SELECTION = "printer_selection"
const val RAPID_BOOKING_SELECTION = "rapid_booking_selection"
const val LANGUAGE_SELECTED = "language_selected"
const val LANDING_PAGE_SELECTED = "landing_page_selected"
const val LANDING_PAGE = "landing_page"
const val PAYMENT_STATUS = "payment_status"

//Bookings Tab - (Book Ticket)
const val PNR_SEARCH = "pnr_search"
const val NEW_BOOKING = "new_booking"
const val ORIGIN_POINT_CLICKS = "origin_point_clicks"
const val DESTINATION_POINT_CLICKS = "destination_point_clicks"
const val ORIGIN_DESTINATION_SWAP_CLICKS = "origin_destination_swap_clicks"
const val TODAY_CLICKS = "today_clicks"
const val TOMORROW_CLICKS = "tomorrow_clicks"
const val SEARCH_BUTTON = "search_button"

//Ticket Details
const val SHARE_ICON = "share_icon"
const val SHARE_VIA_EMAIL = "email_share"
const val SHARE_VIA_SMS = "sms_share"
const val SHARE_VIA_WA = "whatsapp_share"
const val UPDATE_TICKET = "update_ticket"
const val CANCEL_TICKET = "cancel_ticket"
const val SHIFT_PAX = "shift_pax_options"
const val TICKET_BOOKED_SUCCESSFULLY = "ticket_booked_successfully"
const val TICKET_BOOKED_FAILED = "ticket_booked_failed"
const val BULK_SHIFT_PASSENGER = "bulk_shift_passenger"
const val PNR_WISE_SHIFT_PASSENGER = "pnr_wise_shift_passenger"

//Bookings Tab - (MyBooking)
const val VIEW_BOOKING = "view_booking"
const val EDIT_PASSENGER_DETAILS = "edit_passenger_details"
const val FILTER_SELECTION = "filter_selection"

// Dashboard Tab
const val MOST_SEARCHED = "most_searched"
const val PENDING_API_TICKETS = "pending_api_tickets"
const val PENDING_E_TICKETS = "pending_e_tickets"
const val RELEASE_TICKET = "release_ticket"
const val CONTINUE_LAST_SEARCH = "continue_last_search"
const val ACCOUNT_DETAILS = "Account Details_HamburgerMenu"


// SRP
const val FILTERS_OPTIONS = "filters_option"
const val UPDATE_RATE_CARD = "update_rate_card"
const val RATE_CARD = "rate_card"
const val EXTEND_FARE_SETTINGS = "extend_fare_settings"
const val RESERVATION_CHART = "reservation_chart"
const val PICKUP_CHART_BOOK = "pickupchart_book"

object ViewDetails {
    const val BRANCH_WISE_REVENUE = "View Details - Branch Wise Revenue"
    const val EMPTY_SEAT_WISE_REVENUE = "View Details - Empty Seats Wise Revenue"
    const val GST_COLLECTION_REVENUE = "View Details - GST Collection Revenue"
    const val DAY_WISE_REVENUE = "View Details - Day Wise Revenue"
    const val SERVICE_WISE_REVENUE = "View Details - Service Wise Revenue"

    const val E_BOOKING_TRENDS = "View Details - E-Booking Trends"
    const val BRANCH_WISE_BOOKING_TRENDS = "View Details - Branch Wise Booking Trends"
    const val DAY_WISE_BOOKING_TRENDS = "View Details - Day Wise Booking Trends"

    const val DAY_WISE_OCCUPANCY = "View Details - Day Wise Occupancy"
    const val SERVICE_WISE_OCCUPANCY = "View Details - Service Wise Occupancy"
    const val OCCUPANCY_BY_SOURCE = "View Details - Occupancy By Booking Source"
    const val OCCUPANCY_BY_SEAT_STATUS = "View Details - Occupancy By Seat Status"
    const val STATUS = "status"
}

// Pickup Chart (Passenger List TAB)
const val LUGGAGE_OPTION_CLICK = "luggage_option_clicks"
const val CALL_OPTION_CLICKS = "call_option_clicks"
const val QR_CODE_OPTION = "qr_code_option_clicks"
const val PRINT_OPTION = "print_option_clicks"
const val BOARDED_YES_NO = "boarded_yes_no"
const val ETA_CLICK = "eta_clicks"


object PaymentStatus {
    const val STATUS = "status"
}


object AllService {
    const val ALL_SERVICE = "All services"
}

object AddFingerprint {
    const val ADD_CLICK = "Add click"
}



object DashboardIcon {
    const val DASHBOARD_ICON = "Dashboard icon"
}

object PendingQuote {
    const val PENDING_QUOTA = "Pending quota"
}




object Revenue {
    const val REVENUE = "Revenue"
}


object SchedulesSummary {
    const val SCHEDULES_SUMMARY = "schedules summary"
}


object SortBtn {
    const val SERVICE_WISE_BOOKING_SORT = "Service-Wise Booking sort"
    const val BRANCH_CONTRIBUTION_BOOKING_TRENDS_SORT = "Branch-Contribution Booking Trends sort"
    const val E_BOOKING_TRENDS_BOOKINGS_TRENDS_SORT = "E-Booking Trends Booking Trends sort"
    const val BOOKING_SOURCE_WISE_SORT = "Booking Source-Wise sort"
    const val DAY_WISE_OCCUPANCY_SORT = "Day-wise Occupancy sort"
    const val SEAT_STATUS_WISE_SORT = "Seat Status-Wise sort"
    const val BRANCH_WISE_REVENUE_SORT = "Branch-wise Revenue sort"
    const val DAY_WISE_COLLECTION_REVENUE_SORT = "Day-wise Collection Revenue sort"
    const val EMPTY_SEATS_WISE_REVENUE_SORT = "Empty Seats-Wise Revenue sort"
    const val GST_COLLECTION_REVENUE_SORT = "GST Collection Revenue sort"
    const val SERVICE_WISE_COLLECTION_REVENUE_SORT = "Service-wise Collection Revenue sort"
}


// BLock-Unblock
const val BLOCK_UNBLOCK = "bookingpage_block"

// Pickup Chart
const val SEND_SMS = "send_sms"
const val UPDATE_DETAILS = "update_details"
const val CLOSE_RESERVATION = "close_reservation"
const val LOCK_CHART = "lock_chart"
const val VIEW_RESERVATION_CHART = "view_reservation_chart_option"
const val VIEW_COACH_LAYOUT_CHART = "view_coach_layout_chart_option"
const val VIEW_SUMMARY_CLICKS = "view_summary_clicks"

// Pickup Chart (Reservation Chart menu [Shift PAX tab])
const val SHIFT_PAX_SEAT_OPTIONS = "shift_pax_seat_options"

// Pickup Chart (Reservation Chart menu [Collection tab])
const val COLLECTION_TAB_CLICKS = "collection_tab_clicks"

//Pickup Chart (ViewReservation Chart menu [Bulk Cancel tab])
const val BULK_CANCEL_TAB_CLICKS = "bulk_cancel_tab_clicks"
const val SELECT_ALL_CLICKS = "select_all_clicks"
const val CANCEL_FINAL_CTA_CLICKS = "cancel_final_cta_clicks"

// Reports Tab
const val REPORT_TYPE_SELECTION = "report_type_selection"
const val REPORT_SERVICE_SELECTION = "report_service_selection"
const val ADD_FAVORITES_REPORT_CHECK = "add_favorites_report_check_selection"
const val GENERATE_DOWNLOAD_REPORT_CLICKS = "generate_download_report_clicks"



object EditChart {
    const val EDIT_CHART = "Edit Chart"
}

object LandingPage {
    const val LANDING_PAGE_SELECTION = "Landing page selection"
}

object LanguageOption {
    const val LANGUAGE_SELECTION = "language selection"
}

//coach
const val CREW_SEAT_LAYOUT = "Crew_SeatLayout"

//passenger page
const val RETRIEVE_PASSENGER = "Retrieve_Passenger_Details"

object ChangeGraph {
    const val CHANGE_GRAPH = "Change Graph"
}

object BookingTrends {
    const val BOOKING_TRENDS = "Booking trends"
}

object Occupancy {
    const val OCCUPANCY = "Occupancy"
}


object PhoneBlocked {
    const val PHONE_BLOCKED = "Phone blocked"
}

object BookTickets {
    const val BOOK_TICKETS = "Book Tickets"
}

object NewBookings {
    const val New_BOOKING_SRP = "New Booking - SRP"
}


object RapidBook {
    const val RAPID_BOOK = "Rapid Book"
}


object BookingPGSendSms {
    const val SEND_SMS = "Send SMS"
}

object BookingPGUpdateDetails {
    const val UPDATE_DETAILS = "Update Details"
}

object BlockUnblock {
    const val BLOCK = "block"
}

object BlockReservation {
    const val BLOCK = "Block"
}

object EtaClick {
    const val ETA_OPTIONS_CLICKS_VIEW_RESERVATION = "ETA Option Clicks - ViewReservation"
}


object DownloadIcon {
    const val DOWNLOAD_ICON = "Download icon"
}

object PickUpChartBook {
    const val PICKUP_CHART_BOOK = "pickupchart_book"
}


object BulkCancelTabClicks {
    const val VIEW_RESERVATION = "BulkCancel Tab Clicks - ViewReservation"
}

object CollectionTabClicks {
    const val VIEW_RESERVATION = "Collection Tab Clicks - ViewReservation"
}

object Announcement {
    const val ANNOUNCEMENT = "Announcement"
}


object ResetPassword {
    const val REST_PASSWORD_CLICK = "Reset Password click"
}

object MostSearched {
    const val MOST_SEARCHED="Most Searched"
}

object ContinueSearch{
    const val CONTINUE_LAST_SEARCH="Continue Last Search"
}

object PendingSearch{
    const val PENDING_SEARCH="Pending API Tickets"
}


object PendingETicket{
    const val PENDING_E_TICKET="Pending E Tickets"
}

object ReleaseTicket{
    const val RELEASE_TICKET_DASHBOARD="Release Ticket - Dashboard"
}

object OccCalendar{
    const val OCCUPANCY_CALENDAR="Occupancy Calendar"
}


object MainMenu{
    const val HAMBURGER_MENU_CLICK="Hamburger menu click"
}


object AccountDetailHamburgerMenu{
    const val ACCOUNT_DETAILS_HAMBURGER_MENU="Account Details_HamburgerMenu"
}


object SETTING{
    const val SETTINGS_CLICK="Settings click"
}


object AppLogout{
    const val LOG_OUT="Logout"
}

object TicketBookedSuccessfully{
    const val TICKET_BOOKED_SUCCESSFULLY="Ticket Booked Successfully"
}


object TicketBookedFailed{
    const val TICKET_BOOKED_FAILED="Ticket Booked Successfully"
}

object BookingHistory{
    const val CLOCK_ICON="clock icon"
}
object ViewPassanger{
    const val VIEW_PASSANGER="View passenger"
}


object TicketDetails{
    const val NEW_BOOKING_TICKET_DETAILS="New Booking - TicketDetails"
    const val UPDATE_TICKET_TICKET_DETAILS="Update Ticket - TicketDetails"
    const val CANCEL_TICKET_TICKET_DETAILS="Cancel Ticket - TicketDetails"
}



object CallOptionsClick{
    const val CALL_ICON_TICKET_DETAILS="Call Icon - TicketDetails"
}


object ShareViaSms{
    const val WHATSAPP_SHARE_TICKET_DETAILS="Whatsapp Share - TicketDetails"
}
object SmsShare{
    const val SMS_SHARE_TICKET_DETAILS="SMS Share - TicketDetails"
}

object EmailShare{
    const val EMAIL_SHARE_TICKET_DETAILS="Email Share - TicketDetails"
}


object ShareIcon{
    const val SHARE_ICON_TICKET_DETAILS="Share Icon- TicketDetails"
}

object RelaseTicket{
    const val RELEASE_TICKET_DETAILS="Release Ticket- TicketDetails"
}

object ShiftPax{
    const val SHIFT_PASSENGER_TICKET_DETAILS="Shift Passenger - TicketDetails"
    const val PNR_WISE_SHIFT_PASSENGER="Shift Passenger - PNR wise"
    const val BULK_SHIFT_PASSENGER="Shift Passenger - Bulk"
}


object Report{
    const val REPORT_ICON="Reports icon"
}
object PassengerList{
    const val PASSENGER_LIST="Passenger list"
}

object PrintOptions{
    const val PRINT_OPTION_CLICKS_VIEW_RESERVATION="Print Option Clicks - ViewReservation"
}


object ApplyFilter{
    const val APPLY_FILTER="Apply filter"
}

object ModifyDetails{
    const val MODIFY_DETAILS="Modify Details"
}

object CloseChart{
    const val CLOSE_CHART="Close chart"
}


object ShowAll{
    const val SHOW_ALL="Show all"
}

object Status{
    const val STATUS="Status"
}

object SelectAllClicks{
    const val SELECT_ALL_CLICK_BULK_CANCEL="Select All Clicks - BulkCancel"
}

object CancelFinalCtaClicks{
    const val CANCEL_FINAL_CTA_CLICKS_BULK_CANCEL="Cancel Final CTA Clicks - BulkCancel"
}

object ViewOptions{
    const val VIEW="view"
}


object MergeBus{
    const val MERGE_BUS_SELECT_DESTINATION_ID="mergeBusSelectedDestinationId"
    const val MERGE_BUS_SELECT_DESTINATION="mergeBusSelectedDestination"
    const val RIGHT_COACH_EXACT_ROUTE_SERVICE="rightCoachExactRouteService"
}


object PayGayTypes {
    const val PHONEPE_V2 = 60
    const val EASEBUZZ = 57
}

object PayGayTypesUser {
    const val PHONEPE_V2 = 11
    const val EASEBUZZ = 10
}

object PaymentTypes {
    const val QR = "QR"
    const val SMS = "SMS"
    const val VPA = "VPA"
    const val UPI_INTENT = "UPI INTENT"
    const val PHONEPE_V2 = "PHONEPE_V2"
}
