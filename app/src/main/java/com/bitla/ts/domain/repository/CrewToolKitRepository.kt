package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.crew_delete_image.request.CrewDeleteImageRequest
import com.bitla.ts.domain.pojo.crew_toolkit.request.CrewToolKitRequest
import com.bitla.ts.domain.pojo.crew_toolkit.request.ReqBody
import com.bitla.ts.domain.pojo.crew_update.request.UpdateCrewRequest
import com.bitla.ts.koin.models.makeApiCall
import okhttp3.MultipartBody
import okhttp3.RequestBody


class CrewToolKitRepository(private val apiInterface: ApiInterface) {

    suspend fun newFetchCrewCheckList(
        crewToolKitRequest: ReqBody
    ) = makeApiCall { apiInterface.newFetchCrewCheckList(crewToolKitRequest) }


    suspend fun newUpdateCrewCheckList(
        apiKey: String,
        locale : String,
        updateCrewRequest: com.bitla.ts.domain.pojo.crew_update.request.ReqBody
    ) = makeApiCall { apiInterface.newUpdateCrewCheckList(apiKey,locale,updateCrewRequest)}


    suspend fun newDeleteCrewImage(
        locale: String,
        deleteImageRequest: com.bitla.ts.domain.pojo.crew_delete_image.request.ReqBody
    ) = makeApiCall {  apiInterface.newDeleteCrewImage(locale,deleteImageRequest) }



    suspend fun newUploadCrewImage(
        apiKey: String,
        locale: String,
        format: RequestBody,
        resId: RequestBody,
        goodsId: RequestBody,
        goodsImageId: RequestBody,
        goodsImage: MultipartBody.Part,

    ) = makeApiCall { apiInterface.newUploadCrewImage( apiKey,locale,format,resId,goodsId,goodsImageId,goodsImage) }

}
