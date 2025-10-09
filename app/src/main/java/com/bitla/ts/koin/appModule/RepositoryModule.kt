package com.bitla.ts.koin.appModule

import com.bitla.ts.domain.repository.*
import org.koin.dsl.module

val RepositoryModule = module {

    single { DomainRepository(get()) }

    single { LoginRepository(get()) }

    single { BranchWiseRevenueRepository(get()) }

    single { DashboardRepository(get()) }

    single { ForgotPasswordRepository(get()) }

    single { IvrCallingRepository(get()) }


    single { AvailableRoutesRepository(get()) }

    single { BookTicketRepository(get()) }

    single { TicketDetailsRepository(get()) }

    single { RecentSearchRepository(get()) }

    single { BlockRepository(get()) }

    single { AllPassengersRepository(get()) }

    single { SmsTypesRepository(get()) }

    single { ServiceOccupancyRepository(get()) }

    single { ResetPasswordRepository(get()) }

    single { PrivilegeRepository(get()) }

    single { CancelTicketRepository(get()) }

    single { MyBookingsRepository(get()) }

    single { ServiceStageRepository(get()) }

    single { PassengerHistoryRepository(get()) }

    single { SharedRepository(get()) }

    single { PickUpRepository(get()) }

    single { CityDetailRepository(get()) }

    single { ExtendFareRepository(get()) }

    single { CouponRepository(get()) }

    single { BookingRepository(get()) }

    single { EditChartRepository(get()) }

    single { BranchRechargeRepository(get()) }

    single { ShiftPassengerRepository(get()) }

    single { EtaRepository(get()) }

    single { StarredReportsRepository(get()) }

    single { MoveToExtraSeatRepository(get()) }

    single { AllReportsRepository(get()) }

    single { AgentAccountInfoRepository(get()) }

    single { CrewToolKitRepository(get()) }

    single { RedelcomRepository(get()) }

    single { VehicleDetailsRepository(get()) }

    single { PhonePeUpiDirectRepository(get()) }

    single { ManageAccountRepository(get()) }
    single { DashboardRevenueRepository(get()) }

    single { OccupancyGridRepository(get()) }
    single { StagingSummaryRepository(get()) }
    single { BookingSummaryRepository(get()) }
    single { BlackListRepository(get()) }
    single { AddRateCardRepository(get()) }
    single { RestaurantRepository(get()) }
    single { CoachLayoutReportingRepository(get()) }
    single { UpdateCoachTypeRepository(get()) }
    single { RouteManagerRepository(get()) }
    single { SelfAuditRepository(get()) }
    single { MergeBusShiftRepository(get()) }
    single { MoveQuotaBlockSeatRepository(get()) }

    single { MultipleServicesManageFareRepository(get()) }
}


