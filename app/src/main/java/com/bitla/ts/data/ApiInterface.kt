package com.bitla.ts.data


import com.bitla.ts.domain.pojo.reports.ReportsResponse
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.activate_deactivate_route.*
import com.bitla.ts.domain.pojo.BpDpService.response.BpDpServiceResponse
import com.bitla.ts.domain.pojo.YourBus
import com.bitla.ts.domain.pojo.account_info.response.AgentAccountInfoRespnse
import com.bitla.ts.domain.pojo.activate_deactivate_service.request.ActivateDeactivateServiceRequest
import com.bitla.ts.domain.pojo.activate_deactivate_service.response.ActivateDeactivateServiceResponse
import com.bitla.ts.domain.pojo.active_inactive_services.response.ActiveInactiveServicesResponse
import com.bitla.ts.domain.pojo.add_bp_dp_to_service.request.AddBpDpToServiceRequest
import com.bitla.ts.domain.pojo.add_bp_dp_to_service.response.AddBpDpToServiceResponse
import com.bitla.ts.domain.pojo.add_driver.response.AddADHOCDriverResponse
import com.bitla.ts.domain.pojo.add_rate_card.createRateCard.request.CreateRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.createRateCard.response.CreateRateCardResponse
import com.bitla.ts.domain.pojo.add_rate_card.deleteRateCard.response.DeleteRateCardResponse
import com.bitla.ts.domain.pojo.add_rate_card.editRateCard.request.EditRateCardReqBody
import com.bitla.ts.domain.pojo.add_rate_card.editRateCard.response.EditRateCardResponse
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response.FetchRouteWiseFareResponse
import com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.response.FetchShowRateCardResponse
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.ViewRateCardResponse
import com.bitla.ts.domain.pojo.agent_recharge.AgentRechargeResponseModel
import com.bitla.ts.domain.pojo.agent_recharge.BranchRechargeResponseModel
import com.bitla.ts.domain.pojo.agent_recharge.request.*
import com.bitla.ts.domain.pojo.all_coach.response.*
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.*
import com.bitla.ts.domain.pojo.alloted_services.*
import com.bitla.ts.domain.pojo.alloted_services.request.*
import com.bitla.ts.domain.pojo.announcement_details_model.response.*
import com.bitla.ts.domain.pojo.announcement_model.response.*
import com.bitla.ts.domain.pojo.auto_shift.*
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.blacklist_number.*
import com.bitla.ts.domain.pojo.block_configuration_model.*
import com.bitla.ts.domain.pojo.block_seats.*
import com.bitla.ts.domain.pojo.block_seats.request.*
import com.bitla.ts.domain.pojo.block_unblock_reservation.*
import com.bitla.ts.domain.pojo.blocked_numbers_list.*
import com.bitla.ts.domain.pojo.book_extra_seat.*
import com.bitla.ts.domain.pojo.book_ticket.*
import com.bitla.ts.domain.pojo.book_ticket.release_ticket.request.*
import com.bitla.ts.domain.pojo.book_ticket.release_ticket.response.*
import com.bitla.ts.domain.pojo.book_ticket_full.*
import com.bitla.ts.domain.pojo.book_ticket_full.request.*
import com.bitla.ts.domain.pojo.book_ticket_full.request.ReqBody
import com.bitla.ts.domain.pojo.book_with_extra_seat.request.*
import com.bitla.ts.domain.pojo.book_with_extra_seat.response.*
import com.bitla.ts.domain.pojo.booking_history.response.*
import com.bitla.ts.domain.pojo.booking_summary.*
import com.bitla.ts.domain.pojo.booking_summary_details.*
import com.bitla.ts.domain.pojo.bp_dp_details.*
import com.bitla.ts.domain.pojo.branch_list_model.*
import com.bitla.ts.domain.pojo.bulkCancelOtpConfirmtion.request.*
import com.bitla.ts.domain.pojo.bulkCancelOtpConfirmtion.response.*
import com.bitla.ts.domain.pojo.bulk_cancellation.*
import com.bitla.ts.domain.pojo.bulk_ticket_update.response.*
import com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.response.*
import com.bitla.ts.domain.pojo.cancel_partial_ticket_model.response.*
import com.bitla.ts.domain.pojo.cancellation_details_model.response.*
import com.bitla.ts.domain.pojo.cancellation_policies_service_summary.response.*
import com.bitla.ts.domain.pojo.city_details.response.*
import com.bitla.ts.domain.pojo.city_pair.CityPairResponse
import com.bitla.ts.domain.pojo.city_pickup_by_chart_stage.response.*
import com.bitla.ts.domain.pojo.coach_list.CoachListResponse
import com.bitla.ts.domain.pojo.coach_type.CoachTypeListResponse
import com.bitla.ts.domain.pojo.collection_details.*
import com.bitla.ts.domain.pojo.collection_details.trip_collection.TripCollectionDetailsData
import com.bitla.ts.domain.pojo.collection_summary.CollectionSummary
import com.bitla.ts.domain.pojo.create_route.CreateRouteResponse
import com.bitla.ts.domain.pojo.create_stage_data.CreateStageResponse
import com.bitla.ts.domain.pojo.delete_stage.DeleteStageResponse
import com.bitla.ts.domain.pojo.duplicate_service.DuplicateServiceResponse
import com.bitla.ts.domain.pojo.get_route.GetRouteResponse
import com.bitla.ts.domain.pojo.hub_dropdown.HubDropdownResponse
import com.bitla.ts.domain.pojo.ivr_call.*
import com.bitla.ts.domain.pojo.login_auth_post.request.LoginAuthPostRequest
import com.bitla.ts.domain.pojo.logout_auth_post_req.FullLogoutReqBody
import com.bitla.ts.domain.pojo.logout_auth_post_req.LogoutReqBody
import com.bitla.ts.domain.pojo.modify_route.ModifyRouteResponse
import com.bitla.ts.domain.pojo.mealCoupon.*
import com.bitla.ts.domain.pojo.paytm_pos_integration.paytm_pos_txn_status_api.request.PaytmPosTxnStatusRequest
import com.bitla.ts.domain.pojo.preview_route.PreviewRouteResponse
import com.bitla.ts.domain.pojo.reservation_stages.response.ReservationStagesResponse
import com.bitla.ts.domain.pojo.route_list.RouteListResponse
import com.bitla.ts.domain.pojo.route_manager.CitiesListResponse
import com.bitla.ts.domain.pojo.self_audit_question.response.SelfAuditQuestionResponse
import com.bitla.ts.domain.pojo.service_occupancy_details_popup.*
import com.bitla.ts.domain.pojo.service_stages.*
import com.bitla.ts.domain.pojo.stage_for_city.StageListResponse
import com.bitla.ts.domain.pojo.stage_summary_details.*
import com.bitla.ts.domain.pojo.submit_self_audit_form.request.SubmitSelfAuditFormRequest
import com.bitla.ts.domain.pojo.submit_self_audit_form.response.SubmitSelfAuditFormResponse
import com.bitla.ts.domain.pojo.update_coach_type.*
import com.bitla.ts.domain.pojo.update_coach_type.request.UpdateCoachTypeRequest
import com.bitla.ts.domain.pojo.update_route.UpdateRouteResponse
import com.bitla.ts.domain.pojo.update_trip_status.UpdateTripResponse
import com.bitla.ts.domain.pojo.view_reservation.*
import com.bitla.ts.domain.pojo.your_bus_location.*
import com.google.gson.*
import com.bitla.ts.domain.pojo.confirm_otp_cancel_partial_ticket_model.response.ConfirmOtpCancelPartialTicketResponse
import com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.response.ConfirmOtpReleasePhoneBlockTicketResponse
import com.bitla.ts.domain.pojo.confirm_pay_at_bus.PayAtBusResponse
import com.bitla.ts.domain.pojo.confirm_reset_password.ConfirmResetPasswordModel
import com.bitla.ts.domain.pojo.coupon.CouponResponse
import com.bitla.ts.domain.pojo.crew_delete_image.CrewDeleteImage
import com.bitla.ts.domain.pojo.crew_toolkit.CrewToolKIt
import com.bitla.ts.domain.pojo.crew_update.UpdateCrew
import com.bitla.ts.domain.pojo.crew_upload_image.CrewUploadImage
import com.bitla.ts.domain.pojo.dashboard_fetch.response.DashboardFetchResponse
import com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBodyWithoutTicket
import com.bitla.ts.domain.pojo.dashboard_model.release_ticket.response.ReleaseTicketResponse
import com.bitla.ts.domain.pojo.dashboard_model.response.DashboardResponseModel
import com.bitla.ts.domain.pojo.destination_list.DestinationList
import com.bitla.ts.domain.pojo.destination_pair.DestinationPairModel
import com.bitla.ts.domain.pojo.drag_drop_remarks_update.response.DragDropRemarksUpdateResponse
import com.bitla.ts.domain.pojo.dynamic_domain.DynamicDomain
import com.bitla.ts.domain.pojo.edit_chart.EditChart
import com.bitla.ts.domain.pojo.employees_details.response.EmployeesDetailsResponse
import com.bitla.ts.domain.pojo.eta.Eta
import com.bitla.ts.domain.pojo.expenses_details.response.ExpensesDetailsResponse
import com.bitla.ts.domain.pojo.extend_fare.response.ExtendFareResponse
import com.bitla.ts.domain.pojo.ezetap.EzetapTransactionResponse
import com.bitla.ts.domain.pojo.ezetap.ReqBodyEzetapStatus
import com.bitla.ts.domain.pojo.fare_breakup.response.FareBreakupResponse
import com.bitla.ts.domain.pojo.fetch_notification.request.FetchNotificationModel
import com.bitla.ts.domain.pojo.frequent_traveller_model.response.FrequentTravellerDataResponse
import com.bitla.ts.domain.pojo.getCouponDiscount.GetCouponDiscountRequest
import com.bitla.ts.domain.pojo.getCouponDiscount.Response.GetCouponDetailResponse
import com.bitla.ts.domain.pojo.get_destination_list.response.GetDestinationListResponse
import com.bitla.ts.domain.pojo.instant_recharge.AgentPGDataResponse
import com.bitla.ts.domain.pojo.instant_recharge.GetAgentRechargeResponse
import com.bitla.ts.domain.pojo.location_logs.LocationLogs
import com.bitla.ts.domain.pojo.location_logs.request.LocationLogRequest
import com.bitla.ts.domain.pojo.lock_chart.response.LockChartResponse
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.luggage_details.response.FetchLuggageDetailsResponse
import com.bitla.ts.domain.pojo.luggage_details.response.LuggageOptionsDetailsResponse
import com.bitla.ts.domain.pojo.manage_account_view.get_transaction_pdf_url.response.GetTransactionPdfUrlResponse
import com.bitla.ts.domain.pojo.manage_account_view.manage_transaction_search.response.ManageTransactionSearchResponse
import com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.response.ShowTransactionListResponse
import com.bitla.ts.domain.pojo.manage_account_view.transaction_info.response.TransactionInformationResponse
import com.bitla.ts.domain.pojo.manage_account_view.update_account_status.request.UpdateAccountStatusRequest
import com.bitla.ts.domain.pojo.manage_account_view.update_account_status.response.UpdateAccountStatusResponse
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.request.MergeBusSeatMappingRequest
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.response.MergeBusSeatMappingResponse
import com.bitla.ts.domain.pojo.merge_bus_shift_passenger.request.MergeBusShiftPassengerRequest
import com.bitla.ts.domain.pojo.merge_bus_shift_passenger.response.MergeBusShiftPassengerResponse
import com.bitla.ts.domain.pojo.move_to_extra_seat.MoveToExtraSeat
import com.bitla.ts.domain.pojo.move_to_normal_seats.MoveToNormalSeatRequest
import com.bitla.ts.domain.pojo.multiple_shift_passenger.MultiShiftPassengerResponse
import com.bitla.ts.domain.pojo.multistation_data.MultistationRespBody
import com.bitla.ts.domain.pojo.my_bookings.response.MyBookings
import com.bitla.ts.domain.pojo.notificationDetails.GetNotificationDetails
import com.bitla.ts.domain.pojo.notificationDetails.request.NotificationDetailsRequest
import com.bitla.ts.domain.pojo.notify_passengers.NotifyPassengersModel
import com.bitla.ts.domain.pojo.occupancy_datewise.response.OccupancyDateWiseResponse
import com.bitla.ts.domain.pojo.passenger_history.PassengersHistory
import com.bitla.ts.domain.pojo.pay_pending_amount.PayPendingAmount
import com.bitla.ts.domain.pojo.pay_pending_amount.request.PayPendingAmountRequest
import com.bitla.ts.domain.pojo.phone_block_temp_to_permanent_data.response.PhoneBlockTempToPermanentResponse
import com.bitla.ts.domain.pojo.phonepe.PhonePeResponse
import com.bitla.ts.domain.pojo.phonepe.PhonePeStatusResponse
import com.bitla.ts.domain.pojo.phonepe_direct_upi_for_app.request.PhonePeDirectUPIForAppRequest
import com.bitla.ts.domain.pojo.phonepe_direct_upi_for_app.response.PhonePeDirectUPIForAppResponse
import com.bitla.ts.domain.pojo.phonepe_direct_upi_transaction_status.request.PhonePeDirectUPITransactionStatusRequest
import com.bitla.ts.domain.pojo.phonepe_direct_upi_transaction_status.response.PhonePeDirectUPITransactionStatusResponse
import com.bitla.ts.domain.pojo.phonepe_direct_validate_upi_id.response.PhonePeDirectValidateUpiIdResponse
import com.bitla.ts.domain.pojo.photo_block_tickets.response.ConfirmPhoneBlockTicketResponse
import com.bitla.ts.domain.pojo.pickUpVanChart.VanChartStatusChangeRequest
import com.bitla.ts.domain.pojo.pickup_chart_crew_details.response.PickupChartCrewDetailsResponse
import com.bitla.ts.domain.pojo.pickup_chart_pdf_url.PickUpChartPdfResponseModel
import com.bitla.ts.domain.pojo.pinelabs.PinelabTransactionResponse
import com.bitla.ts.domain.pojo.pinelabs.ReqBodyPinelab
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.QuickBookServiceDetailsResponse
import com.bitla.ts.domain.pojo.quota_blocking_tooltip_Info_model.response.QuotaBlockingTooltipInfoResponse
import com.bitla.ts.domain.pojo.rapid_booking.RapidBookingModel
import com.bitla.ts.domain.pojo.rapid_booking.request.RapidBookingRequest
import com.bitla.ts.domain.pojo.recommended_seats.response.RecommendedSeatsResponse
import com.bitla.ts.domain.pojo.redelcom.ReqBodyPrint
import com.bitla.ts.domain.pojo.redelcom.ResponseBodyPG
import com.bitla.ts.domain.pojo.release_partial_booked.ReleasePartialBookedTicket
import com.bitla.ts.domain.pojo.released_summary.ReleasedSummary
import com.bitla.ts.domain.pojo.reset_password_with_otp.ResetPasswordWithOtp
import com.bitla.ts.domain.pojo.rutDiscountDetails.response.RutDiscountResponse
import com.bitla.ts.domain.pojo.seat_types.SeatTypesResponse
import com.bitla.ts.domain.pojo.sendOtpAndQrCode.SendOtqAndQrCodeResponseModel
import com.bitla.ts.domain.pojo.send_sms_email.SendSmsEmailResponse
import com.bitla.ts.domain.pojo.service_allotment.response.ServiceAllotmentResponse
import com.bitla.ts.domain.pojo.service_details_response.ServiceDetailsModel
import com.bitla.ts.domain.pojo.service_routes_list.response.ServiceRoutesListResponse
import com.bitla.ts.domain.pojo.service_summary.ServiceSummaryModel
import com.bitla.ts.domain.pojo.shortRouteCityPair.ShortRouteCityPairApiResponse
import com.bitla.ts.domain.pojo.singleShiftPassenger.SingleShiftPassengerResponse
import com.bitla.ts.domain.pojo.single_block_unblock.SingleBlockUnblock
import com.bitla.ts.domain.pojo.smart_miles_otp.SmartMilesOtp
import com.bitla.ts.domain.pojo.sms_types.SmsTypesModel
import com.bitla.ts.domain.pojo.starred_reports.StarredReportsResponse
import com.bitla.ts.domain.pojo.state_details.response.StateDetailsResponseModel
import com.bitla.ts.domain.pojo.store_fcm.StoreFcmKey
import com.bitla.ts.domain.pojo.ticket_details.response.TicketDetailsModel
import com.bitla.ts.domain.pojo.ticket_details_menu.TicketDetailsMenu
import com.bitla.ts.domain.pojo.ticket_details_phase_3.response.TicketDetailsResponse
import com.bitla.ts.domain.pojo.trackingo_response.TrackingoResponse
import com.bitla.ts.domain.pojo.unblock_seat.UnBlockSeatModel
import com.bitla.ts.domain.pojo.update_boarded_status.response.UpdateBoardedStatusResponseModel
import com.bitla.ts.domain.pojo.update_expenses_details.response.UpdateExpensesDetailsResponse
import com.bitla.ts.domain.pojo.update_notification.UpdateNotificationModel
import com.bitla.ts.domain.pojo.update_notification.request.UpdateNotificationRequest
import com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.response.CreateFareTemplateResponse
import com.bitla.ts.domain.pojo.update_rate_card.fetch_fare_template.response.FetchFareTemplateResponse
import com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.response.ManageFareMultiStationResponse
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultiStationWiseFareResponse
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_commission.response.UpdateRateCardCommissionResponse
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.response.UpdateRateCardFareResponse
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.response.UpdateRateCardSeatWiseResponse
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.response.UpdateRateCardPerSeatResponse
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_time.response.UpdateRateCardTimeResponse
import com.bitla.ts.domain.pojo.upi_check_status.response.UpiTranxStatusResponse
import com.bitla.ts.domain.pojo.upi_create_qr.response.UPICreateQRCodeResponse
import com.bitla.ts.domain.pojo.user_list.UserListModel
import com.bitla.ts.domain.pojo.validate_otp_wallets.ValidateOtpWalletsModel
import com.bitla.ts.domain.pojo.viewSummary.ViewSummaryResonse
import com.bitla.ts.domain.pojo.wallet_otp_generation.WalletOtpGenerationModel
import com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response.OccupancyCalendarResponse
import com.bitla.ts.presentation.view.merge_bus.pojo.ShiftToServicesListResponse
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    // init domain middle tier
    @GET("api/operator_info.json?response_format=hash")
    suspend fun initDomain(): Response<DynamicDomain>

    @POST ("api/blacklist_number.json?response_format=true")
    suspend fun newBlackListNumberApi(
        @Query("api_key") apiKey: String,
        @Query("locale") locale:String,
        @Query("phone_numbers") phoneNumber: String,
        @Query("remarks") remarks: String,
        @Query("status") status: String
    ): Response<BlackListNumberResponse>

    @GET("api/fetch_blacklist_number.json?response_format=true")
    suspend fun getBlackListNumbersList(
        @Query("api_key") apiKey: String,
        @Query("locale") locale:String
    ):Response<BlockedNumbersListResponse>

    @GET("api/get_service_occupancy_details.json?")
    suspend fun getServiceWiseOccupancyList(

    )

    //login api without middle tier
    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/login_auth?is_from_middle_tier=true")
    suspend fun newLoginApi(
        @Query("login") login: String,
        @Query("password") password: String,
        @Query("locale") locale: String?,
        @Query("device_id") deviceId: String,
        @Query("shift_id") shift_id: Int?=null,
        @Query("counter_id") counter_id: Int?=null,
        @Query("counter_balance") counter_balance: String="",
    ): Response<LoginModel>

    //login api without middle tier
    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/login_auth?is_from_middle_tier=true")
    suspend fun newLoginApiPost(
        @Body loginAuthPostRequest: LoginAuthPostRequest,
    ): Response<LoginModel>


    @GET ("api/get_service_occupancy_details.json?")
    suspend fun newGetServiceOccupancyDetails(
        @Query("api_key") apiKey: String,
        @Query("route_id")routeId : String,
        @Query("from")fromDate: String,
        @Query("to")toDate : String


        ):Response<ServiceOccupancyDetails?>


    // logout
    @GET("bus_operator_app/api/logout?is_from_middle_tier=true")
    suspend fun logoutApi(
        @Query("login") login: String,
        @Query("password") password: String,
        @Query("device_id") deviceId: String,
        @Query("is_encrypted") is_encrypted: Boolean?=null,
        @Query("shift_id") shift_id: Int?=null,
        @Query("counter_id") counter_id: Int?=null,
        @Query("counter_balance") counter_balance: Double?=null,
    ): Response<LoginModel>


    @POST("bus_operator_app/api/logout?is_from_middle_tier=true")
    suspend fun logoutPostApi(
        @Body logoutPostReq:LogoutReqBody
    ): Response<LoginModel>



    @GET("bus_operator_app/api/logout?is_from_middle_tier=true")
    suspend fun newResetApi(
        @Query("login") login: String,
        @Query("password") password: String,
        @Query("device_id") deviceId: String,
        @Query("shift_id") shift_id: Int?=null,
        @Query("counter_id") counter_id: Int?=null,
        @Query("counter_balance") counter_balance: Double?=null,
    ): Response<LoginModel>

    @GET("bus_operator_app/api/logout?is_from_middle_tier=true")
    suspend fun newFullLogoutApi(
        @Query("api_key") api_key: String,
        @Query("is_middle_tier") isMiddleTier: Boolean,
        @Query("device_id") deviceId: String,
        @Query("close_counter") closeCounter: Boolean

    ): Response<LoginModel>



    @POST("bus_operator_app/api/logout?is_from_middle_tier=true")
    suspend fun newFullLogoutPostApi(
       @Body fullLogOutReqBody :  FullLogoutReqBody,
       @Query("close_counter") closeCounter: Boolean
    ): Response<LoginModel>


    @POST("bus_operator_app/api/confirm_login_with_otp")
    suspend fun newLoginWithOTPApi(@Body loginWithOtpRequest: com.bitla.ts.domain.pojo.login_with_otp.request.ReqBody): Response<LoginModel>

    // available routes
    @GET("api/available_routes/{origin_id}/{destination_id}/{travel_date}.json?is_from_middle_tier=true")
    suspend fun newAvailableRoutes(
        @Path("origin_id") origin_id: String,
        @Path("destination_id") destination_id: String,
        @Path("travel_date") travel_date: String,
        @Query("api_key") api_key: String,
        @Query("show_injourney_services") show_injourney_services: String,
        @Query("is_cs_shared") is_cs_shared: Boolean,
        @Query("operator_api_key") operator_api_key: String,
        @Query("response_format") response_format: String,
        @Query("show_only_available_services") show_only_available_services: String,
        @Query("locale") locale: String,
        @Query("app_bima_enabled") app_bima_enabled: Boolean
    ): Response<AvailableRoutesModel>

    @GET("api/service_routes_list/{origin_id}/{destination_id}/{travel_date}.json?is_from_middle_tier=true")
    suspend fun serviceRoutesList(
        @Path("origin_id") origin_id: String,
        @Path("destination_id") destination_id: String,
        @Path("travel_date") travel_date: String,
        @Query("api_key") api_key: String,
        @Query("show_injourney_services") show_injourney_services: String,
        @Query("is_cs_shared") is_cs_shared: Boolean,
        @Query("operator_api_key") operator_api_key: String,
        @Query("response_format") response_format: String,
        @Query("show_only_available_services") show_only_available_services: String,
        @Query("locale") locale: String,
    ): Response<ServiceRoutesListResponse>

    @GET("v1/api/available_routes/{origin_id}/{destination_id}/{travel_date}.json?is_from_middle_tier=true")
    suspend fun availableRoutesForAgent(
        @Path("origin_id") origin_id: String,
        @Path("destination_id") destination_id: String,
        @Path("travel_date") travel_date: String,
        @Query("api_key") api_key: String,
        @Query("show_injourney_services") show_injourney_services: String,
        @Query("is_cs_shared") is_cs_shared: Boolean,
        @Query("operator_api_key") operator_api_key: String,
        @Query("response_format") response_format: String,
        @Query("show_only_available_services") show_only_available_services: String,
        @Query("locale") locale: String,
        @Query("load_available_seats") loadAvailableSeats: String,
        @Query("pagination") pagination: String? = null,
        @Query("per_page") perPage: String? = null,
        @Query("page") page: String? = null,
    ): Response<AvailableRoutesModel>


    @GET("api/destination_pairs.json?is_from_middle_tier=true")
    suspend fun getNewDestinationPairs(
        @Query("api_key") api_key: String,
        @Query("operator_api_key") operatorKey: String,
        @Query("response_format") responseFormat: String,
        @Query("app_bima_enabled") appBimaEnable: Boolean,
        @Query("locale") locale: String
    ): Response<DestinationPairModel>

    //@GET("t_tickets/conpay/{pnr_num}?")
    @GET("tickets/do_conpay_for_agent")
    suspend fun getRazorPaySuccess(
        @Query("is_razorpay_from_mobile_app") isRazorpayPayment: Boolean,
        @Query("pnr_number") pnrNum: String,
        @Query("payment_id") paymentId: String,
    ): Response<String>

    @GET("/tickets/do_conpay_for_agent")
    suspend fun getEaseBuzzSuccess(
        @Query("is_easebuzz_from_mobile_app") isEaseBuzzPayment : Boolean,
        @Query("pnr_number") pnrNum: String,
        @Query("amount")amount:String,
        @Query("phone") phone: String,
        @Query("email") email: String,
    ):Response<AgentPGDataResponse>


    @GET("tickets/do_conpay_for_agent")
    suspend fun getRazorPayFailure(
        @Query("order_id") orderId: String,
        @Query("is_razorpay_from_mobile_app") isRazorpayPayment: Boolean,
        @Query("pnr_number") pnrNum: String,

    ): Response<String>

    @GET("v1/api/destination_list.json?is_from_middle_tier=true")
    suspend fun destinationListWithOrigin(
        @Query("api_key") api_key: String,
        @Query("origin_id") originId: String,
        @Query("reservation_id") reservationId: String?= null,
    ): Response<DestinationList>

    @GET("v1/api/get_bp_dp_details.json?is_from_middle_tier=true")
    suspend fun getBpDpDetails(
        @Query("api_key") api_key: String,
        @Query("origin_id") originId: String,
        @Query("destination_id") destinationId: String,
        @Query("reservation_id") reservationId: String? = null,
    ): Response<BpDpDetails>


    @GET("api/destination_pairs.json?is_from_middle_tier=true")
    suspend fun getShortRouteCityPairApi(
        @Query("reservation_id") reservationId: String,
    ): Response<DestinationPairModel>

    @GET("api/quickbookingservicedetails/{reservation_id}.json?")
    suspend fun getQuickBookServiceDetailsApi(
        @Path("reservation_id") reservationId: String,
        @Query("origin_id") origin_id: String,
        @Query("destination_id") destination_id: String,
        @Query("api_key") api_key: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("locale") locale: String,
    ): Response<QuickBookServiceDetailsResponse>

    @POST("api/book_ticket.json")
    suspend fun newBookTicketApi(
        @Body bookTicketRequest: com.bitla.ts.domain.pojo.book_ticket.request.ReqBody
    ): Response<BookTicketModel>


    @GET("api/ticket_details.json?is_from_middle_tier=true")
    suspend fun newTicketDetails(
        @Query("api_key") api_key: String,
        @Query("ticket_number") ticketNumber: String,
        @Query("json_format") jsonFormat: Boolean,
        @Query("is_from_qr_scan") isQrScan: Boolean,
        @Query("locale") locale: String,

        ): Response<TicketDetailsModel>

    @GET("/v1/api/ticket_details.json?is_from_middle_tier=true")
    suspend fun ticketDetailsPhase3(
        @Query("api_key") api_key: String,
        @Query("ticket_number") ticketNumber: String,
        @Query("json_format") jsonFormat: Boolean,
        @Query("is_from_qr_scan") isQrScan: Boolean,
        @Query("locale") locale: String,
        @Query("load_privs") loadPrivs: Boolean,
        @Query("menu_privilege") menuPrivilege: Boolean,
        @Query("is_encrypted") is_encrypted: Boolean?=null,
        ): Response<TicketDetailsResponse>

    @GET("/v1/api/ticket_details_action.json?is_from_middle_tier=true")
    suspend fun ticketDetailsMenus(
        @Query("api_key") api_key: String,
        @Query("ticket_number") ticketNumber: String,
        @Query("json_format") jsonFormat: Boolean,
        @Query("locale") locale: String
    ): Response<TicketDetailsMenu>



    @GET("bus_operator_app/api/block_configurations?is_from_middle_tier=true")
    suspend fun newBlockConfigApi(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
    ): Response<BlockConfigurationModel>


    @GET("bus_operator_app/api/users_list?is_from_middle_tier=true")
    suspend fun newUserListApi(
        @Query("api_key") apiKey: String,
        @Query("city_id") cityId: String,
        @Query("user_type") userType: String,
        @Query("branch_id") branchId: String,
        @Query("locale") locale: String,
    ): Response<UserListModel>

    // branch list

    @GET("bus_operator_app/api/branches_list?is_from_middle_tier=true")
    suspend fun newBranchListApi(
        @Query("api_key") apikey: String,
        @Query("locale") locale: String
    ): Response<BranchListModel>

    @GET("api2/get_bp_dp_service_details/{reservation_id}.json?is_from_middle_tier=true")
    suspend fun newGetLatLongApi(
        @Path("reservation_id") reservationId: String,
        @Query("api_key") apiKey: String
    ):Response<ServiceStageResponseModel>

    @GET("api/service_details/{reservation_id}.json?is_from_middle_tier=true&json_format=true")
    suspend fun newGetServiceDetails(
        @Path("reservation_id") reservationId: String,
        @Query("origin_id") origin_id: String,
        @Query("destination_id") destination_id: String,
        @Query("api_key") api_key: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("exclude_passenger_details") exclude_passenger_details: Boolean,
        @Query("locale") locale: String,
        @Query("app_bima_enabled") app_bima_enabled: Boolean,
        @Query("is_encrypted") is_encrypted: Boolean?=null
    ): Response<ServiceDetailsModel>
    @GET("api/service_details/{reservation_id}.json?is_from_middle_tier=true&json_format=true")
    suspend fun newGetServiceDetailMergeBus(
        @Path("reservation_id") reservationId: String,
        @Query("origin_id") origin_id: String,
        @Query("destination_id") destination_id: String,
        @Query("api_key") api_key: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("exclude_passenger_details") exclude_passenger_details: Boolean,
        @Query("locale") locale: String,
    ): Response<com.example.buscoach.service_details_response.ServiceDetailsModel>


    @GET("api/service_details_by_route_id/{route_id}.json?is_from_middle_tier=true&json_format=true&is_round_trip=false")
    suspend fun getServiceDetailsByRouteId(
        @Path("route_id") routeId: String,
        @Query("origin_id") origin_id: String,
        @Query("destination_id") destination_id: String,
        @Query("travel_date") travel_date: String,
        @Query("api_key") api_key: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("exclude_passenger_details") exclude_passenger_details: Boolean,
        @Query("locale") locale: String,
    ): Response<ServiceDetailsModel>


    // BpDp service details
    @GET("api/service_details/{reservation_id}.json?is_from_middle_tier=true&json_format=true")
    suspend fun newGetBpDpServiceDetails(
        @Path("reservation_id") reservationId: String,
        @Query("api_key") api_key: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("locale") locale: String,
        @Query("origin_id") origin_id: String,
        @Query("destination_id") destination_id: String,
        @Query("boarding_at") boardingAt: String,
        @Query("drop_off") dropOff: String
    ): Response<ServiceDetailsModel>


    @Headers("Content-Type: application/json")
    @POST("api/block_seat.json")
    suspend fun newBlockSeatApi(
        @Body blockSeatRequest: ReqBody__1
    ): Response<BlockSeatModel>


    @POST("api/unblock_seat.json")
    suspend fun unblockSeatApi(
        @Body unblockSeatRequest: com.bitla.ts.domain.pojo.unblock_seat.request.ReqBody
    ): Response<UnBlockSeatModel>

    @POST("api/ivr_call_to_passenger.json")
    suspend fun newIvrCallApi(
//        @Query("reservation_id") reservationId: String,
//        @Query("api_key") apiKey: String,
//        @Query("boarding_id") boardingId: String,
//        @Query("option") option: String
        @Body ivrCallRequest: IvrCallRequest
    ):Response<IvrCallResponse>

    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/reset_password_with_otp")
    suspend fun newResetPasswordWithOtpApi(
        @Body resetPasswordRequest: com.bitla.ts.domain.pojo.reset_password_with_otp.request.ReqBody
    ): Response<ResetPasswordWithOtp>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/confirm_reset_password_with_otp")
    suspend fun newConfirmResetPasswordApi(
        @Body confirmResetPasswordRequest: com.bitla.ts.domain.pojo.confirm_reset_password.request.ReqBody
    ): Response<ConfirmResetPasswordModel>

    // dashboard summary middle tier
    @Headers("Content-Type: application/json")

    @GET("api/dashboard_summary.json?is_from_middle_tier=true")
    suspend fun newDashboardSummaryFragment(
        @Query("api_key") api_key: String,
        @Query("json_format") json_format: String,
        @Query("locale") locale: String,
    ): Response<DashboardResponseModel>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/release_phone_block_tickets")
    suspend fun newGetReleaseTicketApi(
        @Body releaseTicketRequest: com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBody
    ): Response<ReleaseTicketResponse>

    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/release_bima_phone_block_tickets.json")
    suspend fun newGetReleaseBimaTicketApi(
        @Body releaseTicketRequest: com.bitla.ts.domain.pojo.dashboard_model.release_ticket.request.ReqBody
    ): Response<ReleaseTicketResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/release_phone_block_tickets")
    suspend fun newGetReleaseTicketApiWithoutTicket(
        @Body releaseTicketRequest: ReqBodyWithoutTicket
    ): Response<ReleaseTicketResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/api/release_blocked_seats_of_agent_ins_recharg.json")
    suspend fun newGetReleaseAgentRechargBlockedSeatsResponse(
        @Body releaseTicketRequest: ReleaseAgentRechargBlockedSeatsRequest
    ): Response<ReleaseAgentRechargBlockedSeatsResponse>

    @GET("bus_operator_app/api/privilege_details?is_from_middle_tier=true")
    suspend fun getNewPrevilegeDetailApi(
        @Query("api_key") api_key: String,
        @Query("response_format") resp_format: String,
        @Query("locale") locale: String,
        @Header("platform") platform: String = "Android"
    ): Response<PrivilegeResponseModel>


    @GET("api/cancellation_details.json")
    suspend fun newGetCancellationDetailsTicket(
        @Query("api_key") api_key: String,
        @Query("cancel_type") cancel_type: String,
        @Query("ticket_cancellation_percentage_p") icket_cancellation_percentage_p: String,
        @Query("is_from_bus_opt_app") is_from_bus_opt_app: String,
        @Query("is_from_middle_tier") is_from_middle_tier: String,
        @Query("json_format") json_format: String,
        @Query("locale") locale: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("passenger_details") passenger_details: String,
        @Query("pnr_number") pnr_number: String,
        @Query("response_format") response_format: String,
        @Query("seat_numbers") seat_numbers: String,
        @Query("zero_percent") zero_percent: String,
        @Query("is_bima_ticket") isBimaTicket: Boolean,
    ): Response<CancellationDetailsResponse>


    @GET("api/cancellation_details.json")
    suspend fun newGetZeroCancellationDetailsTicket(
        @Query("api_key") api_key: String,
        @Query("cancel_type") cancel_type: String,
        @Query("is_from_bus_opt_app") is_from_bus_opt_app: String,
        @Query("is_from_middle_tier") is_from_middle_tier: String,
        @Query("json_format") json_format: String,
        @Query("locale") locale: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("passenger_details") passenger_details: String,
        @Query("pnr_number") pnr_number: String,
        @Query("response_format") response_format: String,
        @Query("seat_numbers") seat_numbers: String,
        @Query("zero_percent") zero_percent: String,
        @Query("is_bima_ticket") isBimaTicket: Boolean
        ): Response<CancellationDetailsResponse>


    @GET("api/cancel_partial_ticket.json?is_from_middle_tier=true")
    suspend fun newGetCancelPartialTicket(
        @Query("api_key") apikey: String,
        @Query("cancel_type") cancel_type: String,
        @Query("is_from_bus_opt_app") is_from_bus_opt_app: String,
        @Query("is_onbehalf_booked_user") is_onbehalf_booked_user: String,
        @Query("json_format") json_format: String,
        @Query("locale") locale: String,
        @Query("onbehalf_online_agent_flag") onbehalf_online_agent_flag: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("passenger_details") passenger_details: String,
        @Query("response_format") response_format: String,
        @Query("seat_numbers") seat_numbers: String,
        @Query("ticket_cancellation_percentage_p") ticket_cancellation_percentage_p: String,
        @Query("ticket_number") ticket_number: String,
        @Query("travel_date") travel_date: String,
        @Query("zero_percent") zero_percent: String,
        @Query("is_sms_send") is_sms_send: Boolean,
        @Query("is_bima_ticket") isBimaTicket: Boolean,
        @Query("auth_pin") auth_pin: String,
        @Query("remarks") remarks: String
    ): Response<CancelPartialTicketResponse>


    // new Confirm Otp Cancel Partial Ticket
    @GET("api/confirm_otp_cancel_partial_ticket.json?is_from_middle_tier=true")
    suspend fun newGetConfirmOtpCancelPartialTicket(
        @Query("api_key") apikey: String,
        @Query("cancel_type") cancel_type: String,
        @Query("is_from_bus_opt_app") is_from_bus_opt_app: String,
        @Query("is_onbehalf_booked_user") is_onbehalf_booked_user: String,
        @Query("json_format") json_format: String,
        @Query("locale") locale: String,
        @Query("onbehalf_online_agent_flag") onbehalf_online_agent_flag: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("passenger_details") passenger_details: String,
        @Query("response_format") response_format: String,
        @Query("seat_numbers") seat_numbers: String,
        @Query("ticket_cancellation_percentage_p") ticket_cancellation_percentage_p: String,
        @Query("ticket_number") ticket_number: String,
        @Query("travel_date") travel_date: String,
        @Query("zero_percent") zero_percent: String,
        @Query("is_bima_ticket") isBimaTicket: Boolean,
        @Query("otp") otp: String,
        @Query("key") key: String
    ): Response<ConfirmOtpCancelPartialTicketResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/confirm_otp_release_phone_block_tickets")
    suspend fun newGetConfirmOtpReleasePhoneBlockTicketRequest(
        @Body confirmOtpReleasePhoneBlockTicketRequest: com.bitla.ts.domain.pojo.confirm_otp_release_phone_block_tickets_model.request.ReqBody
    ): Response<ConfirmOtpReleasePhoneBlockTicketResponse>

    @POST("bus_operator_app/api/bulk_update_api")
    suspend fun newGetBulkTicketUpdate(
        @Body bulkTicketUpdateRequestModel: com.bitla.ts.domain.pojo.bulk_ticket_update.request.ReqBody
    ): Response<BulkTicketUpdateResponseModel>

    @GET("bus_operator_app/api/get_sms_types?is_from_middle_tier=true")
    suspend fun newSmsTypesApi(
        @Query("api_key") apikey: String,
        @Query("res_id") res_id: String,
        @Query("locale") locale: String,
        @Query("response_format") response_format: String,
    ): Response<SmsTypesModel>



    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/notify_passengers")
    suspend fun newNotifyPassengersApi(
        @Body notifyPassengersRequest: com.bitla.ts.domain.pojo.notify_passengers.request.ReqBody
    ): Response<NotifyPassengersModel>

    @POST("api/rapid_booking_seats_data.json")
    suspend fun newRapidBookingApi(
        @Body reqBody: Any
    ): Response<RapidBookingModel>


    @GET("api/passenger_history.json?is_from_middle_tier=true&json_format=true")
    suspend fun newPassengerHistory(
        @Query("api_key") api_key: String,
        @Query("passenger_details") passenger_details: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("locale") locale: String,
    ): Response<PassengersHistory>


    // tickets booked by you
    @GET("api/tickets_booked_by_you.json?is_from_middle_tier=true")
    suspend fun myNewBookingsApi(
        @Query("api_key") api_key: String,
        @Query("response_format") resp_format: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String,
        @Query("date_type") date_type: Int,
        @Query("locale") locale: String,
    ): Response<MyBookings>

    @GET("api/booking_summary_at_reservation.json?is_from_middle_tier=true")
    suspend fun newBookingSummaryApi(
        @Query("api_key") apikey: String,
        @Query("reservation_id") reservationId: String,
        @Query("response_format") responseFormat: String,
        @Query("locale") locale: String,
    ): Response<BookingSummary>

    @GET("api/service_summary.json?is_from_middle_tier=true")
    suspend fun newServiceSummaryApi(
        @Query("api_key") apikey: String,
        @Query("locale") locale: String,
        @Query("reservation_id") reservationId: String,
        @Query("response_format") reservationFormat: Boolean,
    ): Response<ServiceSummaryModel>


    @GET("bus_operator_app/api/collection_summary?is_from_middle_tier=true")
    suspend fun newCollectionSummaryApi(
        @Query("api_key") apikey: String,
        @Query("reservation_id") reservationId: String,
        @Query("response_format") responseFormat: String,
        @Query("locale") locale: String,
    ): Response<CollectionSummary>

    @GET("bus_operator_app/api/released_summary?is_from_middle_tier=true")
    suspend fun newReleasedSummaryApi(
        @Query("api_key") apikey: String,
        @Query("reservation_id") reservationId: String,
        @Query("response_format") responseFormat: String,
        @Query("locale") locale: String,
    ): Response<ReleasedSummary>

    // ALLOTED_SERVICES
    @Headers("Content-Type: application/json")
    @POST("ts/api/operator_response.json")
    suspend fun getAllotedServicies(
        @Header("Authorization") Authorization: String,
        @Header("Apikey") Apikey: String,
        @Body allotedServiceRequest: AllotedServiceRequest
    ): Response<AllotedServicesResponseModel>



    @GET("api/cities.json?is_from_middle_tier=true")
    suspend fun newGetCityList(
        @Query("api_key") apikey: String,
        @Query("response_format") response_format: String,
        @Query("locale") locale: String,
    ): Response<CityDetailsResponseModel>


    @GET("api/states.json?is_from_middle_tier=true")
    suspend fun newGetStateList(
        @Query("api_key") apikey: String,
        @Query("response_format") responseFormat: String,
        @Query("locale") locale: String,
    ): Response<StateDetailsResponseModel>


    @Headers("Content-Type: application/json")
    @POST("api/block_unblock_reservation.json")
    suspend fun newBlockUnblockReservation(
        @Body blockUnblockRequest: com.bitla.ts.domain.pojo.block_unblock_reservation.request.ReqBody
    ): Response<BlockUnblockReservation>


    @POST("api/update_extend_fare_settings.json")
    suspend fun newExtendFare(
        @Body extendFareRequest: com.bitla.ts.domain.pojo.extend_fare.request.RequestBody
    ): Response<ExtendFareResponse>


    @Headers("Content-Type: application/json")
    @POST("api/validate_coupons.json")
    suspend fun newValidateCoupons(
        @Body couponRequest: com.bitla.ts.domain.pojo.coupon.request.ReqBody
    ): Response<CouponResponse>


    @Headers("Content-Type: application/json")
    @POST("api/send_otp_for_smart_miles.json")
    suspend fun newSmartMilesOtp(
        @Body smartMilesOtpRequest: com.bitla.ts.domain.pojo.smart_miles_otp.request.ReqBody
    ): Response<SmartMilesOtp>


    @GET("bus_operator_app/api/get_pickup_chart?is_from_middle_tier=true")
    suspend fun newGetViewReservation(
        @Query("api_key") apikey: String,
        @Query("res_id") res_id: String,
        @Query("chart_type") chart_type: String,
        @Query("locale") locale: String,
        @Query("is_new_pickup_chart") is_new_pickup_chart: Boolean?,
        @Query("is_encrypted") is_encrypted: Boolean?
    ): Response<ViewReservationResponseModel>

    //@GET("")
   // @GET("api/get_stage_summary?api_key=8O24ZC18R0JIB1S66P2NKVF31OR5TYQY&res_id=201364")
    @GET("api/reservation_booking_summary.json?is_from_middle_tier=true")
     suspend fun newGetBookingSummary(
        @Query("api_key") apikey: String,
        @Query("res_id") res_id: String,
    ): Response<BookingSummaryResponse>
    ///api/get_stage_summary.json?is_from_middle_tier=true&api_key=8O24ZC18R0JIB1S66P2NKVF31OR5TYQY&res_id=201364

    //https://api.jsonbin.io/v3/qs/65c3257f1f5677401f2bf661
    @GET("api/get_stage_summary.json?is_from_middle_tier=true")
    suspend fun newGetStagingSummary(
        @Query("api_key") apikey: String,
        @Query("res_id") res_id: String,
    ): Response<StageSummaryModel>

    @GET("bus_operator_app/api/get_pickup_chart?is_from_middle_tier=true")
    suspend fun newGetViewReservationForCheckingInspector(
        @Query("api_key") apikey: String,
        @Query("res_id") res_id: String,
        @Query("city_id") city_id: Int,
        @Query("chart_type") chart_type: String,
        @Query("locale") locale: String,
    ): Response<com.bitla.ts.domain.pojo.view_reservation.ViewReservationResponseModel>


    @POST("bus_operator_app/api/update_boarded_status")
    suspend fun newUpdateBoardedStatus(
        @Body updateBoardedStatusRequest: com.bitla.ts.domain.pojo.update_boarded_status.ReqBody
    ): Response<UpdateBoardedStatusResponseModel>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/update_boarded_status")
    suspend fun newUpdateBoardedStatusCargo(
        @Body updateBoardedStartusCargo: com.bitla.ts.domain.pojo.update_boarded_status.request.ReqBody
    ): Response<com.bitla.ts.domain.pojo.update_boarded_status.request.response_cargo.UpdateBoardedStatusResponseModel>


    @GET("bus_operator_app/api/get_pickup_chart_pdf_url.json?is_from_middle_tier=true")
    suspend fun newGetPickupChartPDF(
        @Query("api_key") apikey: String,
        @Query("res_id") res_id: String,
        @Query("travel_date") travel_date: String,
        @Query("locale") locale: String,
        @Query("is_encrypted") is_encrypted: Boolean?,
        @Query("audit_type") audit_type: String
    ): Response<PickUpChartPdfResponseModel>


    @Headers("Content-Type: application/json")
    @POST("api/bulk_ticket_cancellation.json")
    suspend fun newBulkCancelation(
        @Body bulkCancellationRequest: com.bitla.ts.domain.pojo.bulk_cancellation.request.ReqBody
    ): Response<BulkCancellationResponseModel>


    @Headers("Content-Type: application/json")
    @POST("api/bulk_ticket_cancellation_confirmation.json")
    suspend fun confirmOtpBulkCancellation(
        @Body bulkCancellationVerificationRequest: BulkCancelVerificationRequest
    ): Response<BulkCancelOtpVerificationResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/resend_otp_and_qr_code")
    suspend fun newResendOtpAndrQrCode(
        @Body sendOtpAndQrCodeRequest: com.bitla.ts.domain.pojo.sendOtpAndQrCode.request.ReqBody
    ): Response<SendOtqAndQrCodeResponseModel>


    @POST("api/edit_chart_for_empty_seats.json")
    suspend fun newEditChart(
        @Body editChartRequest: com.bitla.ts.domain.pojo.edit_chart.request.ReqBody
    ): Response<EditChart>


    @POST("api/adhoc_driver.json")
    suspend fun newAddADHOCDriverService(
        @Body addADHOCDriverRequest: com.bitla.ts.domain.pojo.add_driver.request.ReqBody
    ): Response<AddADHOCDriverResponse>


    @GET("bus_operator_app/api/get_employee_details.json?is_from_middle_tier=true")
    suspend fun newGetEmployeesDetails(
        @Query("api_key") apikey: String,
        @Query("locale") locale: String,
    ): Response<EmployeesDetailsResponse>


    @GET("api/expenses_details.json?is_from_middle_tier=true")
    suspend fun newGetExpensesDetails(
        @Query("api_key") apikey: String,
        @Query("reservation_id") reservationId: String,
        @Query("locale") locale: String,
        @Query("response_format") respFormat: String,

        ): Response<ExpensesDetailsResponse>


    @POST("api/update_expenses_details.json")
    suspend fun newUpdateExpensesDetails(
        @Body udateExpensesDetailsRequest: com.bitla.ts.domain.pojo.update_expenses_details.request.ReqBody
    ): Response<UpdateExpensesDetailsResponse>


    @POST("/bus_operator_app/api/service_allotment/{id}.json?is_from_middle_tier=true")
    suspend fun newUpdateServiceAllotment(
        @Path("id") id: Long,
        @Body reqBody: com.bitla.ts.domain.pojo.service_allotment.request.ReqBody
    ): Response<ServiceAllotmentResponse>



    @GET("bus_operator_app/api/get_all_coaches?is_from_middle_tier=true")
    suspend fun newGetAllCoaches(
        @Query("api_key") apikey: String,
        @Query("res_id") resId: String,
        @Query("locale") locale: String
    ): Response<AllCoachResponse>


    @Headers("Content-Type: application/json")
    @POST("api/fare_break_up_internal.json")
    suspend fun newFareBreakup(
        @Body reqBody: com.bitla.ts.domain.pojo.fare_breakup.request.ReqBody
    ): Response<FareBreakupResponse>


    // book ticket full
    @Headers("Content-Type: application/json")
    @POST("api/book_ticket.json")
    suspend fun newBookTicketMainApi(
        @Body reqBody: ReqBody
    ): Response<BookTicketFullResponse>

    // book ticket insurance
    @Headers("Content-Type: application/json")
    @POST("api/book_ticket.json")
    suspend fun bookTicketInsuranceApi(
        @Body reqBody: ReqBodyWithInsurance
    ): Response<BookTicketFullResponse>

    @Headers("Content-Type: application/json")
    @POST("api/book_ticket.json")
    suspend fun bookTicketRapidBookingApi(
        @Body reqBody: RapidBookingRequest
    ): Response<BookTicketFullResponse>

    // show booking history
    @GET("api/show_history.json?is_from_middle_tier=true")
    suspend fun newShowBookingHistory(
        @Query("api_key") api_key: String,
        @Query("pnr_number") pnr_number: String,
        @Query("response_format") response_format: String,
        @Query("locale") locale: String,
    ): Response<ShowBookingHistoryResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/confirm_phone_block_tickets")
    suspend fun newConfirmPhoneBlockTicketApi(
        @Body confirmPhoneBlockTicketReq: com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody
    ): Response<ConfirmPhoneBlockTicketResponse>

    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/confirm_bima_phone_block_tickets")
    suspend fun newConfirmBimaPhoneBlockTicketApi(
        @Body confirmPhoneBlockTicketReq: com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody
    ): Response<ConfirmPhoneBlockTicketResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/manual_recharge_for_branch")
    suspend fun newBranchRechargeApi(
        @Body branchRechargeRequest: com.bitla.ts.domain.pojo.agent_recharge.request.ReqBody
    ): Response<BranchRechargeResponseModel>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/confirm_manual_recharge_for_branch")
    suspend fun newConfirmBranchRechargeApi(
        @Body branchRechargeRequest: ConfirmAgentRequestBody
    ): Response<BranchRechargeResponseModel>


    @Headers("Content-Type: application/json")
    @POST("api/credit_amount_for_agent.json")
    suspend fun agentRechargeApi(
        @Body agentRechargeRequest: AgentReqBody
    ): Response<AgentRechargeResponseModel>


    @Headers("Content-Type: application/json")
    @POST("api/confirm_credit_amount_for_agent.json")
    suspend fun newConfirmAgentRechargeApi(
        @Body confirmAgentRechargeRequest: ConfirmAgentRequestBody
    ): Response<AgentRechargeResponseModel>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/manage_fares")
    suspend fun newUpdateRateCardFareApi(
        @Body updateRateCardFareRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBody
    ): Response<UpdateRateCardFareResponse>

    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/manage_fares")
    suspend fun newUpdateRateCardFareApiX(
        @Body updateRateCardFareRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBodyNew
    ): Response<UpdateRateCardFareResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/manage_fares")
    suspend fun newUpdateRateCardTimeApi(
        @Body updateRateCardTimeRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_time.request.ReqBody
    ): Response<UpdateRateCardTimeResponse>


    @GET("bus_operator_app/api/announcement?is_from_middle_tier=true")
    suspend fun newAnnouncementRequest(
        @Query("api_key") apikey: String,
        @Query("locale") locale: String,
        @Query("reservation_id") reservation_id: String
    ): Response<AnnouncementApiResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/announcement_details")
    suspend fun newAnnouncementDetailsRequest(
        @Body announcementApiRequest: com.bitla.ts.domain.pojo.announcement_details_model.request.ReqBody
    ): Response<AnnoucementDetailsResponse>


    @POST("bus_operator_app/api/manage_fares")
    suspend fun newUpdateRateCardCommissionApi(
        @Body updateRateCardCommissionRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_commission.request.ReqBody
    ): Response<UpdateRateCardCommissionResponse>


    @GET("api/multistation_wise_fare.json?is_from_middle_tier=true")
    suspend fun newFetchMultiStatioWiseFareApi(
        @Query("api_key") apikey: String,
        @Query("date") date: String,
        @Query("reservation_id") reservation_id: String,
        @Query("channel_id") channelId: String,
        @Query("template_id") templateId: String,
        @Query("locale") locale: String,
    ): Response<MultiStationWiseFareResponse>

    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/create_fare_template")
    suspend fun createFareTemplateApi(
        @Body createFareTemplateRequest: com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.request.ReqBody
    ): Response<CreateFareTemplateResponse>

    @GET("api/fetch_fare_template_details.json?is_from_middle_tier=true")
    suspend fun fetchFareTemplateApi(
        @Query("api_key") apikey: String,
        @Query("template_id") templateId: String,
        @Query("locale") locale: String,
    ): Response<FetchFareTemplateResponse>



    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/manage_fares")
    suspend fun newManageMultiStatioWiseFareApi(
        @Body manageFareMultiStationRequest: com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request.ReqBody
    ): Response<ManageFareMultiStationResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/manage_fares")
    suspend fun newUpdateRateCardSeatWiseApi(
        @Body updateRateCardSeatWiseRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.request.ReqBody
    ): Response<UpdateRateCardSeatWiseResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/manage_fares")
    suspend fun newUpdateRateCardSeatWisePerSeatApi(
        @Body updateRateCardPerSeatRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request.ReqBody
    ): Response<UpdateRateCardPerSeatResponse>


    @Headers("Content-Type: application/json")
    @POST("api/book_extra_seats.json")
    suspend fun newBookExtraSeatApi(
        @Body bookExtraSeatRequest: com.bitla.ts.domain.pojo.book_extra_seat.request.ReqBody
    ): Response<BookExtraSeatResponse>


    @Headers("Content-Type: application/json")
    @POST("api/book_ticket.json")
    suspend fun newBookWithExtraSeatApi(

        @Query("api_key") apiKey: String,
        @Query("boarding_at") boardingAt: String,
        @Query("destination_id") destinationId: String,
        @Query("drop_off") dropOff: String,
        @Query("no_of_seats") noOfSeats: String,
        @Query("origin_id") originId: String,
        @Query("reservation_id") reservationId: String,
        @Query("locale") locale: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("is_from_bus_opt_app") is_from_bus_opt_app: String,
        @Body bookExtraSeatRequest: BookTicketWithExtraSeatRequest


    ): Response<BookSeatWithExtraSeatResponse>


    @POST("bus_operator_app/api/shif_passenger.json")
    suspend fun newSingleShiftPassengerApi(
        @Body singleShiftPassengerRequest: com.bitla.ts.domain.pojo.singleShiftPassenger.request.ReqBody
    ): Response<SingleShiftPassengerResponse>


    @Headers("Content-Type: application/json")
    @POST("api/shift_multiple_passengers.json")
    suspend fun newMultipleShiftPassengerApi(
        @Body multiShiftPassengerRequest: com.bitla.ts.domain.pojo.multiple_shift_passenger.request.ReqBody
    ): Response<MultiShiftPassengerResponse>


    @GET("api/auto_shift_seat_resp.json?is_from_middle_tier=true")
    suspend fun newAutoShiftApi(
        @Query("api_key") apikey: String,
        @Query("auto_macth_by") auto_macth_by: String,
        @Query("locale") locale: String,
        @Query("new_res_id") new_res_id: String,
        @Query("old_res_id") old_res_id: String,
    ): Response<AutoShiftResponse>


    @GET("bus_operator_app/api/get_collection_details?is_from_middle_tier=true")
    suspend fun newCollectionDetailApi(
        @Query("api_key") apikey: String,
        @Query("reservation_id") reservation_id: String,
        @Query("locale") locale: String,
    ): Response<CollectionDetailsResponse>



    @GET("bus_operator_app/api/get_trip_collection_details?is_from_middle_tier=true")
    suspend fun getTripCollectionDetailsApi(
        @Query("api_key") apikey: String,
        @Query("reservation_id") reservation_id: String,
        @Query("locale") locale: String,
    ): Response<TripCollectionDetailsData>

    @GET("api/eta_details.json?is_from_middle_tier=true")
    suspend fun newEtaApi(
        @Query("api_key") apikey: String,
        @Query("locale") locale: String,
        @Query("route_id") route_id: String,
        @Query("travel_date") travel_date: String,
    ): Response<Eta>


    @GET("api/fetch_starred_reports.json?is_from_middle_tier=true")
    suspend fun newStarredReport(
        @Query("api_key") api_key: String,
        @Query("recent_data") recentData: Boolean,
        @Query("locale") locale: String,
    ): Response<StarredReportsResponse>


    @Headers("Content-Type: application/json")
    @POST("api/move_to_extra_seat.json")
    suspend fun newMoveToExtraSeatApi(
        @Body moveToExtraSeatRequest: com.bitla.ts.domain.pojo.move_to_extra_seat.request.ReqBody
    ): Response<MoveToExtraSeat>



    @GET("api/block_shift_seat.json")
    suspend fun moveQuotaBlockSeatApi(
        @Query("blocking_number") blockingNumber: String,
        @Query("old_seat_number") oldSeatNumber:String,
        @Query("new_seat_number") newSeatNumber:String,
        @Query("api_key") apiKey:String,
    ): Response<MoveToExtraSeat>


    @Headers("Content-Type: application/json")
    @POST("api/move_extra_seat_to_normal.json")
    suspend fun newMoveToNormalSeatApi(
        @Body moveToExtraSeatRequest: MoveToNormalSeatRequest
    ): Response<MoveToExtraSeat>


    @POST("api/send_sms.json")
    suspend fun newSendSMSEmailApi(
        @Body sendSMSEmailRequest: com.bitla.ts.domain.pojo.send_sms_email.request.ReqBody
    ): Response<SendSmsEmailResponse>

    @Headers("Content-Type: application/json")
    @POST("api/lock_chart.json")
    suspend fun newLockChartApi(
        @Body lockChartRequest: com.bitla.ts.domain.pojo.lock_chart.ReqBody
    ): Response<LockChartResponse>


    @Headers("Content-Type: application/json")
    @POST("api/send_wallet_otp_to_customer.json?")
    suspend fun newWalletOtpGenerationApi(
        @Body req_body: com.bitla.ts.domain.pojo.wallet_otp_generation.request.ReqBody
    ): Response<WalletOtpGenerationModel>


    @Headers("Content-Type: application/json")
    @POST("pay/upi_payment_api")
    suspend fun newUpiCreateQrCodeApi(
        @Body reqBody: com.bitla.ts.domain.pojo.upi_create_qr.request.ReqBody
    ): Response<UPICreateQRCodeResponse>

    @Headers("Content-Type: application/json")
    @POST("api/upi_tranx_status.json")
    suspend fun newUpiTranxStatus(
        @Body reqBody: com.bitla.ts.domain.pojo.upi_check_status.request.ReqBody
    ): Response<UpiTranxStatusResponse>

    @GET("v1/api/get_pay_stat_of_agent_ins_recharg.json?is_from_middle_tier=true")
    suspend fun newGetAgentUpiTranxStatusApi(
        @Query("api_key") apikey: String,
        @Query("pnr_number") pnrNumber: String,
//        @Query("amount") amount: String,
        @Query("phone") phone: String,
        @Query("is_from_agent_recharge") isFromAgentRecharge: String
    ): Response<UpiTranxStatusResponse>

    @GET("v1/api/get_status_branch_upi_easebuzz.json?is_from_middle_tier=true")
    suspend fun getBranchUpiTranxStatusApi(
        @Query("api_key") apikey: String,
        @Query("pnr_number") pnrNumber: String,
        @Query("branch_phone") branchPhone: String
    ): Response<UpiTranxStatusResponse>

    @POST("v1/api/release_blocked_seats_for_branch_upi?.json")
    suspend fun releaseBranchUpiBlockedSeatsApi(
        @Body releaseTicketRequest: ReleaseAgentRechargBlockedSeatsRequest
    ): Response<ReleaseAgentRechargBlockedSeatsResponse>

    @Headers("Content-Type: application/json")
    @POST("api/validate_wallet_api.json")
    suspend fun newValidateOtpWalletApi(
        @Body req_body: com.bitla.ts.domain.pojo.validate_otp_wallets.request.ReqBody
    ): Response<ValidateOtpWalletsModel>

    //PickupChartCrewDetails API
    @Headers("Content-Type: application/json")

    @GET("api/cancellation_policies.json?is_from_middle_tier=true")
    suspend fun newCancellationPoliciesSummaryApi(
        @Query("api_key") apikey: String,
        @Query("locale") locale: String,
        @Query("response_format") responseFormat: Boolean
    ): Response<CancellationPoliciesServiceSummaryResponse>

    @GET("bus_operator_app/api/pick_up_chart_crew_details?is_from_middle_tier=true")
    suspend fun newPickupChartCrewDetailsApi(
        @Query("api_key") apikey: String,
        @Query("res_id") resId: String,
        @Query("locale") locale: String,
        @Query("is_encrypted") is_encrypted: Boolean?,
        @Query("coach_id") coachId: String
    ): Response<PickupChartCrewDetailsResponse>

    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/close_pickup_by_stage")
    suspend fun newCloseChartByCity(
        @Body reqBody: com.bitla.ts.domain.pojo.city_pickup_by_chart_stage.request.ReqBody
    ): Response<CityPickupChartByStageResponse>

    @Headers("Content-Type: application/json")
    @POST("api/single_page_block_unblock.json")
    suspend fun newSingleBlockUnblock(
        @Query("api_key") apikey: String,
        @Query("locale") locale: String?,
        @Query("remarks") remarks: String?,
        @Query("res_id") res_id: String,
        @Query("response_format") response_format: Boolean,
        @Query("auth_pin") auth_pin: String,
        @Query("blocking_reason") blockingReason: String?,
    ): Response<SingleBlockUnblock>


    @Headers("Content-Type: application/json")
    @GET("bus_operator_app/api/account_balance_info.json?is_from_middle_tier=true")
    suspend fun getAgentAccountBalanceInfo(
        @Query("api_key") api_key: String,
        @Query("locale") locale: String?,
        @Query("agent_id") agentId: String?,
        @Query("branch_id") branchId: String?,
    ): Response<AgentAccountInfoRespnse>
    

    @Headers("Content-Type: application/json")
    @GET("bus_operator_app/api/get_frequent_travellers_data?is_from_middle_tier=true")
    suspend fun getFrequentTravellerApi(
        @Query("api_key") apikey: String,
        @Query("res_id") resId: String,
        @Query("locale") locale: String
    ): Response<FrequentTravellerDataResponse>


    @POST("api/store_fcm_key.json?is_from_middle_tier=true")
    suspend fun newStoreFcmKey(
        @Query("api_key") api_key: String,
        @Query("device_id") deviceId: String,
        @Query("fcm_key") fcmKey: String
    ): Response<StoreFcmKey>


    //    http://siri-r6.ticketsimply.co.in/api/fetch_notification.json?api_key=7K45CSY2UZE71FFH5SGX5SUJN5WKZ1T7
    @Headers("Content-Type: application/json")
    @GET("/api/fetch_notification.json?")
    suspend fun newFetchNotification(
        @Query("api_key") Apikey: String,
        @Query("pagination") pagination: Boolean,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("filter_type") filter_type: String,
        @Query("day") current_day: Int,
        @Query("read_type") read_type: Int,
    ): Response<FetchNotificationModel>

    @Headers("Content-Type: application/json")
    @POST("ts/api/operator_response.json")
    suspend fun notificationDetails(
        @Header("Authorization") Authorization: String,
        @Header("Apikey") Apikey: String,
        @Body notificationDetailsRequest: NotificationDetailsRequest
    ): Response<GetNotificationDetails>


    //    http://gotour-stg.ticketsimply.co.in/api/get_push_noti_details.json?api_key=AOJAQXQ2FOSVUI6DBA5786F4KBAY0BZC&notification_id=29020
    @Headers("Content-Type: application/json")
    @GET("api/get_push_noti_details.json")
    suspend fun newNotificationDetails(
        @Query("api_key") api_key: String,
        @Query("notification_id") notification_id: Int
    ): Response<GetNotificationDetails>

    @Headers("Content-Type: application/json")
    @POST("ts/api/operator_response.json")
    suspend fun updateNotification(
        @Header("Authorization") Authorization: String,
        @Header("Apikey") Apikey: String,
        @Body updateNotificationRequest: UpdateNotificationRequest
    ): Response<UpdateNotificationModel>

    @GET("api/occupancy_calender.json?is_from_middle_tier=true")
    suspend fun newOccupancyCalendar(
        @Query("api_key") apikey: String,
        @Query("end_date") end_date: String,
        @Query("reservation_id") reservation_id: String,
        @Query("start_date") start_date: String,
    ): Response<OccupancyCalendarResponse>


    @Headers("Content-Type: application/json")
    @GET("api2/get_bp_dp_service_details/{reservation_id}.json?is_from_middle_tier=true")
    suspend fun newBpDpService(
        @Path("reservation_id") reservationId: String,
        @Query("api_key") Apikey: String,
    ): Response<BpDpServiceResponse>

    // Your bus API
    @GET("http://platform.yourbus.in/processGPSV2.php?")
    fun yourBus(
        @Query("acc_key") accKey: String,
        @Query("gps_id") gpsId: String,
        @Query("llt1") llt1: String,
    ): Call<YourBus>

    @Headers("Content-Type: application/json")
    @POST("api/log_creation?format=json")
    fun locationLogs(
        @Body locationLogRequest: LocationLogRequest
    ): Call<LocationLogs>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/fetch_crew_check_list")
    suspend fun newFetchCrewCheckList(
        @Body crewToolKitRequest: com.bitla.ts.domain.pojo.crew_toolkit.request.ReqBody
    ): Response<CrewToolKIt>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/update_crew_check_list?is_from_middle_tier=true&operator_api_key=Bitla@123")
    suspend fun newUpdateCrewCheckList(
        @Query("api_key") apikey: String,
        @Query("locale") locale: String,
        @Body updateCrewRequest: com.bitla.ts.domain.pojo.crew_update.request.ReqBody
    ): Response<UpdateCrew>

    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/stuff_goods_image_delete?is_from_middle_tier=true&operator_api_key=Bitla@123\"")
    suspend fun newDeleteCrewImage(
        @Query("locale") locale: String,
        @Body crewDeleteImageRequest: com.bitla.ts.domain.pojo.crew_delete_image.request.ReqBody
    ): Response<CrewDeleteImage>


    // upload crew image
    @Multipart
    @POST("bus_operator_app/api/stuff_goods_image_upload?is_from_middle_tier=true&operator_api_key=Bitla@123")
    suspend fun newUploadCrewImage(
        @Query("api_key") api_key: String,
        @Query("locale") locale: String,
        @Part("format") format: RequestBody,
        @Part("reservation_id") res_id: RequestBody,
        @Part("stuff_goods_id") stuff_goods_id: RequestBody,
        @Part("stuff_goods_image_id") stuff_goods_image_id: RequestBody,
        @Part stuff_goods_image: MultipartBody.Part
    ): Response<CrewUploadImage>


    // allotedService_new_api
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

    // viewSummaaaary
    @GET("bus_operator_app/api/view_summary?")
    @Headers("Content-Type: application/json")
    suspend fun viewSummaryApi(
        @Query("is_group_by_hubs") is_group_by_hubs: Boolean,
        @Query("hub_id") hub_id: Int?,
        @Query("api_key") Apikey: String,
        @Query("travel_date") travel_date: String,
        @Query("is_from_middle_tier") is_from_middle_tier: Boolean,
        @Query("view_summary") view_summary: Boolean,
        @Query("origin") origin: Int?,
        @Query("destination") destination: Int?,
        @Query("locale") locale: String?
    ): Response<ViewSummaryResonse>

    //Redelcom Impression Api
    @Headers("Content-Type: application/json")
    @POST("v2/impresion")
    fun apiRedelcomPrint(
        @Header("X-Authentication") content_type: String,
        @Body reqBody: ReqBodyPrint
    ): Call<ResponseBody>

    @GET("api/get_redelcom_pg_status.json?is_from_middle_tier=true&operator_api_key=BITLA@123")
    suspend fun apiGetRedelcomPgStatus(
        @Query("pnr_number") pnr_number: String,
        @Query("terminal_id") terminal_id: String,
        @Query("api_key") api_key: String,
        @Query("locale") locale: String,
    ): Response<ResponseBodyPG>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/get_auto_discount_details")
    suspend fun getCouponDiscountDetails(
        @Body getCouponDiscountRequest: GetCouponDiscountRequest
    ): Response<GetCouponDetailResponse>


    @GET("api/confirm_ticket_for_pay_at_bus.json?")
    @Headers("Content-Type: application/json")
    fun confirmPayAtBUs(
        @Query("ticket_number") ticket_number: String,
        @Query("api_key") Apikey: String,
        @Query("locale") locale: String?
    ): Call<PayAtBusResponse>


    @GET("/bus_operator_app/api/rut_based_auto_discount_details.json?")
    @Headers("Content-Type: application/json")
    suspend fun rutDiscountDetails(
        @Query("seat_number") seat_number: String,
        @Query("reservation_id") reservation_id: String,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("rut_number") rut_number: String,
        @Query("no_of_seats") no_of_seats: Int,
        @Query("date") date: String,
        @Query("api_key") api_key: String,
        @Query("is_from_middle_tier") is_from_middle_tier: Boolean
    ): Response<RutDiscountResponse>


    @GET("/bus_operator_app/api/get_prefill_passenger_details.json?")
    @Headers("Content-Type: application/json")
    suspend fun getPrefillPassenger(
        @Query("card_number") card_number: String,
        @Query("card_type") card_type: Int,
        @Query("seat_number") seat_number: String,
        @Query("api_key") api_key: String,
        @Query("locale") locale: String?,
        @Query("is_from_middle_tier") is_from_middle_tier: Boolean
    ): Response<JsonElement>

