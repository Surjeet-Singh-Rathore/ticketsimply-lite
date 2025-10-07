package com.bitla.ts.data.listener

import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response.FetchRouteWiseFareDetail
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.RouteWiseFareDetail
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultistationFareDetails

interface DialogSingleListButtonListener {
    fun onSingleButtonClickList(list: MutableList<MultistationFareDetails>? =null)
    fun onSingleButtonClickListFetchFareDetails(list: MutableList<FetchRouteWiseFareDetail>? =null)
    fun onSingleButtonClickListViewFareDetails(list: MutableList<RouteWiseFareDetail>? =null)
}
