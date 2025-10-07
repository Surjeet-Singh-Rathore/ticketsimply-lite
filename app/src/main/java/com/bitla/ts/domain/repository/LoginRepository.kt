package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.login_auth_post.request.LoginAuthPostRequest
import com.bitla.ts.domain.pojo.login_model.request.LoginRequest
import com.bitla.ts.domain.pojo.login_model.request.NewLoginRequest
import com.bitla.ts.domain.pojo.login_with_otp.request.LoginWithOtpRequest
import com.bitla.ts.domain.pojo.login_with_otp.request.ReqBody
import com.bitla.ts.domain.pojo.logout_auth_post_req.LogoutReqBody
import com.bitla.ts.koin.models.makeApiCall
import com.bitla.ts.utils.security.EncrypDecryp

class LoginRepository(private val apiInterface: ApiInterface) {
    suspend fun getNewLoginDetails(login : String,pass :String, locale:String?,deviceId: String,is_encrypted:Boolean, shiftId: Int? = null, counterId: Int?= null, counterBalance: String = "") = makeApiCall { apiInterface.newLoginApi(login,pass,locale,deviceId, shiftId, counterId, counterBalance)}
    suspend fun getNewLoginDetailsPost(loginAuthPostRequest: LoginAuthPostRequest) = makeApiCall {  apiInterface.newLoginApiPost(loginAuthPostRequest)}
    suspend fun getNewResetDetails(login: String,pass: String,deviceId: String, shiftId: Int? = null, counterId: Int?= null, counterBalance: Double? = null) = makeApiCall {  apiInterface.logoutApi(EncrypDecryp.getEncryptedValue(login),EncrypDecryp.getEncryptedValue(pass), EncrypDecryp.getEncryptedValue(deviceId),EncrypDecryp.isEncrypted(), shiftId, counterId, counterBalance) }
    suspend fun getNewResetPostApi(logoutReqBody: LogoutReqBody) = makeApiCall {
        apiInterface.logoutPostApi(logoutReqBody)
    }
    suspend fun newGetLoginWithOTPDetails(loginWithOtpRequest: ReqBody) = makeApiCall {  apiInterface.newLoginWithOTPApi(loginWithOtpRequest) }
}