//    http://chilestg-r5.ticketsimply.us/bus_operator_app/api/get_prefill_passenger_details?card_number=32132132-1&
// card_type=7&
// seat_number=27&api_key=TWHGVP1PUOK702XMMM5E3FWV3IHTF8UM&operator_api_key=BITLA@123&locale=en

    // Release partial booked Ticket
    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/release_partial_booked_tickets.json?")
    fun releasePartialBookedTicket(
        @Query("pnr_number") pnr_number: String,
        @Query("api_key") api_key: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("locale") locale: String,
    ): Call<ReleasePartialBookedTicket>

    // Pay pending amount
    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/pay_pending_amount?")
    fun payPendingAmount(
        @Query("api_key") api_key: String,
        @Query("operator_api_key") operator_api_key: String,
        @Query("locale") locale: String,
        @Body payPendingAmountRequest: PayPendingAmountRequest
    ): Call<PayPendingAmount>


    @Headers("Content-Type: application/json")
    @GET("bus_operator_app/api/alloted_service_with_date_change")
    suspend fun getAllottedServicesWithDateChange(
        @Query("api_key") apiKey: String,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("hub_id") hubId: String?,
        @Query("is_group_by_hubs") isGroupByHubs: Boolean,
        @Query("view_mode") viewMode: String,
        @Query("locale") locale: String,
        @Query("is_from_middle_tier") isFromMiddleTier: Boolean

    ): Response<AllotedServicesResponseModel>

    @Headers("Content-Type: application/json")
    @GET("api/coach_list.json")
    suspend fun getCoachList(
        @Query("api_key") apiKey: String,
        @Query("route_id") routeId: String
    ) : Response<CoachListResponse>

    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/update_boarded_status")
    suspend fun vanChartStatus(
        @Query("api_key") apiKey: String,
        @Query("pnr_number") pnr_number: String,
        @Query("seat_number") seat_number: String,
        @Query("is_pickup_van") is_pickup_van: Boolean,
        @Query("locale") locale: String,
        @Body vanChartStatusChangeRequest: VanChartStatusChangeRequest
    ): Response<UpdateBoardedStatusResponseModel>


    @Headers("Content-Type: application/json")
    @GET("bus_operator_app/api/get_pickup_van_pickup_chart")
    suspend fun getPickupVanChart(
        @Query("api_key") apiKey: String,
        @Query("schedule_id") schedule_id: String,
        @Query("locale") locale: String,
        @Query("is_encrypted") is_encrypted: Boolean,

    ): Response<JsonElement>

    @Headers("Content-Type: application/json")
    @GET("api/validate_ts_app_vpa_phonepe.json")
    suspend fun getPhonePeDirectValidateUpiId(
        @Query("api_key") apiKey: String?,
        @Query("vpa") vpa: String?,
    ): Response<PhonePeDirectValidateUpiIdResponse>

    @Headers("Content-Type: application/json")
    @POST("api/upi_phonepe_direct_txn_status.json")
    suspend fun getPhonePeDirectTransactionStatus(
        @Body() phonePeDirectUPITransactionStatusRequest: PhonePeDirectUPITransactionStatusRequest
    ): Response<PhonePeDirectUPITransactionStatusResponse>


    @Headers("Content-Type: application/json")
    @POST("api/phonepe_direct_upi_for_app.json")
    suspend fun getPhonePeDirectUPIForApp(
        @Query("api_key") apiKey: String?,
        @Body() phonePeDirectUPIForAppRequest: PhonePeDirectUPIForAppRequest
    ): Response<PhonePeDirectUPIForAppResponse>

    @Headers("Content-Type: application/json")
    @POST("api/pinelab_payment_tranx_status.json")
    suspend fun pinelabPaymentStatusApi(
        @Body reqBody: ReqBodyPinelab
    ): Response<PinelabTransactionResponse>


    @Headers("Content-Type: application/json")
    @POST("api/shortroute_citypair.json?is_from_middle_tier=true")
    suspend fun shortRouteCityPair(
        @Query("api_key") apiKey: String,
        @Query("reservation_id") reservationId: String,
    ): Response<ShortRouteCityPairApiResponse>


    //    DUMMY API CALL
    @GET("link.php?")
    fun dummy(@Query("q") q: String): Call<GetNotificationDetails>


    @GET("api/get_multihop_seat_details.json")
    suspend fun getMultiStationSeatDataApi(
        @Query("api_key") apiKey: String?,
        @Query("reservation_id") resId: String?,
        @Query("seat_number") seatNumber: String?,
        @Query("is_bima") isBima: Boolean,
        @Query("locale") locale: String
    ): Response<MultistationRespBody>

    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/phone_block_temp_to_permanent.json")
    suspend fun getPhoneBlockTempToPermanent(
        @Body reqBody: com.bitla.ts.domain.pojo.phone_block_temp_to_permanent_data.request.PhoneBlockTempToPermanentReq
    ): Response<PhoneBlockTempToPermanentResponse>

    @Headers("Content-Type: application/json")
    @POST("api/drag_drop_remarks_update.json")
    suspend fun dragDropRemarksUpdate(
        @Body reqBody: com.bitla.ts.domain.pojo.drag_drop_remarks_update.request.DragDropRemarksUpdateRequest
    ): Response<DragDropRemarksUpdateResponse>

    @GET("api/show_transaction.json?is_from_middle_tier=true")
    suspend fun showTransactionList(
        @Query("api_key") apikey: String,
        @Query("list_type") listType: String,
        @Query("agent_id") agentId: String,
        @Query("branch_id") branchId: String,
        @Query("from_date") fromDate: String,
        @Query("to_date") toDate: String,
        @Query("category") category: String,
        @Query("page") pageNo: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("pagination") pagination: Boolean? = null,
        @Query("locale") locale: String?
    ): Response<ShowTransactionListResponse>

    @GET("api/get_transaction_pdf_url.json?is_from_middle_tier=true")
    suspend fun getTransactionPdfUrlResponse(
        @Query("api_key") apikey: String,
        @Query("list_type") listType: String,
        @Query("agent_id") agentId: String,
        @Query("branch_id") branchId: String,
        @Query("from_date") fromDate: String,
        @Query("to_date") toDate: String,
        @Query("category") category: String,
        @Query("locale") locale: String?
    ): Response<GetTransactionPdfUrlResponse>

    @GET("api/show_transaction_info.json?is_from_middle_tier=true")
    suspend fun transactionInfo(
        @Query("api_key") apikey: String,
        @Query("transaction_no") transactionNo: String,
        @Query("from_date") fromDate: String,
        @Query("to_date") toDate: String,
        @Query("locale") locale: String?
    ): Response<TransactionInformationResponse>

    @Headers("Content-Type: application/json")
    @POST("api/update_account_status.json")
    suspend fun updateAccountStatus(
        @Body reqBody: UpdateAccountStatusRequest
    ): Response<UpdateAccountStatusResponse>

    @GET("api/search_transaction_data.json?is_from_middle_tier=true")
    suspend fun manageTransactionSearch(
        @Query("api_key") apikey: String,
        @Query("list_type") listType: String,
        @Query("search") search: String,
        @Query("agent_id") agentId: String,
        @Query("branch_id") branchId: String,
        @Query("category") category: String,
        @Query("from_date") fromDate: String,
        @Query("to_date") toDate: String,
        @Query("locale") locale: String?
    ): Response<ManageTransactionSearchResponse>

    @GET("v1/api/occupancy_datewise.json?")
    suspend fun getOccupancyDateWise(
        @Query("api_key") apikey: String,
        @Query("date") date: String,
        @Query("route_id") routeId: String
        ): Response<OccupancyDateWiseResponse?>

    @GET("v1/api/active_inactive_services")
    suspend fun getActiveInactiveServices(
        @Query("api_key") apikey: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<ActiveInactiveServicesResponse>

    @POST("v1/api/activate_deactivate_service")
    suspend fun activateDeactivateServices(
        @Query("api_key") apikey: String,
        @Body activateDeactivateServiceRequest: ActivateDeactivateServiceRequest
    ): Response<ActivateDeactivateServiceResponse?>


    @GET("v1/api/get_hub_list.json?")
    suspend fun hubListApi(
        @Query("api_key") apiKey: String,
    ): Response<UserListModel>

    @POST("v1/api/campaigns_and_promotions_discount/{reservation_id}.json?is_from_middle_tier=true&json_format=true")
    suspend fun campaignsAndPromotionsDiscountApi(
        @Path("reservation_id") reservationId: String?,
        @Query("api_key") api_key: String?,
        @Query("operator_api_key") operator_api_key: String?,
        @Query("locale") locale: String?,
        @Query("origin_id") origin_id: String?,
        @Query("destination_id") destination_id: String?,
        @Query("boarding_at") boardingAt: String?,
        @Query("drop_off") dropOff: String?,
        @Body campaignsAndPromotionsDiscountRequest: com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.request.ReqBody?
    ): Response<CampaignsAndPromotionsDiscountResponse>


    @GET("api/quota_blocking_information.json?is_from_middle_tier=true")
    suspend fun quotaBlockingTooltipInfoApi(
        @Query("api_key") apikey: String,
        @Query("res_id") resId: String,
        @Query("seat_no") seatNumber: String,
        @Query("locale") locale: String
    ): Response<QuotaBlockingTooltipInfoResponse>

    @Headers("Content-Type: application/json")
    @POST("api/ezetap_payment_tranx_status.json")
    suspend fun ezetapPaymentStatusApi(
        @Body reqBody: ReqBodyEzetapStatus
    ): Response<EzetapTransactionResponse>

    @GET("api/routewise_ratecard_list.json?is_from_middle_tier")
    suspend fun fetchShowRareCardApi(
        @Query("api_key") apikey: String,
        @Query("route_id") routeId: String,
        @Query("locale") locale: String
    ): Response<FetchShowRateCardResponse>

    @GET("api/fetch_route_wise_fare.json?is_from_middle_tier")
    suspend fun fetchRouteWiseFareDetailsApi(
        @Query("api_key") apikey: String,
        @Query("route_id") routeId: String,
        @Query("locale") locale: String
    ): Response<FetchRouteWiseFareResponse>

    @GET("api/route_wise_fare.json?is_from_middle_tier")
    suspend fun viewRareCardApi(
        @Query("api_key") apikey: String,
        @Query("rate_card_id") rateCardId: String,
        @Query("locale") locale: String
    ): Response<ViewRateCardResponse>


    @GET("api/delete_rate_card.json?is_from_middle_tier")
    suspend fun deleteRareCardApi(
        @Query("api_key") apikey: String,
        @Query("rate_card_id") rateCardId: String,
        @Query("locale") locale: String
    ): Response<DeleteRateCardResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/create_rate_card")
    suspend fun createRateCardApi(
        @Body reqBodyCreateRateCard: CreateRateCardReqBody
    ): Response<CreateRateCardResponse>


    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/update_rate_card")
    suspend fun editRateCardApi(
        @Body reqBodyEditRateCard: EditRateCardReqBody
    ): Response<EditRateCardResponse>


    @Headers("Content-Type: application/json")
    @GET("api/get_agent_transaction_details.json?is_from_middle_tier=true")
    suspend fun getAgentTransactionDetailsApi(
        @Query("api_key") apikey: String,
        @Query("agent_amount") agentAmount: String,
        @Query("locale") locale: String,
    ): Response<GetAgentRechargeResponse>

    @Headers("Content-Type: application/json")
    @GET("api/get_agent_pg_detail.json")
    suspend fun getAgentPGDetail(
        @Query("api_key") apikey: String,
        @Query("amount") agentAmount: String,
        @Query("pay_gay_type") pgType: String,
        @Query("native_app_type") nativeAppType: Int,
    ): Response<AgentPGDataResponse>

    @Headers("Content-Type: application/json")
    @POST("apis/hermes/pg/v1/pay")
    suspend fun getPhonepePayPageTransactionStatus(
        @Header("X-VERIFY") xVerify: String,
        @Body body: RequestBody
    ): Response<PhonePeResponse>

    @Headers("Content-Type: application/json")
    @GET("/bus_operator_app/api/phonepe_direct_txn_status.json")
    suspend fun getPhonePeTransStatus(
       @Query("pnr_number") pnr_number: String,
       @Query("api_key") api_key: String,
    ): Response<PhonePeStatusResponse>

    // Your bus API
    @GET("http://platform.yourbus.in/ci/getLastGPSByOPSvcId?")
    fun yourBusFetchBusLocation(
        @Query("api_travel_id") apiTravelId: String,
        @Query("route_id") routeId: String,
    ): Call<YourBusLocation>

    @Headers("Content-Type: application/json")
    @POST("api/update_print_count.json")
    suspend fun updatePrintCountApi(
       @Query("pnr_number") pnr_number: String,
       @Query("is_update_print_count") isUpdatePrintCount: Boolean,
       @Query("api_key") api_key: String,
       ): Response<PhonePeStatusResponse>

    @Headers("Content-Type: application/json")
    @GET("/api/get_paybitla_agent_txn_status.json")
    suspend fun getPayBitlaTransStatus(
       @Query("pnr_number") pnr_number: String,
    ): Response<AgentPGDataResponse>



    @GET("bus_operator_app/api/restaurant_list?is_from_middle_tier=true")
    suspend fun getRestaurantListApi(
        @Query("api_key") apikey: String
    ): Response<RestaurantListResponse>


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

    @GET("api/reservation_stages.json?is_from_middle_tier=true&json_format=true")
    suspend fun getReservationStagesApi(
        @Query("reservation_id") reservationId: String,
        @Query("api_key") apiKey: String,
        @Query("operator_api_key") operatorApiKey: String,
        @Query("locale") locale: String,
    ): Response<ReservationStagesResponse>

    @Headers("Content-Type: application/json")
    @GET("api/boarding_stage_seats/{reservation_id}.json?is_from_middle_tier=true&json_format=true")
    suspend fun getBoardingStageSeatsApi(
        @Path("reservation_id") reservationId: String,
        @Query("origin_id") originId: String,
        @Query("destination_id") destinationId: String,
        @Query("api_key") apiKey: String,
        @Query("operator_api_key") operatorApiKey: String,
        @Query("locale") locale: String,
        @Query("app_bima_enabled") appBimaEnabled: Boolean,
        @Query("boarding_id") boardingId: String,
    ): Response<ServiceDetailsModel>
    @Headers("Content-Type: application/json")
    @POST("api/update_coach_layout.json")
    suspend fun updateCoachTypeApi(
        @Body body: UpdateCoachTypeRequest
    ): Response<UpdateCoachType>



    @Headers("Content-Type: application/json")
    @GET("/api/cities.json?is_from_middle_tier=true")
    suspend fun getCitiesList(
        @Query("api_key") apiKey: String,
        @Query("response_format") responseFormat: String,
        @Query("locale") locale: String,
    ): Response<CitiesListResponse>

    @Headers("Content-Type: application/json")
    @GET("/api/route_list/{origin_id}/{dest_id}.json?is_from_middle_tier=true")
    suspend fun getRouteList(
        @Path("origin_id") origin: String,
        @Path("dest_id") dest_id:String,
        @Query("api_key") apiKey: String,
        @Query("response_format") responseFormat: String,
        @Query("locale") locale: String,
        @Query("filter_value") filterValue: String,
        @Query("page") page: String,
        @Query("per_page") perPage: String,
        @Query("search") search: String,
        @Query("filter_type") filterType: String,
    ): Response<RouteListResponse>

    @Headers("Content-Type: application/json")
    @GET("/v1/api/get_hub_list.json?")
    suspend fun getHubDropDownList(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("response_format") responseFormat: String,
    ): Response<HubDropdownResponse>

    @Headers("Content-Type: application/json")
    @GET("/v1/api/get_coach_types.json?")
    suspend fun getCoachTypeList(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("response_format") responseFormat: String,
    ): Response<CoachTypeListResponse>

    @Headers("Content-Type: application/json")
    @POST("/v1/api/city_pairs.json?is_from_middle_tier=true")
    suspend fun getCityPair(
        @Query("api_key") apiKey: String,
        @Query("response_format") responseFormat: String,
        @Query("locale") locale: String,
        @Body reqCitiesBody : Any
    ): Response<CityPairResponse>


    @Headers("Content-Type: application/json")
    @GET("/v1/api/stage_details.json?is_from_middle_tier=true")
    suspend fun getStageList(
        @Query("city_id") cityId: String,
        @Query("api_key") apiKey: String,
        @Query("operator_api_key") operatorApiKey: String,
        @Query("response_format") responseFormat: String,
        @Query("locale") locale: String,
        @Query("route_id") routeId: String
    ): Response<StageListResponse>

    @Headers("Content-Type: application/json")
    @POST("/v1/api/activate_deactivate_route?")
    suspend fun activateDeactivateRoute(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("response_format") responseFormat: String,
        @Body activateDeactivateRouteReqBody: Any

    ): Response<ActivateDeactivateResponse>


    @Headers("Content-Type: application/json")
    @POST("/v1/api/create_route?")
    suspend fun createRoute(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("response_format") responseFormat: String,
        @Body TypecreateRouteRequestBody: Any
    ): Response<CreateRouteResponse>


    @Headers("Content-Type: application/json")
    @POST("/v1/api/update_route?")
    suspend fun updateRoute(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("response_format") responseFormat: String,
        @Query("route_id") routeId: String,
        @Body updateRouteRequestBody: JsonObject?
    ): Response<UpdateRouteResponse>

    @Headers("Content-Type: application/json")
    @GET("/v1/api/get_route_data?")
    suspend fun getRouteData(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("response_format") responseFormat: String,
        @Query("route_id") routeId: String,
    ): Response<GetRouteResponse>

    @Headers("Content-Type: application/json")
    @POST("/v1/api/modify_route?")
    suspend fun modifyRoute(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("response_format") responseFormat: String,
        @Query("route_id") routeId: String,
        @Query("step") step: String,
        @Body modifyRouteRequestBody: Any
    ): Response<ModifyRouteResponse>

    @Headers("Content-Type: application/json")
    @POST("/v1/api/delete_stage")
    suspend fun deleteStage(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("operator_api_key") operatorApiKey: String,
        @Query("response_format") responseFormat: String,
        @Body ReqBody: Any,
    ): Response<DeleteStageResponse>


    @Headers("Content-Type: application/json")
    @GET("/v1/api/duplicate_route?")
    suspend fun duplicateService(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("operator_api_key") operatorApiKey: String,
        @Query("response_format") responseFormat: String,
        @Query("route_id") routeId: String,
    ): Response<DuplicateServiceResponse>


    @Headers("Content-Type: application/json")
    @GET("/v1/api/preview_route?")
    suspend fun previewRoute(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("route_id") routeId: String,
    ): Response<PreviewRouteResponse>

    @Headers("Content-Type: application/json")
    @POST("/v1/api/create_route_stages.json")
    suspend fun createStagesApi(
        @Query("api_key") apiKey: String,
        @Query("locale") locale: String,
        @Query("response_format") responseFormat: String,
        @Query("route_id") routeId: String,
        @Body reqBody: Any
    ): Response<CreateStageResponse>



    @Headers("Content-Type: application/json")
    @POST("api/paytm_pos_txn_status.json")
    suspend fun paytmPosTxnStatusApi(
        @Body paytmPosTxnStatusRequest: PaytmPosTxnStatusRequest
    ): Response<BookTicketFullResponse>

    @GET("bus_operator_app/api/get_checklist_questions")
    suspend fun getCheckListQuestions(
        @Query("api_key") apiKey: String,
        @Query("res_id") resId: String,
    ): Response<SelfAuditQuestionResponse>

    @Headers("Content-Type: application/json")
    @POST("bus_operator_app/api/dispatcher_form_audit_submit")
    suspend fun selfAuditFormSubmitApi(
       @Body selfAuditFormSubmitRequest: SubmitSelfAuditFormRequest
    ): Response<SubmitSelfAuditFormResponse>

    @Headers("Content-Type: application/json")
    @POST("api/update_trip_status.json")
    suspend fun updateTripStatusApi(
       @Body reqBody: Any
    ): Response<UpdateTripResponse>





    @GET("/v1/api/shift_to_services?is_from_middle_tier=true")
    suspend fun getMergeBusShiftToServices(
        @Query("origin_id") origin_id: String,
        @Query("destination_id") destination_id: String,
        @Query("travel_date") travel_date: String,
        @Query("api_key") api_key: String,
        @Query("response_format") response_format: String,
        @Query("locale") locale: String,
        @Query("old_res_id") old_res_id: String,
    ): Response<ShiftToServicesListResponse>

    @GET("/v1/api/recommended_seats?is_from_middle_tier=true&json_format=true&operator_api_key=BITLA@123")
    suspend fun getMergeBusRecommendedSeats(
        @Query("api_key") apiKey: String?,
        @Query("res_id") resId: String?,
        @Query("pnr_number") pnrNumber: String?,
        @Query("origin_id") originId: String?,
        @Query("destination_id") destinationId: String?,
        @Query("exclude_passenger_details") excludePassengerDetails: Boolean?,
        @Query("locale") locale: String?,
    ): Response<RecommendedSeatsResponse>

    @GET("/v1/api/merge_service_details?is_from_middle_tier=true&json_format=true&operator_api_key=BITLA@123")
    suspend fun getMergeServiceDetails(
        @Query("api_key") apiKey: String?,
        @Query("res_id") resId: String?,
        @Query("origin_id") originId: String?,
        @Query("destination_id") destinationId: String?,
        @Query("exclude_passenger_details") excludePassengerDetails: Boolean?,
        @Query("locale") locale: String?,
    ): Response<com.example.buscoach.service_details_response.ServiceDetailsModel>


    @POST("/v1/api/merge_bus_shift_passenger")
    suspend fun mergeBusShiftPassenger(
        @Query("api_key") apiKey: String?,
        @Body mergeBusShiftPassengerRequest: MergeBusShiftPassengerRequest
    ): Response<MergeBusShiftPassengerResponse>

    @POST("/v1/api/merge_bus_seat_mapping")
    suspend fun mergeBusSeatMapping(
        @Query("api_key") apiKey: String?,
        @Body mergeBusSeatMappingRequest: MergeBusSeatMappingRequest
    ): Response<MergeBusSeatMappingResponse>


    @POST("/v1/api/add_bp_dp_to_service")
    suspend fun addBpDpToService(
        @Body addBpDpRequest: AddBpDpToServiceRequest
    ): Response<AddBpDpToServiceResponse>

    @GET("/v1/api/get_destination_list.json")
    suspend fun getDestinationList(
        @Query("api_key") apiKey: String?,
        @Query("reservation_id") reservationId: String?
    ): Response<GetDestinationListResponse>


    @GET("v1/api/get_destination_list.json?is_from_middle_tier=true")
    suspend fun getDestinationListWithOrigin(
        @Query("api_key") api_key: String,
        @Query("origin_id") originId: String
    ): Response<DestinationList>

    @GET("v1/api/get_multi_hop_seat_details")
    suspend fun getMultiStationSeatDataApiMergeBus(
        @Query("api_key") apiKey: String?,
        @Query("res_id") resId: String?,
        @Query("seat_number") seatNumber: String?,
        @Query("is_bima") isBima: Boolean,
    ): Response<com.example.buscoach.multistation_data.MultiStationRespBody>

    @GET("bus_operator_app/api/blocking_reason_list")
    suspend fun getServiceBlockReasonsList(
        @Query("api_key") apiKey: String,
    ): Response<ServiceBlockReasonListResp>

    @GET("bus_operator_app/api/available_service_list?is_from_middle_tier=true")
    suspend fun availableServiceList(
        @Query("origin_id") originId: String,
        @Query("destination_id") destinationId: String,
        @Query("travel_date") travelDate: String,
        @Query("api_key") apiKey: String,
        @Query("pagination") pagination: Boolean,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
    ): Response<AvailableRoutesModel>

    @GET("bus_operator_app/api/get_seat_types")
    suspend fun getSeatTypes(
        @Query("api_key") apiKey: String,
        @Query("route_ids") routeIds: String,
        @Query("origin_id") originId: String?,
        @Query("destination_id") destinationId: String?,
        @Query("travel_date") travelDate: String?,
    ): Response<SeatTypesResponse>

    @POST("bus_operator_app/api/multiple_services_manage_fares")
    suspend fun multipleServicesManageFares(
        @Body reqBody: com.bitla.ts.domain.pojo.multiple_services_manage_fare.request.ReqBody,
    ): Response<com.bitla.ts.domain.pojo.multiple_services_manage_fare.response.Response>

    @GET("api/fetch_credit_info.json")
    suspend fun fetchCreditInfo(
        @Query("api_key") apiKey: String,
    ): Response<CreditInfoResponse>

    @GET("bus_operator_app/api/fetch_luggage_details")
    suspend fun fetchLuggageDetailsIntlApi(
        @Query("api_key") apiKey: String,
        @Query("pnr_number") pnrNumber: String
    ): Response<FetchLuggageDetailsResponse>

    @POST("bus_operator_app/api/update_luggage_details")
    suspend fun updateLuggageOptionIntlApi(
        @Body reqBody: com.bitla.ts.domain.pojo.luggage_details.request.ReqBody
    ) : Response<LuggageOptionsDetailsResponse>

    @GET("/api/tracking/asset_wise_service_details?")
    fun getAssetWiseServiceDetails(
        @Query("r_asset_id") assetId: String,
        @Query("api_key") apiKey: String
    ): Call<TrackingoResponse>

    @GET("api/get_phonepe_v2_transaction_status.json")
    suspend fun getPhonePeV2Status(
        @Query("api_key") apiKey: String,
        @Query("order_id") orderId: String
    ): Response<PhonePeV2StatusResponse>

    @GET("tickets/do_conpay_for_agent")
    suspend fun phonePeV2RechargeSuccessConPay(
        @Query("is_phone_pe_v2") isPhonePeV2: Boolean,
        @Query("pnr_number") pnrNumber: String
    ): Response<String>

    @GET("tickets/confirm_phonepev2_pending_seats")
    suspend fun confirmPhonePeV2PendingSeat(
        @Query("pnr_number") pnrNumber: String
    ): Response<String>
}