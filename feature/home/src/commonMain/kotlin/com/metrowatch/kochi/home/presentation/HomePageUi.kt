package com.metrowatch.kochi.home.presentation

data class HomePageUi(
    val locationText: String,
    val locationLatitude: Double,
    val locationLongitude: Double,
    val nearestStation: NearestStation,
    val nearestStationAvailable: Boolean,
    val stationList: List<NearestStation>,
    val pageState: PageState
) {

    companion object {

        fun initData() = HomePageUi(
            locationText = "",
            locationLatitude = 0.0,
            locationLongitude = 0.0,
            nearestStation = NearestStation(
                id = 0,
                stationName = "",
                stationCode = "",
                distanceToStation = "",
                nextTrainTo = "",
                nextTrainTime = "",
                line = "",
                stationId = "",
                locationLatitude = 0,
                locationLongitude = 0
            ),
            nearestStationAvailable = false,
            stationList = emptyList(),
            pageState = PageState.Loading
        )
    }
}

enum class PageState {
    Loading,
    Success,
    Error
}
