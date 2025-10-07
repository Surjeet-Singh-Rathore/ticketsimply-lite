package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.koin.models.makeApiCall
import com.google.gson.JsonObject

class RouteManagerRepository(private val apiInterface: ApiInterface) {

    suspend fun getCities(
        apiKey: String,
        responseFormat: String,
        locale: String
    ) = makeApiCall {  apiInterface.getCitiesList(apiKey,responseFormat,locale)}

    suspend fun getRouteList(
        originId : String,
        destinationId : String,
        apiKey: String,
        responseFormat: String,
        locale: String,
        filterValue: String,
        page: String,
        perPage: String,
        search: String,
        filterType : String
    ) = makeApiCall { apiInterface.getRouteList(originId,destinationId,apiKey,responseFormat,locale,filterValue,page,perPage,search,filterType)}


    suspend fun getHubDropDown(
        apiKey: String,
        locale: String,
        responseFormat: String
    ) = makeApiCall {  apiInterface.getHubDropDownList(apiKey,locale,responseFormat)}

    suspend fun getCoachType(
        apiKey: String,
        locale: String,
        responseFormat: String
    ) = makeApiCall {  apiInterface.getCoachTypeList(apiKey,locale,responseFormat)}

    suspend fun getCityPair(
        apiKey: String,
        responseFormat: String,
        locale: String,
        reqCitiesBody : Any

    ) = makeApiCall {  apiInterface.getCityPair(apiKey,responseFormat,locale,reqCitiesBody)}

    suspend fun getStage(
        cityId: String,
        apiKey: String,
        operatorApiKey: String,
        responseFormat: String,
        locale: String,
        routeId: String
    ) = makeApiCall {  apiInterface.getStageList(cityId,apiKey,operatorApiKey,responseFormat,locale,routeId)}

    suspend fun getRouteActivateDeactivateStatus(
        apiKey: String,
        locale: String,
        responseFormat: String,
        activateDeactivateRouteReqBody: Any
    ) = makeApiCall {  apiInterface.activateDeactivateRoute(apiKey,locale,responseFormat,activateDeactivateRouteReqBody)}


    suspend fun createRoute(
        apiKey: String,
        locale: String,
        responseFormat: String,
        createRouteRequestBody: Any
    ) = makeApiCall {  apiInterface.createRoute(apiKey,locale,responseFormat,createRouteRequestBody)}


    suspend fun updateRoute(
        apiKey: String,
        locale: String,
        responseFormat: String,
        routeId: String,
        updateRouteRequestBody: JsonObject?
    ) = makeApiCall {  apiInterface.updateRoute(apiKey,locale,responseFormat,routeId,updateRouteRequestBody)}


    suspend fun getRouteData(
        apiKey: String,
        locale: String,
        responseFormat: String,
        routeId: String

    ) = makeApiCall {   apiInterface.getRouteData(apiKey,locale,responseFormat,routeId)}


    suspend fun modifyRoute(
        apiKey: String,
        locale: String,
        responseFormat: String,
        routeId: String,
        step: String,
        modifyRouteRequestBody: Any

    ) = makeApiCall { apiInterface.modifyRoute(apiKey,locale,responseFormat,routeId,step,modifyRouteRequestBody)}


    suspend fun deleteStage(
        apiKey: String,
        locale: String,
        operatorApiKey: String,
        responseFormat: String,
        reqBody: Any

    ) = makeApiCall {  apiInterface.deleteStage(apiKey,locale,operatorApiKey,responseFormat,reqBody)}


    suspend fun duplicateService(
        apiKey: String,
        locale: String,
        operatorApiKey: String,
        responseFormat: String,
        routeId: String

    ) = makeApiCall {  apiInterface.duplicateService(apiKey,locale,operatorApiKey,responseFormat,routeId)}


    suspend fun previewRoute(
        apiKey: String,
        locale: String,
        routeId: String

    ) = makeApiCall {  apiInterface.previewRoute(apiKey,locale,routeId)}

    suspend fun createRouteStagesApi(
        apiKey: String,
        locale: String,
        responseFormat: String,
        routeId: String,
        reqBody: Any

    ) = makeApiCall {apiInterface.createStagesApi(apiKey,locale,responseFormat,routeId,reqBody)}

}