package com.metrowatch.kochi.station.di

import com.metrowatch.kochi.station.data.StationRepositoryImpl
import com.metrowatch.kochi.station.data.TripRepositoryImpl
import com.metrowatch.kochi.station.domain.ContactsUseCase
import com.metrowatch.kochi.station.domain.MetroRoutesUseCase
import com.metrowatch.kochi.station.domain.ParkingInfoUseCase
import com.metrowatch.kochi.station.domain.PlanTripUseCase
import com.metrowatch.kochi.station.domain.StationListUseCase
import com.metrowatch.kochi.station.domain.StationRepository
import com.metrowatch.kochi.station.domain.TripRepository
import com.metrowatch.kochi.station.domain.WaterMetroRoutesUseCase
import com.metrowatch.kochi.station.domain.WaterMetroStationsUseCase
import com.metrowatch.kochi.station.presentation.StationViewModel
import com.metrowatch.kochi.station.presentation.contact.ContactsViewModel
import com.metrowatch.kochi.station.presentation.metroroutes.MetroRoutesViewModel
import com.metrowatch.kochi.station.presentation.parking.ParkingViewModel
import com.metrowatch.kochi.station.presentation.picker.StationPickerViewModel
import com.metrowatch.kochi.station.presentation.plantrip.PlanTripViewModel
import com.metrowatch.kochi.station.presentation.watermetro.WaterMetroRoutesViewModel
import com.metrowatch.kochi.station.presentation.watermetro.WaterMetroStationsViewModel
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
