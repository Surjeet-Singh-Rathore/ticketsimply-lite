package com.bitla.ts.utils.app_data

import com.bitla.ts.domain.pojo.recent_search.RecentSearch

class AppData {
    companion object {
        var bgColor = 0
        var textColor = 0
        var phoneNumCount = 0
        var maxSeats = 0
        var operatorName = ""
        var subDomainName = ""
        var headOfficeNumber = ""
        var smallLogo = ""
        var bigLogo = ""
        var androidUrl = ""
        var iosUrl = ""
        var operatorEmail = ""
        var currencyType = ""
        var offerMessage = ""
        var enableOtpOnSignUp: Boolean = false
        var isBimaEnabled: Boolean = false
        var isWhatsappChatEnabled: Boolean = false
        var isRoundTrip: Boolean = false
        var isCheckForUpdate: Boolean = false
        var isWalletBooking: Boolean = false
        var sendBookingViaWhatsapp: Boolean = false
        var isPassportDetailEnabled: Boolean = false
        var chargesInclusive: Boolean = false
        var isPhoneBookingAllowed: Boolean = false
        var isGeneralLogin: Boolean = true
        var isRecentSearches: Boolean = true
        var authToken = ""
        var firstName = ""
        var lastName = ""
        var mobileNumber = ""
        var email = ""
        var gender = ""
        var isAllowCashCredit: Boolean = false
        var recentSearch = mutableListOf<RecentSearch>()
        var selectFirstOption: Boolean = false
        var trackingo_api_key = ""
        var trackingo_url = ""
        var customerHelplineNumber = ""
        var addMoney = false
        var loginType = ""
        var searchAutocomplete = false
        var sortWithCity = false
        var backgroundImage = ""
        var allowPackageRequest = false
        var nextDate = ""
        var nextDay = false
        var arrivalDate = ""
        var departureDate = ""
        var arrivalDateR = ""
        var departureDateR = ""
        var bpDate = ""
        var dpDate = ""
        var bpDateR = ""
        var dpDateR = ""
        var showContactUs: Boolean = false
        var advanceBookingDays: Int = 90
    }
}