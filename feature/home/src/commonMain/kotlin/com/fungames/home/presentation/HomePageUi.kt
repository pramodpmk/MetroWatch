package com.fungames.home.presentation

data class HomePageUi(
    val locationText: String,
    val locationLatitude: Double,
    val locationLongitude: Double,
    val nearestStation: NearestStation,
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
                locationLatitude = 0,
                locationLongitude = 0
            ),
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
