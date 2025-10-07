package com.example.buscoach.base

import com.example.buscoach.service_details_response.ServiceDetailsModel

object BaseClass {
    private var serviceDetailsModelLeft = ServiceDetailsModel()
    private var serviceDetailsModelRight: ServiceDetailsModel? = null
    private var isAllSeatSelection = false
    private var isSeatClickable = true


    fun setLeftCoachResponse(
        response: ServiceDetailsModel)
    {
        serviceDetailsModelLeft = response
    }

    fun getLeftCoachResponse() : ServiceDetailsModel {
        return serviceDetailsModelLeft
    }
    fun setRightCoachResponse(
        response: ServiceDetailsModel)
    {
        serviceDetailsModelRight = response
    }

    fun getRightCoachResponse() : ServiceDetailsModel? {
        return serviceDetailsModelRight
    }

    fun selectAllSeats(selectAll : Boolean)
    {
        isAllSeatSelection = selectAll
    }

    fun getAllSeatSelection() : Boolean
    {
        return isAllSeatSelection
    }

    fun setSeatClickListener(isClick : Boolean)
    {
        isSeatClickable = isClick
    }

    fun getSeatClickListener() : Boolean
    {
        return isSeatClickable
    }



}