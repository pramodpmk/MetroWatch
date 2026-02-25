package com.fungames.core.station.di

import com.fungames.core.station.data.StationRepositoryImpl
import com.fungames.core.station.data.TripRepositoryImpl
import com.fungames.core.station.domain.ContactsUseCase
import com.fungames.core.station.domain.MetroRoutesUseCase
import com.fungames.core.station.domain.ParkingInfoUseCase
import com.fungames.core.station.domain.PlanTripUseCase
import com.fungames.core.station.domain.StationListUseCase
import com.fungames.core.station.domain.StationRepository
import com.fungames.core.station.domain.TripRepository
import com.fungames.core.station.domain.WaterMetroRoutesUseCase
import com.fungames.core.station.domain.WaterMetroStationsUseCase
import com.fungames.core.station.presentation.StationViewModel
import com.fungames.core.station.presentation.contact.ContactsViewModel
import com.fungames.core.station.presentation.metroroutes.MetroRoutesViewModel
import com.fungames.core.station.presentation.parking.ParkingViewModel
import com.fungames.core.station.presentation.picker.StationPickerViewModel
import com.fungames.core.station.presentation.plantrip.PlanTripViewModel
import com.fungames.core.station.presentation.watermetro.WaterMetroRoutesViewModel
import com.fungames.core.station.presentation.watermetro.WaterMetroStationsViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val stationModule = module {
    singleOf(::StationRepositoryImpl) bind StationRepository::class
    singleOf(::StationListUseCase)
    singleOf(::WaterMetroStationsUseCase)
    singleOf(::WaterMetroRoutesUseCase)
    singleOf(::MetroRoutesUseCase)
    singleOf(::ParkingInfoUseCase)
    singleOf(::ContactsUseCase)
    viewModelOf(::StationViewModel)
    viewModelOf(::StationPickerViewModel)
    factoryOf(::TripRepositoryImpl) bind TripRepository::class
    factoryOf(::PlanTripUseCase)
    viewModelOf(::PlanTripViewModel)
    viewModelOf(::ParkingViewModel)
    viewModelOf(::WaterMetroStationsViewModel)
    viewModelOf(::WaterMetroRoutesViewModel)
    viewModelOf(::MetroRoutesViewModel)
    viewModelOf(::ContactsViewModel)
}
