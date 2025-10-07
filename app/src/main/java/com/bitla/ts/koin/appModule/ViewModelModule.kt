package com.bitla.ts.koin.appModule

import AddRateCardSingleViewModel
import SingleViewModel
import com.bitla.ts.presentation.viewModel.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.dsl.*

val ViewModelModule = module {

    viewModel { DomainViewModel(get()) }

    viewModel { LoginViewModel(get()) }

    viewModel { IvrCallingViewModel<Any?>(get()) }
    viewModel { PaymentMethodViewModel() }


    viewModel { BranchWiseRevenueViewModel<Any?>(get()) }


    viewModel { ForgotPasswordViewModel(get()) }

    viewModel { AvailableRoutesViewModel<Any?>(get()) }

    viewModel { DestinationPairViewModel(get()) }

    viewModel { BookTicketViewModel<Any?>(get()) }

    viewModel { TicketDetailsViewModel<Any?>(get()) }

    viewModel { RecentSearchViewModel<Any?>(get()) }

    viewModel { BlockViewModel<Any?>(get()) }

    viewModel { AllPassengersViewModel<Any?>(get()) }

    viewModel { SmsTypesViewModel<Any?>(get()) }

    viewModel { ResetPasswordViewModel<Any?>(get()) }

    viewModel { SharedViewModel<Any?>(get()) }

    viewModel { PrivilegeDetailsViewModel(get()) }

    viewModel { CancelTicketViewModel<Any?>(get()) }

    viewModel { PickUpChartViewModel<Any?>(get()) }

    viewModel { CityDetailViewModel<Any?>(get()) }

    viewModel { MyBookingsViewModel<Any?>(get()) }

    viewModel { ServiceOccupancyViewModel<Any?>(get()) }

    viewModel { PassengerHistoryViewModel<Any?>(get()) }

    viewModel { ExtendFareViewModel<Any?>(get())}

    viewModel { ValidateCouponViewModel<Any?>(get()) }

    viewModel { BookingOptionViewModel<Any?>(get()) }

    viewModel { EditChartViewModel<Any?>(get()) }

    viewModel { AgentRechargeViewModel<Any?>(get()) }

    viewModel { ServiceStageViewModel<Any?>(get()) }

    viewModel { ShiftPassengerViewModel<Any?>(get()) }

    viewModel { EtaViewModel<Any?>(get()) }

    viewModel { MoveToExtraSeatViewModel<Any?>(get()) }

    viewModel { AgentAccountInfoViewModel<Any?>(get()) }

    viewModel { CrewToolKitViewModel<Any?>(get()) }

    viewModel { RedelcomViewModel<Any?>(get()) }

    viewModel { PhonePeUpiDirectViewModel<Any?>(get()) }

    viewModel { PassengerDetailsViewModel<Any?>() }

    viewModel { ManageAccountViewModel<Any?>(get()) }

    viewModel { TicketDetailsComposeViewModel<Any?>(get()) }
    viewModel { BookingSummaryViewModel<Any?>(get()) }
    viewModel { StagingSummaryViewModel<Any?>(get()) }
    viewModel { OccupancyGridViewModel<Any?>(get()) }
    viewModel { BlackListViewModel<Any?>(get()) }

    viewModel { MergeBusSharedViewModel(get())}

    single { SingleViewModel<Any?>() }

    single { AddRateCardSingleViewModel<Any?>() }

    viewModel { AddRateCardViewModel<Any?>(get()) }
    viewModel { MergeBusShiftPassengerViewModel<Any?>(get()) }

    viewModel { BusTrackingViewModel<Any?>() }
    viewModel { SelfAuditViewModel(get()) }
    viewModel { CoachLayoutReportingViewModel(get()) }
    viewModel { UpdateCoachTypeViewModel<Any?>(get()) }
    viewModel { RouteManagerViewModel<Any?>(get()) }
    viewModel { MoveQuotaBlockSeatViewModel<Any?>(get()) }

    viewModel { MultipleServicesManageFareViewModel(get()) }
}