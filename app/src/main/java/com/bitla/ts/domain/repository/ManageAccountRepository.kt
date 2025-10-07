package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.request.ReqBody
import com.bitla.ts.domain.pojo.manage_account_view.update_account_status.request.UpdateAccountStatusRequest
import com.bitla.ts.koin.models.makeApiCall

class ManageAccountRepository(private val apiInterface: ApiInterface) {

    suspend fun getShowTransactionListApi(
        reqBody: ReqBody,
    ) = makeApiCall {
        apiInterface.showTransactionList(
            reqBody.apikey,
            reqBody.listType,
            reqBody.agentId,
            reqBody.branchId,
            reqBody.fromDate,
            reqBody.toDate,
            reqBody.category,
            reqBody.pageNo,
            reqBody.perPage,
            reqBody.pagination,
            reqBody.locale
        )
    }


    suspend fun getManageTransactionSearchApi(
        reqBody: com.bitla.ts.domain.pojo.manage_account_view.manage_transaction_search.request.ReqBody,
    ) = makeApiCall {
        apiInterface.manageTransactionSearch(
            reqBody.apikey,
            reqBody.listType,
            reqBody.search,
            reqBody.agentId,
            reqBody.branchId,
            reqBody.category,
            reqBody.fromDate,
            reqBody.toDate,
            reqBody.locale
        )
    }

    suspend fun getTransactionPdfUrlApi(
        reqBody: com.bitla.ts.domain.pojo.manage_account_view.get_transaction_pdf_url.request.ReqBody,
    ) = makeApiCall {
        apiInterface.getTransactionPdfUrlResponse(
            reqBody.apikey,
            reqBody.listType,
            reqBody.agentId,
            reqBody.branchId,
            reqBody.fromDate,
            reqBody.toDate,
            reqBody.category,
            reqBody.locale
        )
    }

    suspend fun transactionInfoApi(
        reqBody: com.bitla.ts.domain.pojo.manage_account_view.transaction_info.request.ReqBody,
    ) = makeApiCall {
        apiInterface.transactionInfo(
            reqBody.apikey,
            reqBody.transactionNo,
            reqBody.fromDate,
            reqBody.toDate,
            reqBody.locale
        )
    }

    suspend fun updateAccountStatus(
        reqBody: UpdateAccountStatusRequest
    ) = makeApiCall {  apiInterface.updateAccountStatus(reqBody)}
}
