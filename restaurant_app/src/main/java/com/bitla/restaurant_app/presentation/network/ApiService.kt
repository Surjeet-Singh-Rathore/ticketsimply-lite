package com.bitla.restaurant_app.presentation.network

import com.bitla.restaurant_app.presentation.pojo.allotedServiceDirect.AllotedDirctResponse.AllotedDirectResponse
import com.bitla.restaurant_app.presentation.pojo.LoginModel
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.MealCouponDetailsResponse
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.ReqBody
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.MealCouponStatusResponse
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.RestaurantListResponse
import com.bitla.restaurant_app.presentation.pojo.reports.ReportsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("bus_operator_app/api/get_new_meal_coupon_details.json?is_from_middle_tier=true")
    suspend fun getMealCouponDetailsApi(
        @Query("api_key") apikey: String,
        @Query("qr_value") qrValue: String,
        @Query("coupon_code") couponCode: String,
    ): Response<MealCouponDetailsResponse>

    @POST("bus_operator_app/api/update_the_meal_coupon_status.json")
    suspend fun updateMealCouponStatus(@Body updateMealCouponStatusRequest: ReqBody): Response<MealCouponStatusResponse>

    @GET("bus_operator_app/api/restaurant_list?is_from_middle_tier=true")
    suspend fun getRestaurantListApi(
        @Query("api_key") apikey: String
    ): Response<RestaurantListResponse>


    @GET("bus_operator_app/api/alloted_services?is_from_middle_tier=true&operator_api_key=BITLA@123")
    @Headers("Content-Type: application/json")
    suspend fun allotedServiceDirectCall(
        @Query("is_group_by_hubs") is_group_by_hubs: Boolean,
        @Query("hub_id") hub_id: Int?,
        @Query("api_key") Apikey: String,
        @Query("travel_date") travel_date: String,
        @Query("page") page: Int? = null,
        @Query("per_page") per_page: Int? = null,
        @Query("view_mode") view_mode: String,
        @Query("pagination") pagination: Boolean,
        @Query("origin") origin: String?,
        @Query("destination") destination: String?,
        @Query("locale") locale: String?,
        @Query("is_checking_inspector") isCheckingInspector: Boolean?,
        @Query("service_filter") serviceFilter: String?,
        @Query("res_id") resvId: String?,
    ): Response<AllotedDirectResponse>


    @GET("api/restaurant_report.json?is_from_middle_tier=true")
    suspend fun getRestaurantReportApi(
        @Query("api_key") api_key: String,
        @Query("response_format") resp_format: String,
        @Query("is_pdf_download") is_pdf_download: Boolean,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String,
        @Query("locale") locale: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("pagination") pagination: Boolean,
        @Query("restaurant_id") restaurantId: String,
        @Query("route_id") routeId: String,
    ): Response<ReportsResponse>

    @GET("bus_operator_app/api/logout?is_from_middle_tier=true")
    suspend fun logoutApi(
        @Query("api_key") api_key: String,
        @Query("is_middle_tier") isMiddleTier: Boolean,
        @Query("device_id") deviceId: String
    ): Response<LoginModel>
}